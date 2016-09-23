package utilities.ogame;

import objects.Coordinates;
import ogame.utility.Initialize;
import utilities.selenium.UIMethods;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by jarndt on 9/23/16.
 */
public class MissionBuilder {
    public static final String  EXPEDITION              = "missionButton15",
                                COLONIZATION            = "missionButton7",
                                RECYCLE_DEBRIS_FIELD    = "missionButton8",
                                TRANSPORT               = "missionButton3",
                                DEPLOYMENT              = "missionButton4",
                                ESPIONAGE               = "missionButton6",
                                ACS_DEFEND              = "missionButton5",
                                ATTACK                  = "missionButton1",
                                ACS_ATTACK              = "missionButton2",
                                MOON_DESTRUCTION        = "missionButton9";


    public static void attackTarget(String attackingFromPlanetName, Coordinates yourTargetsCoordinates) throws IOException {
        UIMethods.clickOnAttributeAndValue("id", Initialize.getPlanetMap().get(attackingFromPlanetName).getWebElement());

        UIMethods.clickOnText("Fleet");
        UIMethods.typeOnAttributeAndValue("id", "ship_202","9999");
        UIMethods.clickOnText("Next");


        System.out.println("Attacking: "+yourTargetsCoordinates);
        Coordinates attackTarget = yourTargetsCoordinates;//new Coordinates(results.get(0).get("COORDINATES").toString());

        UIMethods.typeOnAttributeAndValue("id","galaxy",attackTarget.getGalaxy()+"");
        UIMethods.typeOnAttributeAndValue("id","system",attackTarget.getSystem()+"");
        UIMethods.typeOnAttributeAndValue("id","position",attackTarget.getPlanet()+"");

        UIMethods.clickOnText("Next");

        UIMethods.waitForText("Select mission for target:",1, TimeUnit.MINUTES);
        UIMethods.clickOnText("Attack");//document.getElementById("missionButton1").click()
        UIMethods.clickOnText("Send fleet");
    }

}
