package ogame.utility;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import objects.*;
import ogame.pages.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utilities.Utility;
import utilities.database._HSQLDB;
import utilities.filesystem.FileOptions;
import utilities.selenium.UIMethods;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private long points;

    public static ZoneOffset getZoneOffset() throws IOException, SQLException {
        if(zoneOffset == null){
            ArrayList<String> zones = new ArrayList<>(ZoneId.getAvailableZoneIds());
            String zone = _HSQLDB.executeQuery("select * from server " +
                            "where number = " + Initialize.getUniverseID()).get(0).get("TIMEZONE").toString();

            zoneOffset = LocalDateTime.now().atZone(ZoneId.of(zone)).getOffset();
        }

        return zoneOffset;
    }

    private static ZoneOffset zoneOffset;
    private static int universeID;
    private static String username, password;
    private BuildTask currentResearch;
    private int currentDarkMatter = 0;
    private static HashMap<String,List<Integer>> mappings = new HashMap<>();
    private List<Buildable> buildables = new ArrayList<>();
    private HashMap<String,Integer> researches = new HashMap<>();//, facilities = new HashMap<>(), buildings = new HashMap<>();

    private HashMap<Coordinates,Planet> planets = new HashMap<>();//planetName, Planet object

    public void setTotalFleetSlots(int totalFleetSlots) {
        this.totalFleetSlots = totalFleetSlots;
    }

    public long getPoints() {
        return points;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    private int totalFleetSlots;

    public static BuildTask getCurrentResearch() {
        BuildTask research = getInstance().currentResearch;
        if(research != null && research.isComplete()){ //if it's complete then set the research to the new level and remove as a research being researched
            getResearches().put(research.getBuildable().getName(),research.getCountOrLevel());
            getInstance().currentResearch = null;
        }
        return getInstance().currentResearch;
    }

    public static int getUniverseID() {
        return universeID;
    }

    public static void setCurrentDarkMatter(int currentDarkMatter) {
        Initialize.getInstance().currentDarkMatter = currentDarkMatter;
    } public static int getCurrentDarkMatter() {
        return Initialize.getInstance().currentDarkMatter;
    }

    public int getFleetSlotsAvailable() {
        return fleetSlotsAvailable;
    }

    public void setFleetSlotsAvailable(int fleetSlotsAvailable) {
        this.fleetSlotsAvailable = fleetSlotsAvailable;
    }

    private int fleetSlotsAvailable;

    public static HashMap<Coordinates, Planet> getPlanetMap() throws IOException {
        return getInstance().getPlanets();
    }

    public static Buildable getBuildableByName(String name){
        return getBuildableObjects().stream().filter(a->name.equals(a.getName())).collect(Collectors.toList()).get(0);
    } public static Buildable getBuildableByNameIgnoreCase(String name){
        return getBuildableObjects().stream().filter(a->name.equalsIgnoreCase(a.getName())).collect(Collectors.toList()).get(0);
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
        universeID =Integer.parseInt(params[0].replaceAll("[^0-9]",""));
        username = params[1];
        password = params[2];
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
    
    public static Overview justLogin(String universe, String username, String password) throws IOException{
         return new Login().login(universe,username,password);
    }

    private Initialize(String universe, String username, String password) throws IOException {
        loadFiles();

        new Login().login(universe,username,password);

        String[] params = QueueManager.getInstance().getLoginParameters();
        String fName = params[1]+"_";
        fName += Utility.getOgniterUniverseNumber(params[0])+"";

        readObject(Utility.PROFILE_DIR+fName);

        QueueManager.start();
    }

    public static Planet getPlanet(Coordinates coords) throws IOException {
        return getInstance().getPlanetMap().get(coords);
    }
    public static Planet getPlanet(String name) throws IOException {
        return getInstance().getPlanetMap().values().stream()
                .filter(a->a.getPlanetName().equals(name))
                .collect(Collectors.toList()).get(0);
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
    }public HashMap<String,Integer> getFacilities(Coordinates planetName) throws IOException { //research name, level
        return getMapValue(Facilities.ID,"Facilities",getPlanets().get(planetName).getFacilities());
    }public HashMap<String,Integer> getBuildings(Coordinates planetName) throws IOException { //research name, level
        return getMapValue(Resources.ID,"Resources",getPlanets().get(planetName).getBuildings());
    }public HashMap<String,Integer> getShips(Coordinates planetName) throws IOException { //research name, level
        return getMapValue(Shipyard.ID,Shipyard.SHIPYARD,getPlanets().get(planetName).getShips());
    }public HashMap<String,Integer> getDefense(Coordinates planetName) throws IOException { //research name, level
        return getMapValue(Shipyard.ID,Defence.DEFENCE,getPlanets().get(planetName).getShips());
    }
    
    public HashMap<String, Integer> getBuildables(String type) throws IOException{
    	return getMapValue(getIdMap().get(type), type, new HashMap<String, Integer>());
    }
    
    private Map<String, String> getIdMap(){
    	Map<String, String> idMap = new HashMap<String, String>();
    	idMap.put(Overview.RESEARCH, Research.ID);
    	idMap.put(Overview.FACILITIES, Facilities.ID);
    	idMap.put(Overview.RESOURCES, Resources.ID);
    	return idMap;
    }

	public Map<String, Integer> getFacilities(int index) throws IOException {
		return getFacilities((Coordinates) getPlanets().keySet().toArray()[index]);
	}
    
    private HashMap<String,Integer> getMapValue(String ID, String name, HashMap<String,Integer> map) throws IOException {
        if(!map.isEmpty())
            return map;

        UIMethods.clickOnText(name);

        map.putAll(getValues(ID,name));

        return map;
    }

    public HashMap<String,Integer> getValues(String ID, String typeName) throws IOException{
        String preappender = "";
        if(Shipyard.SHIPYARD.equals(typeName))
            preappender = Shipyard.WEB_ID_APPENDER;
        return getValues(ID,typeName,preappender);
    }public HashMap<String,Integer> getValues(String ID, String typeName, String prepender) throws IOException {
        List<Integer> values = getMappings().get(typeName);
        int v1 = values.get(0), v2 = values.get(1);
        HashMap<String,Integer> map = new HashMap<>();
        List<Buildable> buildableList = buildables.stream()
                .filter(a -> a.getId() >= v1 && a.getId() <= v2).collect(Collectors.toList());
        for(Buildable b : buildableList) {
            String v = UIMethods.getTextFromAttributeAndValue(ID, prepender+b.getWebName());
            if(v == null)
                v = 0+"";
            if(v.contains("(")){
                String[] split = v.split("\\(");
                split[0] = split[0].trim();
                split[1] = split[1].replaceAll("\\)","").replaceAll("\\+", "");
                v = Integer.parseInt(split[0]) + Integer.parseInt(split[1]) + "";
                
            }
            if(v.contains("\n")){
            	v = v.split("\n")[1];
            }
            map.put(b.getName(),Integer.parseInt(v));
        }
        return map;
    }

    public HashMap<Coordinates, Planet> getPlanets() throws IOException { //planet name, Planet
        if(!planets.isEmpty())
            return planets;
        
        Elements smallPlanets = Jsoup.parse(UIMethods.getWebDriver().getPageSource()).select("div.smallplanet");
        for(Element e : smallPlanets){
            String id = e.id();
            String planetName = e.select("span.planet-name  ").text();
            String coordinates = e.select("span.planet-koords  ").text();

            Planet p = new Planet();
            p.setWebElement(id);
            p.setAttributeAndValue("id",id);
            p.setCoordinates(new Coordinates(coordinates));

            //FOR MOONS
            Elements el = e.select("a.moonlink");
            if(el != null && el.size() > 0){
                Planet m = new Planet();
                m.setAttributeAndValue("href",el.get(0).attr("href"));
                p.setMoon(m);
                planets.put(p.getCoordinates().clone().setType(Coordinates.MOON),m);
            }

            planets.put(p.getCoordinates(),p);
        }

        for(Coordinates coordinates : planets.keySet()){
            UIMethods.clickOnAttributeAndValue("id",planets.get(coordinates).getWebElement());
            UIMethods.clickOnText(Overview.OVERVIEW);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            planets.get(coordinates).setPlanetName(UIMethods.getTextFromAttributeAndValue("id","planetNameHeader").trim());
            planets.get(coordinates).setPlanetProperties(new PlanetProperties().parseProperties());

            getFacilities(coordinates);
            getBuildings(coordinates);
            getShips(coordinates);
//            Utility.clickOnNewPage(Shipyard.SHIPYARD); //TODO defense page
        }


        return planets;
    }

    static public DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void writeObject(Object...obj) throws IOException {
        String[] params = QueueManager.getInstance().getLoginParameters();
        JSONArray data = new JSONArray();
        for(Object o : obj) {
            String gson = new Gson().toJson(o);
            JSONObject jsonObj = new JSONObject(gson);
            data.put(jsonObj);
        }
        String fName = params[1]+"_";
        fName += Utility.getOgniterUniverseNumber(params[0])+"";

        JSONObject jo = new JSONObject().put("data",data).put("timestamp", LocalDateTime.now().format(f));
        new File(Utility.PROFILE_DIR).mkdirs();
        FileOptions.writeToFileOverWrite(Utility.PROFILE_DIR+fName,jo.toString());
    }


    public static void writeToJSON() throws IOException {
        getInstance().writeObject(getPlanetMap(),getResearches());
    }

    public void readObject(String src) throws IOException {
        if(!new File(src).exists()){
            getResearch();
            getPlanets();
            writeObject(planets,researches);
            return;
        }
        String obj = FileOptions.readFileIntoString(src);
        JSONObject jo = new JSONObject(obj);

        String lastUpdate = jo.getString("timestamp");
        LocalDateTime dateTime = LocalDateTime.from(f.parse(lastUpdate));

        if(dateTime.plusMinutes(10).isBefore(LocalDateTime.now())){
            getResearch();
            getPlanets();
            writeObject(planets,researches);
        }else{
            JSONArray jarr = jo.getJSONArray("data");
            HashMap<String, Planet> tempPlanets = new Gson()
                    .fromJson(jarr.get(0).toString(), new TypeToken<HashMap<String, Planet>>(){}.getType());
            tempPlanets.forEach((coordinate,planet)->{
                JSONObject joCoords = new JSONObject(coordinate).getJSONObject("Coordinates");
                String universe = joCoords.getString("universe");
                if(universe.equals("null"))
                    universe = null;
                planets.put(new Coordinates(universe,
                                            joCoords.getInt("galaxy"),
                                            joCoords.getInt("system"),
                                            joCoords.getInt("planet"),
                                            joCoords.getInt("type")),planet);
            });

            researches = new Gson().fromJson(jarr.get(1).toString(), new TypeToken<HashMap<String, Integer>>(){}.getType());
        }
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

    public int getTotalFleetSlots() {
        return totalFleetSlots;
    }

    public static void setCurrentResearch(BuildTask currentResearch) {
        getInstance().currentResearch = currentResearch;
    }
}
