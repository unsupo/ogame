package ogame.objects.game.fleet;

import bot.Bot;
import ogame.objects.game.Coordinates;
import ogame.objects.game.Ship;
import ogame.pages.Fleet;
import ogame.pages.Research;
import org.openqa.selenium.By;
import utilities.webdriver.DriverController;
import utilities.webdriver.JavaScriptFunctions;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by jarndt on 5/31/17.
 */
public class Mission {
    public static final String
            ATTACKING       = "Attacking",
            DEPLOYMENT      = "Deplyoment",
            ESPIONAGE       = "Espionage",
            TRANSPORT       = "Transport",
            COLONIZATION    = "Colonization",
            HARVESTING      = "Harvesting",
            DESTROY         = "Destroy",
            EXPEDITION      = "Expedition",

            OWN_FLEET       = "Own Fleet",
            FOREIGN_FLEET   = "Foreign Fleet";

    private int espionageMission = 6,
            missileAttack = 10,
            expedition = 15,
            colonization = 7,
            recycleDebrisField = 8,
            transport = 3,
            deployment = 4,
            acsDefend = 5,
            attack = 1,
            acsAttack = 2,
            moonDestruction = 9;

    private int type = 1;
    //attack https://s135-en.ogame.gameforge.com/game/index.php?page=fleet1&amp;galaxy=8&amp;system=307&amp;position=11&amp;type=1&amp;mission=1

    public void sendFleet(FleetObject fleetObject, Bot b) throws IOException {
        b.getPageController().goToPage(Fleet.FLEET);
        if(b.getFleetInfo().getFleetsRemaining() == 0) {
            System.out.println("No fleet slots");
            return;
        }
        if(fleetObject.getMission().equals(Mission.COLONIZATION) && b.canGetAnotherPlanet(fleetObject.getToCoordinates())){
            System.out.println("Astrophysics too low to colonize");
            return;
        }
        if(fleetObject.getShips().get(Ship.ESPIONAGE_PROBE) < 1 && fleetObject.getMission().equals(Mission.ESPIONAGE)){
            System.out.println("No probes in mission, can't go on an espionage mission");
            return;
        }if(fleetObject.getShips().get(Ship.ESPIONAGE_PROBE) == 1 && fleetObject.getShips().size() == 1 && fleetObject.getMission().equals(Mission.ESPIONAGE))
            sendProbe(b.getDriverController(),fleetObject.getToCoordinates());

        String shipIdR = "[SHIP_ID]", shipCountR = "[SHIP_COUNT]";
        String jsSelectShip = "toggleMaxShips(\"#shipsChosen\", "+shipIdR+","+shipCountR+"); ",
                jsSubmit    = "checkShips(\"shipsChosen\"); trySubmit();";
        StringBuilder page1 = new StringBuilder("");
        for(Map.Entry<String, Integer> s : fleetObject.getShips().entrySet())
            page1.append(
                    jsSelectShip
                            .replace(shipIdR,Ship.getShipByName(s.getKey()).getShipID().replace("am",""))
                            .replace(shipCountR,s.getValue()+"")
            );
        page1.append(jsSubmit);

        DriverController d = b.getDriverController();
        d.executeJavaScript(page1.toString());
        d.waitForElement(By.xpath("//*[@id='mission']"),1L, TimeUnit.MINUTES);


        Coordinates c = fleetObject.getToCoordinates();
        JavaScriptFunctions.fillFormByXpath(d,"//*[@id='galaxy']",c.getGalaxy()+"");
        JavaScriptFunctions.fillFormByXpath(d,"//*[@id='system']",c.getSystem()+"");
        JavaScriptFunctions.fillFormByXpath(d,"//*[@id='position']",c.getPlanet()+"");

        d.executeJavaScript("trySubmit();");
        d.waitForElement(By.xpath("//*[@id='fleetStatusBar']"),1L, TimeUnit.MINUTES);




    }


    private String missionR = "[MISSION]", galaxyR = "[GALAXY]", systemR = "[SYSTEM]", planetR = "[PLANET]", typeR = "[TYPE]", countR = "[COUNT]";
    private String sendShips = "" +
            "sendShipsWithPopup(\n" +
            "                "+missionR+",\n" + //mission
            "                "+galaxyR+",\n" + //galaxy
            "                "+systemR+",\n" + //system
            "                "+planetR+",\n" + //planet
            "                "+typeR+",\n" + //type
            "                "+countR+" \n" +//count
            "        ); return";


    public void sendProbe(DriverController controller, Coordinates coordinates) {
        sendProbe(controller,coordinates,1);
    }public void sendProbe(DriverController controller, Coordinates coordinates, int count){
        controller.executeJavaScript(
                sendShips.replace(missionR,espionageMission+"")
                        .replace(galaxyR,coordinates.getGalaxy()+"")
                        .replace(systemR,coordinates.getSystem()+"")
                        .replace(planetR,coordinates.getPlanet()+"")
                        .replace(typeR,type+"")
                        .replace(countR,count+"")
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// STATIC STUFF ///////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getMission(String s) {
        if(s.equals("Attack") || s.equals("Attack (R)"))
            return ATTACKING;
        if(s.contains("Espionage"))
            return ESPIONAGE;

        return null;
    }
    public static String getFleetType(String s) {
        if(s.equals("Own fleet"))
            return OWN_FLEET;

        return null;
    }
}
