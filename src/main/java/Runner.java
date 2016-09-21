import utilities.database._HSQLDB;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by jarndt on 9/19/16.
 */
public class Runner {
    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        /*_HSQLDB.executeQuery("insert into player(player_name,alliance_name,player_link,alliance_link,player_rank,player_status)" +
                " values("+
                "testplayer"+","+
                "testalliance"+","+
                "testplayerlink"+","+
                "getAllianceLink"+","+
                "getPlayerRank"+","+
                "getPlayerStatus"+
                ");");
        */
//        IntStream.iterate(1, i -> i + 1).limit(9).parallel().forEach(a->IntStream.iterate(1, i -> i + 1).limit(499).parallel()
//                .forEach(b -> {
//                    try {
//                        new OgniterGalaxyParser().parseUniverse(398, a, b);
//                        System.out.println("DONE WITH: "+a+" : "+b+" : *");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }));

//        for(int i = 1; i<10; i++)
//            for(int j = 1; j<500; j++)
//                new OgniterGalaxyParser().parseUniverse(398,i,j);

        for(Map<String, Object> a : _HSQLDB.executeQuery("select * from player p JOIN planet t ON p.player_name = t.player_name where player_status in ('I','i')"))
            System.out.println(a);
//        new OgniterGalaxyParser().parseUniverse(398,1,1);
        _HSQLDB.getInstance().db.stopDBServer();
        System.exit(0);

        /*
        Initialize.login("s129-en.ogame.gameforge.com","unsupo","supersmash")
                .clickOnResearch().clickOnEnergyTechnology().clickOnStartWithDM(); //500 DM
        Thread.sleep(Utility.getInProgressTime());

        new Research().clickOnCombustionDrive().clickOnStartWithDM();
        Thread.sleep(Utility.getInProgressTime());

        new Research().clickOnCombustionDrive().clickOnStartWithDM();
        Thread.sleep(Utility.getInProgressTime());

        System.out.println(Utility.getInProgressTime());
        Thread.sleep(Utility.getInProgressTime());
        new Facilities().clickOnShipyardBuilding().clickOnStartWithDM();
        System.out.println(Utility.getInProgressTime());
        Thread.sleep(Utility.getInProgressTime());
        new Facilities().clickOnShipyardBuilding().clickOnStartWithDM();
        System.out.println(Utility.getInProgressTime());
        Thread.sleep(Utility.getInProgressTime());
        */
    }

}
