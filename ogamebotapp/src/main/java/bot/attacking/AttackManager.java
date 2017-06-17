package bot.attacking;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ogame.objects.game.Coordinates;
import ogame.objects.game.data.Server;
import ogame.objects.game.messages.CombatMessage;
import ogame.objects.game.messages.EspionageMessage;
import utilities.database.Database;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 6/14/17.
 */
public class AttackManager {
    private Server server;
    private int ogameUserId;
    private long points;
    private int rank;
    private Coordinates mainPlanet;
    private String name;
    private HashMap<String,Target> targetHashMap = new HashMap<>();

    private transient Database d;
    private Database getDatabase() throws SQLException, IOException, ClassNotFoundException {
        if(d == null)
            d = Database.newDatabaseConnection();
        return d;
    }

    public AttackManager(String username, Server server, int ogameUserId, int points, int rank, Coordinates coordinates) {
        this.server = server;
        this.ogameUserId = ogameUserId;
        this.points = points;
        this.rank = rank;
        mainPlanet = coordinates;
        this.name = username;
    }

    public AttackManager setRankPoints(int rank, int points) {
        this.rank = rank;
        this.points = points;
        return this;
    }

    public long getPoints() throws SQLException, IOException, ClassNotFoundException {
        if(points <= 0) {
            List<Map<String, Object>> v = getDatabase().executeQuery(
                    queryPoints.replace(serverR, server.getServerID() + "").replace(nameR, name)
            );
            if(v!=null && v.size() >0 && v.get(0)!=null && v.get(0).size()>0) {
                points = (long) v.get(0).get("score");
                rank = (int) v.get(0).get("position");
            }
            return points;
        }
        return points;
    }

    public List<Target> getEspionageTargets() throws SQLException, IOException, ClassNotFoundException {
        //TODO better espionage targets?
        return getBlindAttackTargets();
    }

    public List<Target> getBlindAttackTargets() throws SQLException, IOException, ClassNotFoundException {
        List<Map<String, Object>> v = getDatabase().executeQuery(
                queryPlayers.replace(serverR, server.getServerID() + "").replace(scoreR, getPoints() + "")
        );
        List<Target> targets;
        if(v!=null && v.size() >0 && v.get(0)!=null && v.get(0).size()>0) {
            List<Target> coords = v.stream().map(a ->new Target(server,new Coordinates(a.get("coords").toString()))).collect(Collectors.toList());
            for(Target t : coords)
                if(!targetHashMap.containsKey(t.getCoordinates().getStringValue()))
                    targetHashMap.put(t.getCoordinates().getStringValue(),t);
        }
        targets = new ArrayList<>(targetHashMap.values()).stream().filter(a->a.getLastAttack().plusHours(1).isBefore(LocalDateTime.now())).collect(Collectors.toList());
        Collections.sort(targets,(a,b)->new Integer(a.getCoordinates().getDistance(mainPlanet)).compareTo(b.getCoordinates().getDistance(mainPlanet)));

        //TODO remove targets who have defenses ie targets who's combat reports have a failed attack mission (or draw)
        v = getDatabase().executeQuery("select * from combat_messages where attacker_status in ('draw') and server_id = "+server.getServerID());
        if(v!=null && v.size() >0 && v.get(0)!=null && v.get(0).size()>0) {
            List<Target> coords = v.stream().map(a -> new Target(server, new CombatMessage(a))).collect(Collectors.toList());
            ArrayList<Target> remove = new ArrayList<>(), add = new ArrayList<>();
            for(Target t : coords)
                for(Target tt : targets)
                    if(tt.getCoordinates().equals(t.getCoordinates())) {
                        remove.add(tt);
                        if(!t.hasDefense())
                            add.add(t);
                    }
            targets.removeAll(remove);
            targets.addAll(add);
        }

        v = getDatabase().executeQuery("select * from espionage_messages where max_info >= 2 and server_id = "+server.getServerID());
        if(v!=null && v.size() >0 && v.get(0)!=null && v.get(0).size()>0) {
            List<Target> coords = v.stream().map(a -> new Target(server, (EspionageMessage)new Gson().fromJson(a.get("json_esp_object").toString(),new TypeToken<EspionageMessage>(){}.getType()))).collect(Collectors.toList());
            ArrayList<Target> remove = new ArrayList<>(), add = new ArrayList<>();
            for(Target t : coords)
                for(Target tt : targets)
                    if(tt.getCoordinates().equals(t.getCoordinates())) {
                        remove.add(tt);
                        if(!t.hasDefense())
                            add.add(t);
                    }
            targets.removeAll(remove);
            targets.addAll(add);
        }

        return targets;
    }

    public List<Target> getAttackTargets(){
        //TODO
        return new ArrayList<>();
    }

    public List<Target> getSafeAttackTargets() throws SQLException, IOException, ClassNotFoundException {
        //TODO targets from espioange and combat reports
        List<Map<String, Object>> v = getDatabase().executeQuery("select * from espionage_messages where max_info >= 2 and server_id = " + server.getServerID());
        List<Target> targets = new ArrayList<>();
        if(v!=null && v.size() >0 && v.get(0)!=null && v.get(0).size()>0) {
            targets.addAll(v.stream()
                    .map(a ->
                            new Target(server,
                                    (EspionageMessage)new Gson().fromJson(a.get("json_esp_object").toString(),
                                            new TypeToken<EspionageMessage>(){}.getType())
                            )
                    )
                    .filter(a -> {
                        try {
                            return !a.hasDefense();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    })
                    .collect(Collectors.toList())
            );

        }
        return  targets;
    }

    public List<Target> getRecyclerTargets(){
        //TODO recycler targets
        return  new ArrayList<>();
    }

    private String serverR = "[SERVER]", scoreR = "[SCORE]", nameR = "[NAME]";
    //this query gets the players who are inactive (I or i) who have a score between 5*yourScore and 1/5*yourScore
    private String
            queryPlayers = "" +
            "select * from planet where server_id = "+serverR+" and (coords,timestamp) in (\n" +
            "    select coords,max(timestamp)\n" +
            "    \tfrom planet where server_id = "+serverR+" and player_id in (\n" +
            "            select p.player_id from player p, player_highscore h\n" +
            "                where p.player_id = h.player_id and p.timestamp = h.player_t\n" +
            "                    and h.type = '0'\n" +
            "                    and status in ('I','i')\n" +
            "                    and p.server_id = "+serverR+"\n" +
            "                    and p.timestamp = (select timestamp from player where server_id = "+serverR+" order by timestamp desc limit 1)\n" +
            "                    and score > "+scoreR+"/5 and score < "+scoreR+" * 5\n" +
            "                order by h.position desc\n" +
            "    \t\t) group by coords)\n" +
            "order by coords;",

            queryPoints = "" +
                    "select position,score from player p, player_highscore h \n" +
                    "\twhere p.player_id = h.player_id and p.timestamp = h.player_t\n" +
                    "    \tand p.server_id = "+serverR+"\n" +
                    "        and h.type = '0'\n" +
                    "        and (p.player_id,p.timestamp) in (\n" +
                    "            \tselect player_id, timestamp from player \n" +
                    "            \t\twhere server_id = "+serverR+" and name = '"+nameR+"' \n" +
                    "            \t\torder by timestamp desc limit 1\n" +
                    "            );"
    ;

}
