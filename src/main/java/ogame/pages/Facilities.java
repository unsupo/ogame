package ogame.pages;

import ogame.utility.Initialize;
import utilities.selenium.UIMethods;

import java.util.concurrent.TimeUnit;

/**
 * Created by jarndt on 9/19/16.
 */
public class Facilities extends OGamePage{
    public static final String ID = "ref";
    private static final String ROBOTICS_FACTORY = "Robotics Factory";
    private static final String RESEARCH_LAB = "Research Lab";
    private static final String SHIPYARD = "Shipyard";

    @Override
    public String getPageLoadedConstant() {
        return "Facility buildings";
    }

    private Action performAction(String constant){
        String webName = Initialize.getBuildableByName(constant).getWebName();
        UIMethods.clickOnAttributeAndValue(ID,webName);
        UIMethods.waitForText(constant,30, TimeUnit.SECONDS);
        return new Action();
    }

    public Action clickOnRoboticsFactory() {
        return performAction(ROBOTICS_FACTORY);
    }

    public Action clickOnResearchLab() {
        return performAction(RESEARCH_LAB);
    }

    public Action clickOnShipyardBuilding() {
        return performAction(SHIPYARD);
    }
}
