package ogame.utility;

import objects.Buildable;
import objects.Coordinates;
import objects.Planet;
import ogame.pages.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

    private static HashMap<String,List<Integer>> mappings = new HashMap<>();
    private List<Buildable> buildables = new ArrayList<>();
    private HashMap<String,Integer> researches = new HashMap<>();//, facilities = new HashMap<>(), buildings = new HashMap<>();
    private HashMap<String,Planet> planets = new HashMap<>();//planetName, Planet object

    public static Buildable getBuildableByName(String name){
        return getBuildableObjects().stream().filter(a->name.equals(a.getName())).collect(Collectors.toList()).get(0);
    }  public static Buildable getBuildableByID(int id) {
        return getBuildableObjects().stream().filter(a->a.getId() == id).collect(Collectors.toList()).get(0);
    }

    public static Initialize getInstance(){
        if(instance == null)
            instance = new Initialize();
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

        QueueManager.start();
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


    public HashMap<String,Integer> getResearch() throws IOException { //research name, level
        return getMapValue(Research.ID,"Research",researches);
    }public HashMap<String,Integer> getFacilities(String planetName) throws IOException { //research name, level
        return getMapValue(Facilities.ID,"Facilities",getPlanets().get(planetName).getFacilities());
    }public HashMap<String,Integer> getBuildings(String planetName) throws IOException { //research name, level
        return getMapValue(Resources.ID,"Resources",getPlanets().get(planetName).getBuildings());
    }
    private HashMap<String,Integer> getMapValue(String ID, String name, HashMap<String,Integer> map) throws IOException {
        if(!map.isEmpty())
            return map;

        UIMethods.clickOnText(name);

        map.putAll(getValues(ID,name));

        return map;
    }

    public HashMap<String,Integer> getValues(String ID, String typeName) throws IOException {
        List<Integer> values = getMappings().get(typeName);
        int v1 = values.get(0), v2 = values.get(1);
        HashMap<String,Integer> map = new HashMap<>();
        List<Buildable> buildableList = buildables.stream()
                .filter(a -> a.getId() >= v1 && a.getId() <= v2).collect(Collectors.toList());
        for(Buildable b : buildableList) {
            String v = UIMethods.getTextFromAttributeAndValue(ID, b.getWebName());
            if(v.contains("(")){
                String[] split = v.split("\\(");
                split[0] = split[0].trim();
                split[1] = split[1].replaceAll("\\)","").replaceAll("\\+", "");
                v = Integer.parseInt(split[0]) + Integer.parseInt(split[1]) + "";
            }
            map.put(b.getName(),Integer.parseInt(v));
        }
        return map;
    }

    public HashMap<String, Planet> getPlanets() throws IOException { //planet name, Planet
        if(!planets.isEmpty())
            return planets;

        Elements smallPlanets = Jsoup.parse(UIMethods.getWebDriver().getPageSource()).select("div.smallplanet");
        for(Element e : smallPlanets){
            String id = e.id();
            String planetName = e.select("span.planet-name  ").text();
            String coordinates = e.select("span.planet-koords  ").text();

            Planet p = new Planet();
            p.setWebElement(id);
            p.setCoordinates(new Coordinates(coordinates));

            planets.put(planetName,p);
        }

        for(String name : planets.keySet()){
            UIMethods.clickOnAttributeAndValue("id",planets.get(name).getWebElement());
            getFacilities(name);
            getBuildings(name);
        }

        return planets;
    }


    public static String getType(String name) throws IOException{
    	return getType(getBuildableByName(name).getId());
    }
    public static HashMap<String, List<Integer>> getMappings() throws IOException {
        if(mappings.isEmpty()) {
            List<String> v = FileOptions.readFileIntoListString(MAPPINGS);
            for(String s : v){
                String[] split = s.split(",");
                mappings.put(split[0], Arrays.asList(Integer.parseInt(split[1]),Integer.parseInt(split[2])));
            }
        }
        return mappings;
    }
    public static String getType(int id) throws IOException {
        for(String key : getMappings().keySet()){
            List<Integer> l = getMappings().get(key);
            Integer v1 = l.get(0), v2 = l.get(1);
            if(id <= v2 && id >= v1)
                return key;
        }
        return null;
    }


}
