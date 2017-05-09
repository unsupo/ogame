package ogame.pages;

import ogame.objects.Resource;

/**
 * Created by jarndt on 5/8/17.
 */
public class Resources {
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

    public static String[] names = {
            METAL_MINE, CRYSTAL_MINE, DUETERIUM_SYNTHESIZER,
            SOLAR_PLANET, FUSION_REACTOR,
            METAL_STORAGE, CRYSTAL_STORAGE,DUETERIUM_TANK
    };

    static {
        baseCosts = Resource.convertCosts(new long[] {
                60,15,0,10,
                48,24,0,10,
                225,75,0,20,
                75, 30, 0,0,
                900, 360, 180,0,
                1000, 0, 0,0,
                1000,500, 0,0,
                1000, 1000, 0,0
        });
    }

    public static Resource[] baseCosts;
}
