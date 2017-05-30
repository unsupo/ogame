package ogame.simulators;

import ogame.objects.game.Buildable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by jarndt on 5/26/17.
 */
public class FarmingSim {
    /*
    SUDO CODE:
        goal_buildings
        start():
            get starting resources
            for buildings you can build right now:
                if building is less than goal_buildings level:
                    tree.build_it()

        build_it():
            subtract required resources for it
            advance_timestep()

        advance_timestep():
            increase time by time it takes to build it.
            increase resources based off mine levels and time pasted

     */


    public static final double METAL_RATIO = 1, CRYSTAL_RATIO = 2, DUETERIUM_RATIO = 3;
    public static final int METAL_BASE = 30, CRYSTAL_BASE = 15, DUETERIUM_BASE = 0;

    HashMap<String,Integer> facilities = new HashMap<>(), resources = new HashMap<>();

    public FarmingSim() throws IOException {
        Buildable.getFacilitites().forEach(a->facilities.put(a.getName(),0));
        Buildable.getResources().forEach(a->resources.put(a.getName(),0));
    }
    public FarmingSim(HashMap<String,Integer> currentLevels) throws IOException {
        Buildable.getFacilitites().forEach(a->facilities.put(a.getName(),0));
        Buildable.getResources().forEach(a->resources.put(a.getName(),0));

        currentLevels.forEach((a,b)->{
            if(resources.containsKey(a)) resources.put(a,b);
            if(facilities.containsKey(a)) facilities.put(a,b);
        });
    }
}
