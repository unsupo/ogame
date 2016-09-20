package objects;

import java.util.HashMap;

/**
 * Created by jarndt on 9/19/16.
 */
public class Planet {
    public String planetName;

    public Coordinates coordinates;

    public HashMap<String, Integer> buildings   = new HashMap<>(), //building name, level, facilities are included
                                    defense     = new HashMap<>(), //defense name, count
                                    ships       = new HashMap<>(); //ship name, count

    public long currentMetal, currentCrystal, currentDueterium,
                metalProduction, crystalProduction, dueteriumProduction;
}
