package ogame.pages;

/**
 * Created by jarndt on 9/19/16.
 */
public class Resources extends OGamePage{
    public static final String ID = "ref";
    public static final String RESOURCES = "Resources";
    public static final String  METAL_MINE              = "Metal Mine",
                                CRYSTAL_MINE            = "Crystal Mine",
                                DUETERIUM_SYNTHESIZER   = "Deuterium Synthesizer",
                                SOLAR_PLANET            = "Solar Plant",
                                FUSION_REACTOR          = "Fusion Reactor",
                                METAL_STORAGE           = "Metal Storage",
                                CRYSTAL_STORAGE         = "Crystal Storage",
                                DUETERIUM_TANK          = "Dueterium Tank";

    @Override
    public String getPageLoadedConstant() {
        return null;
    }
}
