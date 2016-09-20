package ogame.pages;

import ogame.utility.Initialize;
import utilities.selenium.UIMethods;

import java.util.concurrent.TimeUnit;

/**
 * Created by jarndt on 9/19/16.
 */
public class Research extends AbstractOgamePage{
    public static final String ID = "id";
    private static final String ENERGY = "Energy Technology";
    private static final String COMBUSTION = "Combustion Drive";

    @Override
    public String getPageLoadedConstant() {
        return null;
    }

    private Action performAction(String constant){
        String webName = Initialize.getBuildableByName(constant).getWebName();
        UIMethods.clickOnAttributeAndValue(ID,webName);
        UIMethods.waitForText(constant,30, TimeUnit.SECONDS);
        return new Action();
    }

    public Action clickOnEnergyTechnology() {
        return performAction(ENERGY);
    }

    public Action clickOnCombustionDrive() {
        return performAction(COMBUSTION);
    }
}
