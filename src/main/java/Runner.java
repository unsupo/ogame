import objects.ai.AI;
import objects.ai.DefaultAI;
import objects.ai.ProfileFollower;
import ogame.pages.Merchant;
import ogame.pages.Overview;
import ogame.utility.Initialize;
import ogame.utility.QueueManager;
import utilities.Utility;
import utilities.selenium.Task;
import utilities.selenium.UIMethods;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by jarndt on 9/19/16.
 */
public class Runner {

    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
//        String v = FileOptions.readFileIntoString(Utility.RESOURCE_DIR+"test");
////        Jsoup.parse(v).select("li.list_item > a").stream().filter(e->!e.select("span").isEmpty() && e.select("span").text().contains("("))
////                .map(e->e.text())
//        Elements table = Jsoup.parse(v).select("#eventContent").select("tr");
//        for(Element e : table) {
////            System.out.println(e.select("td.icon_movement_reserve, td.icon_movement").select("span").attr("title"));
//            System.out.println(e.select("td.destCoords").text().trim());
//        }

        runAI(new ProfileFollower());
    }

    public static void runAI(AI ai) throws IOException {
        while(true){
            try {
                Task defaultTask = ai.getDefaultTask(),
                        task = ai.getTask(),
                        attackedTask = ai.getAttackedTask();

                Merchant.getItemOfDay();

                if (Utility.isBeingAttack())
                    if (attackedTask == null)
                        DefaultAI.attackedTask();
                    else
                        attackedTask.execute();

                if (task != null)
                    task.execute();

                if (defaultTask != null)
                    defaultTask.execute();

                Utility.clickOnNewPage(Overview.OVERVIEW);
                Initialize.writeToJSON();

                Thread.sleep(30000);
            }catch (Exception e){
                e.printStackTrace();
                //you got logged out
                if(UIMethods.doesPageContainAttributeAndValue("id","loginSubmit")){
                    String[] params = QueueManager.getLoginParams();
                    Initialize.justLogin(params[0],params[1],params[2]);
                }
            }
        }
    }



    //EXAMPLES

    //        System.out.println(new Ship(Ship.SMALL_CARGO).getCost());

//        Initialize.login();
//        Utility.build(Facilities.RESEARCH_LAB);


//        System.out.println(Resource.getCumulativeCost(Research.COMBUSTION,2,6));
//        Initialize.login();
//        Coordinates yourCoordinates = new Coordinates(5,400,1);
//
//        _HSQLDB.setDbName(572);
//        List<Map<String, Object>> results = _HSQLDB.executeQuery(
//                "select coordinates from player p JOIN planet t ON p.player_name = t.player_name " +
//                        "where player_status in ('I','i') and " +
//                        "regexp_substring(coordinates,'[0-9]+')='" + yourCoordinates.getGalaxy() + "'");
//
//        List<Coordinates> coords = results.stream().map(a->new Coordinates(a.get("COORDINATES").toString()))
//                .collect(Collectors.toList());
//        Collections.sort(coords,(a, b)->Integer.compare(yourCoordinates.getDistance(a),yourCoordinates.getDistance(b)));
//
//        System.out.println(coords);

//        Initialize.login();
//        Coordinates coords = Utility.getActivePlanet().getCoordinates();
//        List<Coordinates> targets = Utility.getInactiveTargets(coords);
//        new MissionBuilder().setDestination(targets.get(0))
//                .setSource(coords).setFleet(new Fleet().addShip(Ship.SMALL_CARGO,9999)).setMission(MissionBuilder.ATTACK).sendFleet();


        /*
        Initialize.login();
//        UIMethods.clickOnText("Overview");
        UIMethods.clickOnText("Fleet");
        UIMethods.typeOnAttributeAndValue("id", "ship_202","9999");
        UIMethods.clickOnText("Next");

        Coordinates coordinates = Initialize.getPlanetMap().get("Homeworld").getCoordinates();
        List<Map<String, Object>> results = _HSQLDB.executeQuery(
                "select coordinates from player p JOIN planet t ON p.player_name = t.player_name " +
                    "where player_status in ('I','i') and " +
                        "regexp_substring(coordinates,'[0-9]+')='" + coordinates.getGalaxy() + "'");

        System.out.println("Attacking: "+results.get(0));
        Coordinates attackTarget = new Coordinates(results.get(0).get("COORDINATES").toString());

        UIMethods.typeOnAttributeAndValue("id","galaxy",attackTarget.getGalaxy()+"");
        UIMethods.typeOnAttributeAndValue("id","system",attackTarget.getSystem()+"");
        UIMethods.typeOnAttributeAndValue("id","position",attackTarget.getPlanet()+"");

        UIMethods.clickOnText("Next");

//        ((JavascriptExecutor)UIMethods.getWebDriver()).executeScript("document.getElementById(\"missionButton1\").click()");
        UIMethods.waitForText("Select mission for target:",1, TimeUnit.MINUTES);
        UIMethods.clickOnText("Attack");//document.getElementById("missionButton1").click()
        UIMethods.clickOnText("Send fleet");
*/

//        QueueManager.getInstance();


//        _HSQLDB.setDbName(572);
//        QueueManager.getInstance().parseUniverse();

//    Research o = Initialize.login("s129-en.ogame.gameforge.com", "<username>", "<password>")
//            .clickOnResearch();
//    HashMap<String, Integer> requirements = Utility.getBuildableRequirements("Small Cargo Ship");
//
//    HashMap<String, Integer> researches = Initialize.getResearches();
//        for(String name : requirements.keySet())
//            if(researches.containsKey(name) && researches.get(name) < requirements.get(name))
//            for(int i = 0; i<requirements.get(name) - researches.get(name); i++) {
//        Thread.sleep(Utility.getInProgressTime());
//        o.clickOnResearchByName(name).clickOnStartWithDM();
//    }


    //        _HSQLDB.setDbName(572);
//        List<Map<String, Object>> results = _HSQLDB.executeQuery(
//                "select * from player p JOIN planet t ON p.player_name = t.player_name " +
//                        "where player_status in ('I','i') and " +
//                        "regexp_substring(coordinates,'[0-9]+')='" + "4" + "'");
//
//        System.out.println(results);
//        Overview o = Initialize.login("s129-en.ogame.gameforge.com", "<username>", "<password>");
//        o.clickOnResearch().clickOnCombustionDrive().clickOnStartWithDM();
//        new Shipyard().clickOnSmallCargo();

//        printAllRequirements("Deathstar");
//
//        requirements.forEach((a,b)-> System.out.println(a+" "+b));

//        System.out.println(Initialize.getBuildableByName("Deathstar").getRequires());



//        Overview o = Initialize.login("s129-en.ogame.gameforge.com", "<username>", "<password>");
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
