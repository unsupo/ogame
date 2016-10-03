package objects.messages;

import objects.Coordinates;
import ogame.utility.Resource;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utilities.Utility;
import utilities.database._HSQLDB;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 9/30/16.
 */
public class EspionageMsg implements IMessage{
    String player;
    Coordinates targetCoordinates;
    String activityType;
    String activityString;
//    LocalDateTime activity; //TODO date of time since last active
    Resource resources;
    int lootPercent;
    int counterEspionagePercent;
    long fleets = -1, defence = -1;

    LocalDateTime msgDate;

    /**
     *
     *
     * @param li_msg Jsoup.parse(v).select("li.msg")
     */
    public EspionageMsg(LocalDateTime msgDate, Element li_msg){
        this.msgDate = msgDate;

        String[] split = li_msg.select("span.msg_title > a").text().split("\\[");
        if(split.length != 2)
            throw new IllegalArgumentException("Not an espionage message");
        targetCoordinates = new Coordinates(split[1]);
        Element msg_contents = li_msg.select("span.msg_content").get(0);
        Elements compacting = msg_contents.select("div.compacting");

        Elements playerCompacting = compacting.get(0).select("span");
        player = playerCompacting.get(1).text().trim();
        activityType = playerCompacting.get(2).text();
        try {
            activityString = playerCompacting.get(3).text();
        }catch (Exception e){
            e.printStackTrace();
        }
        Elements lootDataCompacting = compacting.get(1).select("span > span");
        long metal = parseNumber(lootDataCompacting.get(0));
        long crystal = parseNumber(lootDataCompacting.get(1));
        long dueterium = parseNumber(lootDataCompacting.get(2));
        resources = new Resource(metal,crystal,dueterium);

        Elements lootPercentageCompacting = compacting.get(2).select("span");
        lootPercent = Integer.parseInt(lootPercentageCompacting.get(0).text().trim().replaceAll("[^0-9]",""));
        counterEspionagePercent = Integer.parseInt(lootPercentageCompacting.get(1).text().trim().replaceAll("[^0-9]",""));

        Elements lootFleetDefenseCompacting = compacting.get(3).select("span");
        try {
            switch (lootFleetDefenseCompacting.size()) {
                case 2:
                    fleets = parseNumber(lootFleetDefenseCompacting.get(0));
                    defence = parseNumber(lootFleetDefenseCompacting.get(1));
                    Utility.markAsDontAttack(targetCoordinates, 1, fleets, defence);
                    break;
                case 1:
                    fleets = parseNumber(lootFleetDefenseCompacting.get(0));
                    Utility.markAsDontAttack(targetCoordinates, 1, fleets);
                    break;
                case 0:
                    Utility.markAsDontAttack(targetCoordinates, 1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void writeToDatabase(int universeID) throws IOException, SQLException {
        List<String> names = Arrays.asList(("coords,msgDate,activityType," +
                "activityString,metal,crystal,deuterium,lootPercent,counterEspionagePercent, fleets,defence,universe_id").split(","));
        String query = "INSERT INTO ESPIONAGE_REPORTS({NAMES}) VALUES(";
        query = query.replace("{NAMES}",names.stream().collect(Collectors.joining(",")));
        query+="'"+getTargetCoordinates().getStringValue()+"','";
        query+=msgDate.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()+"','";
        query+=activityType+"','"+activityString+"','"+resources.metal+"','"+resources.crystal+"','"+resources.deuterium+"','";
        query+=lootPercent+"','"+counterEspionagePercent+"','"+fleets+"','"+defence+"','"+universeID+"');";
        try {
            _HSQLDB.executeQuery(query);
        }catch (Exception e){
            if (!e.getMessage().contains("unique constraint")) {
                System.err.println("FAILED QUERY: " + query);
                throw e;
            }
        }
    }

    public static Long parseNumber(Element e){
        String v = e.text().trim().replace("Metal: ","").replace("M","000000");
        return Long.parseLong(v.replaceAll("[^0-9]",""));
    }public static Long parseNumber(String e){
        String v = e.replace("Metal: ","").replace("M","000000");
        return v.trim().isEmpty()?0:Long.parseLong(v.replaceAll("[^0-9]",""));
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Coordinates getTargetCoordinates() {
        return targetCoordinates;
    }

    public void setTargetCoordinates(Coordinates targetCoordinates) {
        this.targetCoordinates = targetCoordinates;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityString() {
        return activityString;
    }

    public void setActivityString(String activityString) {
        this.activityString = activityString;
    }

    public Resource getResources() {
        return resources;
    }

    public void setResources(Resource resources) {
        this.resources = resources;
    }

    public int getLootPercent() {
        return lootPercent;
    }

    public void setLootPercent(int lootPercent) {
        this.lootPercent = lootPercent;
    }

    public int getCounterEspionagePercent() {
        return counterEspionagePercent;
    }

    public void setCounterEspionagePercent(int counterEspionagePercent) {
        this.counterEspionagePercent = counterEspionagePercent;
    }

    public long getFleets() {
        return fleets;
    }

    public void setFleets(long fleets) {
        this.fleets = fleets;
    }

    public long getDefence() {
        return defence;
    }

    public void setDefence(long defence) {
        this.defence = defence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EspionageMsg that = (EspionageMsg) o;

        if (lootPercent != that.lootPercent) return false;
        if (counterEspionagePercent != that.counterEspionagePercent) return false;
        if (fleets != that.fleets) return false;
        if (defence != that.defence) return false;
        if (player != null ? !player.equals(that.player) : that.player != null) return false;
        if (targetCoordinates != null ? !targetCoordinates.equals(that.targetCoordinates) : that.targetCoordinates != null)
            return false;
        if (activityType != null ? !activityType.equals(that.activityType) : that.activityType != null) return false;
        if (activityString != null ? !activityString.equals(that.activityString) : that.activityString != null)
            return false;
        return resources != null ? resources.equals(that.resources) : that.resources == null;

    }

    @Override
    public int hashCode() {
        int result = player != null ? player.hashCode() : 0;
        result = 31 * result + (targetCoordinates != null ? targetCoordinates.hashCode() : 0);
        result = 31 * result + (activityType != null ? activityType.hashCode() : 0);
        result = 31 * result + (activityString != null ? activityString.hashCode() : 0);
        result = 31 * result + (resources != null ? resources.hashCode() : 0);
        result = 31 * result + lootPercent;
        result = 31 * result + counterEspionagePercent;
        result = 31 * result + (int) (fleets ^ (fleets >>> 32));
        result = 31 * result + (int) (defence ^ (defence >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "EspionageMsg{" +
                "player='" + player + '\'' +
                ", targetCoordinates=" + targetCoordinates +
                ", activityType='" + activityType + '\'' +
                ", activityString='" + activityString + '\'' +
                ", resources=" + resources +
                ", lootPercent=" + lootPercent +
                ", counterEspionagePercent=" + counterEspionagePercent +
                ", fleets=" + fleets +
                ", defence=" + defence +
                '}';
    }
}
