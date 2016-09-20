package ogame.utility;

import objects.Buildable;
import objects.Planet;
import ogame.pages.Login;
import ogame.pages.Overview;
import utilities.Utility;
import utilities.filesystem.FileOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 9/19/16.
 */
public class Initialize {
    public static String    BUILDINGS   = Utility.BUILDING_INFO,
                            FACILITIES  = Utility.FACILITIES_INFO,
                            RESEARCH    = Utility.RESEARCH_INFO;

    private static Initialize instance;

    private List<Buildable> buildables = new ArrayList<>();

    public static Buildable getBuildableByName(String name){
        return getBuildableObjects().stream().filter(a->name.equals(a.getName())).collect(Collectors.toList()).get(0);
    }

    private static Initialize getInstance(){
        if(instance == null)
            throw new IllegalArgumentException("Please login first Initialize.login(uni,username,password)");
        return instance;
    }

    public static Overview login(String universe, String username, String password) throws IOException {
        instance = new Initialize(universe,username,password);
        return new Overview();
    }
    public static List<Buildable> getBuildableObjects(){
        return getInstance().buildables;
    }

    private Initialize(String universe, String username, String password) throws IOException {
        loadFiles();

        new Login().login(universe,username,password);

        getPlanets();
        getResearch();
    }

    private void addBuildingFromFile(String file) throws IOException {
        FileOptions.readFileIntoListString(file).forEach(a->buildables.add(new Buildable(a)));
    }

    private void loadFiles() throws IOException {
        addBuildingFromFile(BUILDINGS);
        addBuildingFromFile(RESEARCH);
        addBuildingFromFile(FACILITIES);
    }


    public HashMap<String,Integer> getResearch() { //research name, level
        return new HashMap<>();
    }

    public HashMap<String, Planet> getPlanets() { //planet name, Planet
        return new HashMap<>();
    }
}
