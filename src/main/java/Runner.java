import ogame.utility.Initialize;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by jarndt on 9/19/16.
 */
public class Runner {
    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        System.out.println(Initialize.getBuildableByName("Light Fighter"));


//        Overview o = Initialize.login("s129-en.ogame.gameforge.com", "unsupo", "supersmash");
//        o.clickOnResearch().clickOnCombustionDrive().clickOnStartWithDM();
//        new Shipyard().clickOnSmallCargo();

//        _HSQLDB.executeQuery("update player set alliance_name = 'null',player_link = 'en/398/player/100003',alliance_link = 'null',player_rank = 4089,player_status = 'a' WHERE player_name = 'comagf';");

//        OgniterGalaxyParser.parseEntireUniverse(398);


//        for(int i = 1; i<10; i++)
//            for(int j = 1; j<500; j++)
//                new OgniterGalaxyParser().parseUniverse(398,i,j);
//
//        for(Map<String, Object> a : _HSQLDB.executeQuery(
//                "select * from player p JOIN planet t ON p.player_name = t.player_name " +
//                        "where player_status in ('I','i') and " +
//                            "regexp_substring(coordinates,'[0-9]+')='5'"))
//            System.out.println(a);
//        new OgniterGalaxyParser().parseUniverse(398,1,1);
//        _HSQLDB.getInstance().db.stopDBServer();
//        System.exit(0);

        /*
        Initialize.login("s129-en.ogame.gameforge.com","<username>","<password>")
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
