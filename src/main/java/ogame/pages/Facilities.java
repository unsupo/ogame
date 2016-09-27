package ogame.pages;

import ogame.utility.Initialize;
import utilities.selenium.UIMethods;

import java.util.concurrent.TimeUnit;

/**
 * Created by jarndt on 9/19/16.
 */
public class Facilities extends OGamePage{
    public static final String ID = "ref";

    public static final String FACILITIES = "Facilities";

    public static final String ROBOTICS_FACTORY    = "Robotics Factory",
                                RESEARCH_LAB        = "Research Lab",
                                SHIPYARD            = "Shipyard";

	public static final String ALLIANCE_DEPOT      = "Alliance Depot";

	public static final String MISSILE_SILO        = "Missile Silo";

	public static final String NANITE_FACTORY      = "Nanite Factory";

	public static final String TERRAFORMER         = "Terraformer";

	public static final String SPACE_DOCK          = "Space Dock";

	public static final String LUNAR_BASE          = "Lunar Base";

	public static final String SENSOR_PHALANX      = "Sensor Phalanx";

	public static final String JUMP_GATE           = "Jump Gate";

    public static final String[] names = {ROBOTICS_FACTORY, RESEARCH_LAB, SHIPYARD, ALLIANCE_DEPOT, MISSILE_SILO, NANITE_FACTORY,
                                        TERRAFORMER,SPACE_DOCK, LUNAR_BASE, SENSOR_PHALANX, JUMP_GATE};


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
