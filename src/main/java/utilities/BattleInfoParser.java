package utilities;

import objects.Player;
import objects.Ship;
import utilities.filesystem.FileOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarndt on 8/8/16.
 */
public class BattleInfoParser {
    private final static String WEAPONS     = "weapons",
                                ARMOUR      =  "armour",
                                SHIELDING   = "shielding";

    public static List<Player> parse(String battleInfoPath) throws IOException {
        List<String> file = FileOptions.readFileIntoListString(battleInfoPath);
        List<Player> players = new ArrayList<>();

        Player p = null;
        for(String s : file){
            String[] split = s.split(":");
            if(s.startsWith("-")){
                if(p == null)
                    p = new Player(s);
                else {
                    players.add(p);
                    p = new Player(s);
                }
            }else if(WEAPONS.equals(split[0]))
                p.setWeaponsTech(Integer.parseInt(split[1]));
            else if(ARMOUR.equals(split[0]))
                p.setArmourTeach(Integer.parseInt(split[1]));
            else if(SHIELDING.equals(split[0]))
                p.setShieldingTech(Integer.parseInt(split[1]));
            else if(Ship.isValidShip(split[0]))
                p.addShip(split[0],Integer.parseInt(split[1]));
        }
        players.add(p);
        return players;
    }

}
