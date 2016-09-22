package ogame.utility;

import objects.Buildable;
import objects.Planet;
import ogame.pages.Login;
import ogame.pages.Overview;
import ogame.pages.Research;
import utilities.Utility;
import utilities.filesystem.FileOptions;
import utilities.selenium.UIMethods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 9/19/16.
 */
public class Initialize {
    public static String    BUILDINGS   = Utility.BUILDING_INFO,
                            FACILITIES  = Utility.FACILITIES_INFO,
                            RESEARCH    = Utility.RESEARCH_INFO,
                            SHIPYARD    = Utility.SHIPYARD_INFO,
                            MAPPINGS    = Utility.MAPPINGS;

    private static Initialize instance;

    private List<Buildable> buildables = new ArrayList<>();
    private HashMap<String,Integer> researches = new HashMap<>();

    public static Buildable getBuildableByName(String name){
        return getBuildableObjects().stream().filter(a->name.equals(a.getName())).collect(Collectors.toList()).get(0);
    }  public static Buildable getBuildableByID(int id) {
        return getBuildableObjects().stream().filter(a->a.getId() == id).collect(Collectors.toList()).get(0);
    }

    private static Initialize getInstance(){
        if(instance == null)
            instance = new Initialize();
//            throw new IllegalArgumentException("Please login first Initialize.login(uni,username,password)");
        return instance;
    }

    public static Overview login(String universe, String username, String password) throws IOException {
        instance = new Initialize(universe,username,password);
        return new Overview();
    }
    public static Overview login() throws IOException {
        String[] params = QueueManager.getLoginParams();
        instance = new Initialize(params[0],params[1],params[2]);
        return new Overview();
    }




    public static List<Buildable> getBuildableObjects(){
        return getInstance().buildables;
    }
    private Initialize() {
        try {
            loadFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static HashMap<String,Integer> getResearches(){
        return getInstance().researches;
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
        addBuildingFromFile(SHIPYARD);
    }


    public HashMap<String,Integer> getResearch() { //research name, level
        if(!researches.isEmpty())
            return researches;

        new Overview().clickOnResearch();

        List<Buildable> researchList = buildables.stream()
                .filter(a -> a.getId() >= 16 && a.getId() <= 31).collect(Collectors.toList());

        for(Buildable b : researchList) {
            String v = UIMethods.getTextFromAttributeAndValue(Research.ID, b.getWebName());
            if(v.contains("(")){
            	String[] split = v.split("\\(");
            	split[0] = split[0].trim();
            	split[1] = split[1].replaceAll("\\)","").replaceAll("\\+", "");
            	v = Integer.parseInt(split[0]) + Integer.parseInt(split[1]) + "";
            }
            researches.put(b.getName(),Integer.parseInt(v));
        }
        return researches;
    }

    public HashMap<String, Planet> getPlanets() { //planet name, Planet
        return new HashMap<>();
    }

    private static HashMap<String,List<Integer>> mappings = new HashMap<>();

    public static String getType(String name) throws IOException{
    	return getType(getBuildableByName(name).getId());
    }
    
    public static String getType(int id) throws IOException {
        if(mappings.isEmpty()) {
            List<String> v = FileOptions.readFileIntoListString(MAPPINGS);
            for(String s : v){
                String[] split = s.split(",");
                mappings.put(split[0], Arrays.asList(Integer.parseInt(split[1]),Integer.parseInt(split[2])));
            }
        }
        for(String key : mappings.keySet()){
            List<Integer> l = mappings.get(key);
            Integer v1 = l.get(0), v2 = l.get(1);
            if(id <= v2 && id >= v1)
                return key;
        }
        return null;
    }

}
