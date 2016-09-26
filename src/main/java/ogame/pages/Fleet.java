package ogame.pages;

/**
 * Created by jarndt on 9/19/16.
 */
public class Fleet extends OGamePage{
    public static final String WEB_ID_APPENDER = "ship_",
                                BUTTON_ID_WEB_APPENDER = "button";
    public static final String FLEET = "Fleet";
    public static final String ID = "id";

    @Override
    public String getPageLoadedConstant() {
        return null;
    }
}
