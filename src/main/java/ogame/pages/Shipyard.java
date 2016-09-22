package ogame.pages;

import ogame.utility.Initialize;
import utilities.selenium.UIMethods;

import java.util.concurrent.TimeUnit;

/**
 * Created by jarndt on 9/19/16.
 */
public class Shipyard extends AbstractOgamePage{
    @Override
    public String getPageLoadedConstant() {
        return null;
    }

    public static final String ID = "id";

    private Action performAction(String constant){
        String webName = Initialize.getBuildableByName(constant).getWebName();
        UIMethods.clickOnAttributeAndValue(ID,webName);
        UIMethods.waitForText(constant,30, TimeUnit.SECONDS);
        return new Action();
    }


    public void clickOnSmallCargo() {
    }
}
