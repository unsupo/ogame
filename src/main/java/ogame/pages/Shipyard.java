package ogame.pages;

import ogame.utility.Initialize;
import utilities.Utility;
import utilities.selenium.UIMethods;

import java.util.concurrent.TimeUnit;

import objects.Ship;

/**
 * Created by jarndt on 9/19/16.
 */
public class Shipyard extends OGamePage{
    public static final String ID = "id";

    @Override
    public String getPageLoadedConstant() {
        return null;
    }


    public Action clickOnSmallCargo() {
        return Utility.clickAction(ID, Ship.SMALL_CARGO);
    }

}
