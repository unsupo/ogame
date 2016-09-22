package utilities.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utilities.Utility;
import utilities.database._HSQLDB;
import utilities.filesystem.FileOptions;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by jarndt on 9/21/16.
 */
public class OgniterGalaxyParser {

    public static final String  UNIVERSE_REGX   = "{universe}",
                                GALAXY_REGX     = "{galaxy}",
                                SYSTEM_REGX     = "{system}";

    public static final String UNIVERSE_LINK = "http://en.ogniter.org/en/"+UNIVERSE_REGX+"/galaxy/"+GALAXY_REGX+"/"+SYSTEM_REGX;

    private int retryCount = 0;
    public void parseUniverse(int universe, int galaxy, int system) throws IOException {
        String link = getLink(universe,galaxy,system);
        Elements doc = null;
        try {
            doc = Jsoup.connect(link).timeout(5000).get().select("table").get(0).select("tr");
        }catch (Exception e){
            if(retryCount++ <= 100)
                parseUniverse(universe,galaxy,system);
            else {
                e.printStackTrace();
                System.out.println("FAILED [" + galaxy + ":" + system + ":*] LINK: " + link);
            }
            return;
        }
        List<String> columnNames = new ArrayList<>();
        List<PlanetPlayer> planetPlayers = new ArrayList<>();
        int index = 0;
        for(Element e : doc){
            if(index++ == 0)
                parseColumnNames(columnNames, e);
            else
                planetPlayers.add(new PlanetPlayer(e));
        }

        planetPlayers.forEach(a->insertPlanetPlayer(a));

    }

    private void insertPlanetPlayer(PlanetPlayer planetPlayer) {
        if(planetPlayer.getPlanetName() == null)
            return;
        if(planetPlayer.getPlanetName().trim().isEmpty())
            return;


        String planetQuery = null, playerQuery = null;
        try {
            playerQuery = "insert into player(player_name,alliance_name,player_link,alliance_link,player_rank,player_status)" +
                    " values("+
                            "'"+planetPlayer.getPlayerName()+"',"+
                            "'"+planetPlayer.getAllianceName()+"',"+
                            "'"+planetPlayer.getPlayerLink()+"',"+
                            "'"+planetPlayer.getAllianceLink()+"',"+
                            ""+planetPlayer.getPlayerRank()+","+
                            "'"+planetPlayer.getPlayerStatus()+
                    "');";


            _HSQLDB.executeQuery(playerQuery);

        } catch (Exception e){
            if(e.getMessage().contains("unique constraint")){
                playerQuery = "update player set " +
                            "alliance_name = " + "'"+planetPlayer.getAllianceName()+"',"+
                            "player_link = " + "'"+planetPlayer.getPlayerLink()+"',"+
                            "alliance_link = " + "'"+planetPlayer.getAllianceLink()+"',"+
                            "player_rank = " + ""+planetPlayer.getPlayerRank()+","+
                            "player_status = " + "'"+planetPlayer.getPlayerStatus()+"'"+
                                " WHERE player_name = '"+planetPlayer.getPlayerName()+"'"+
                        ";";


                try {
                    _HSQLDB.executeQuery(playerQuery);
                } catch (Exception e1){
                    e1.printStackTrace();
                    System.out.println(playerQuery);
                }
            }
            else {
                e.printStackTrace();
                System.out.println(playerQuery);
            }
        }

        try {
            planetQuery = "insert into planet(player_name,planet_name,moon_name,moon_size,coordinates)" +
                            " values("+
                            "'"+planetPlayer.getPlayerName()+"',"+
                            "'"+planetPlayer.getPlanetName()+"',"+
                            "'"+planetPlayer.getMoonName()+"',"+
                            "'"+planetPlayer.getMoonSize()+"',"+
                            "'"+planetPlayer.getCoordinates().getCoordinates()+
                            "');";
            _HSQLDB.executeQuery(planetQuery);

        } catch (Exception e){
            if(e.getMessage().contains("unique constraint")){
                planetQuery = "update planet set " +
                        "planet_name = " + "'"+planetPlayer.getPlanetName()+"',"+
                        "moon_name = " + "'"+planetPlayer.getMoonName()+"',"+
                        "moon_size = " + "'"+planetPlayer.getMoonSize()+"',"+
                        "coordinates = " + "'"+planetPlayer.getCoordinates().getCoordinates()+
                            " WHERE player_name = '"+planetPlayer.getPlayerName()+
                                "' and planet_name = " + "'"+planetPlayer.getPlanetName()+"'"+
                        ";";


                try {
                    _HSQLDB.executeQuery(playerQuery);
                } catch (Exception e1){
                    e1.printStackTrace();
                    System.out.println(planetQuery);
                }
            }
            else {
                e.printStackTrace();
                System.out.println(planetQuery);
            }
        }
    }

    private void parseColumnNames(List<String> columnNames, Element e) {
        for(Element el : e.select("th"))
            columnNames.add(el.text());
    }

    private String getLink(int universe, int galaxy, int system) {
        return UNIVERSE_LINK.replace(UNIVERSE_REGX,universe+"")
                            .replace(GALAXY_REGX,galaxy+"")
                            .replace(SYSTEM_REGX,system+"");
    }

    private static class Counter{
        private int doneCount = 0;
        public synchronized void increment(){
            doneCount++;
        }

        public int getDoneCount() {
            return doneCount;
        }
    }
    public static void parseEntireUniverse(int universe){ //398
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            String lastUpdate = FileOptions.readFileIntoString(Utility.LAST_UPDATE);
            LocalDateTime dateTime = LocalDateTime.from(f.parse(lastUpdate));

            if(dateTime.plusDays(1).isBefore(LocalDateTime.now()))
                return;

        } catch (IOException e) {
        }

        final Counter c = new Counter();
        IntStream.iterate(1, i -> i + 1).limit(9).parallel().forEach(a->IntStream.iterate(1, i -> i + 1).limit(499).parallel()
                .forEach(b -> {
                    try {
                        new OgniterGalaxyParser().parseUniverse(universe, a, b);
                        c.increment();
                        System.out.println("DONE WITH: "+a+":"+b+":* \tTotal Left: "+(499*9 - c.getDoneCount()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));

        try {
            FileOptions.writeToFileOverWrite(Utility.LAST_UPDATE,LocalDateTime.now().format(f));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //OTHER OPTION IS ExecutorService
//        ExecutorService executor = Executors.newFixedThreadPool(Utility.MAX_THREAD_COUNT);
//        for(final Counter c1 = new Counter(); c1.getDoneCount() < 9; c1.increment())
//            for(final Counter c2 = new Counter(); c2.getDoneCount() < 499; c2.increment())
//                executor.execute(()->{
//                try {
//                    int a = c1.getDoneCount(), b = c2.getDoneCount();
//                    new OgniterGalaxyParser().parseUniverse(universe, a,b);
//                    c.increment();
//                    System.out.println("DONE WITH: "+c1.getDoneCount()+":"+b+":* \tTotal Left: "+(499*9 - c.getDoneCount()));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//        executor.shutdown();
//        try {
//            executor.awaitTermination(5, TimeUnit.MINUTES);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
