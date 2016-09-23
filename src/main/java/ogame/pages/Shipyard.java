package ogame.pages;

import objects.Ship;
import utilities.Utility;

/**
 * Created by jarndt on 9/19/16.
 */
public class Shipyard extends OGamePage{
    public static final String ID = "id", WEB_ID_APPENDER = "details";

    public static final String SHIPYARD = "Shipyard";

    @Override
    public String getPageLoadedConstant() {
        return null;
    }


    public Action clickOnSmallCargo() {
        return Utility.clickAction(ID, Ship.SMALL_CARGO);
    }

}
