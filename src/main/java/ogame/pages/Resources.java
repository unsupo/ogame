package ogame.pages;

import ogame.utility.Resource;

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

    public static final String[] names = {
            METAL_MINE, CRYSTAL_MINE, DUETERIUM_SYNTHESIZER,
            SOLAR_PLANET, FUSION_REACTOR,
            METAL_STORAGE, CRYSTAL_STORAGE,DUETERIUM_TANK
    };

    public static final Resource[] baseCosts = Resource.convertCosts(new long[] {
            60, 15, 0,0,
            48, 24, 0,0,
            225, 75, 0,0,
            75, 30, 0,0,
            900, 360, 180,0,
            1000, 0, 0,0,
            1000,500, 0,0,
            1000, 1000, 0,0
    });


    @Override
    public String getPageLoadedConstant() {
        return null;
    }
}
