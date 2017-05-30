package ogame.pages;

/**
 * Created by jarndt on 5/8/17.
 */
public class Research {
    public static void main(String[] args) {
        String techs = "\n" +
                "  ROCKET_LAUNCHERS              INT DEFAULT 0,\n" +
                "  LIGHT_LASERS                  INT DEFAULT 0,\n" +
                "  HEAVY_LASERS                  INT DEFAULT 0,\n" +
                "  ION_CANNONS                   INT DEFAULT 0,\n" +
                "  GAUSS_CANNONS                 INT DEFAULT 0,\n" +
                "  PLASMA_TURRETS                INT DEFAULT 0,\n" +
                "  SMALL_SHIELD_DOME             INT DEFAULT 0,\n" +
                "  LARGE_SHIELD_DOME             INT DEFAULT 0,\n" +
                "  ANTI_BALLISTIC_MISSILES       INT DEFAULT 0,\n" +
                "  INTERPLANETARY_MISSILES       INT DEFAULT 0,";

//        System.out.println(
//                techs.replaceAll("\"","")
//                        .replaceAll(".*\\= ","")
//                        .replaceAll(",\n","\",\"")
//        );


        System.out.println(
                techs.replaceAll("\"","")
                        .replaceAll("INT.*","")
                        .replace(" ","")
                        .replaceAll("\n","\",\"")
        );
    }

    public static final String ID = "id";

    public static final String
            ENERGY              = "Energy Technology",
            LASER               = "Laser Technology",
            ION                 = "Ion Technology",
            HYPERSPACE_TECH     = "Hyperspace Technology",
            PLASMA              = "Plasma Technology",
            COMBUSTION          = "Combustion Drive",
            IMPULSE             = "Impulse Drive",
            HYPERSPACE_DRIVE    = "Hyperspace Drive",
            ESPIONAGE           = "Espionage Technology",
            COMPUTER            = "Computer Technology",
            ASTROPHYSICS        = "Astrophysics",
            INTERGALACTIC       = "Intergalactic Research Network",
            GRAVITON            = "Graviton Technology",
            WEAPONS             = "Weapons Technology",
            SHIELDING           = "Shielding Technology",
            ARMOUR              = "Armour Technology";

    public static final String RESEARCH = "Research";

    public static final String[] names = {
            ENERGY, LASER, ION, HYPERSPACE_TECH, PLASMA, COMBUSTION, IMPULSE,
            HYPERSPACE_DRIVE, ESPIONAGE, COMPUTER, ASTROPHYSICS, INTERGALACTIC,
            GRAVITON, WEAPONS, SHIELDING, ARMOUR
    };


}
