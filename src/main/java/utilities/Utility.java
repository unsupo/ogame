package utilities;

import objects.*;
import objects.messages.EspionageMsg;
import ogame.pages.*;
import ogame.pages.Fleet;
import ogame.utility.Initialize;
import ogame.utility.Resource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utilities.database._HSQLDB;
import utilities.selenium.UIMethods;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 8/8/16.
 */
public class Utility {
    public static final int MAX_THREAD_COUNT = 200;

    public static final String  DIR             = System.getProperty("user.dir"),
            RESOURCE_DIR    = DIR+"/src/main/resources/",
            PROFILE_DIR     = RESOURCE_DIR+"/profile/",
            SHIP_INFO       = RESOURCE_DIR+"ogame_ship_info.cvs",
            BATTLE_INFO     = RESOURCE_DIR+"battle_info",
            RESEARCH_INFO   = RESOURCE_DIR+"research_info.cvs",
            FACILITIES_INFO = RESOURCE_DIR+"facilities_info.cvs",
            BUILDING_INFO   = RESOURCE_DIR+"building_info.cvs",
            SHIPYARD_INFO   = RESOURCE_DIR+"shipyard_info.cvs",
            MAPPINGS        = RESOURCE_DIR+"mapper.cvs",
            LAST_UPDATE     = RESOURCE_DIR+"last_update";

    public static HashMap<String,String> idFromType = new HashMap();

    static{ //put the page name constant and the id of the items in the page
        idFromType.put(Facilities.FACILITIES,Facilities.ID);
        idFromType.put(Research.RESEARCH,Research.ID);
        idFromType.put(Resources.RESOURCES,Resources.ID);
        idFromType.put(Shipyard.SHIPYARD,Shipyard.ID);
    }

    public static long getInProgressTime(){
        return getInProgressTime("class","time");
    }
    public static long getInProgressTime(String attribute, String value){
        String time;
        String[] v;
        try {
            time = UIMethods.getTextFromAttributeAndValue(attribute,value);//"class", "time");
            v = time.split(" ");
        }catch (Exception e){
            return 0;
        }
        return getTimeConversion(v);
    }

    public static HashMap<String,Integer> getBuildableRequirements(String buildableName){
        requirements = new HashMap<>();
        getAllRequirements(buildableName);
        return requirements;
    }

    public static Map<String,Integer> getResearchRequirements(String buildableName){
        return getSubRequirements(buildableName, Arrays.asList(Research.names));
    }

    public static Map<String,Integer> getFacilityRequirements(String buildableName){
        return getSubRequirements(buildableName, Arrays.asList(Facilities.names));
    }

    private static Map<String, Integer> getSubRequirements(String buildableName, List<String> filter){
        Map<String, Integer> requirements = getBuildableRequirements(buildableName);
        Map<String, Integer> researchReqs = new HashMap<String, Integer>();
        for(String key: requirements.keySet()){
            if(filter.contains(key)){
                researchReqs.put(key, requirements.get(key));
            }
        }
        return researchReqs;
    }

    private static HashMap<String,Integer> requirements;

    private static void getAllRequirements(String buildable){
        for(Buildable b : Initialize.getBuildableByName(buildable).getRequires()){
            if(!requirements.containsKey(b.getName()) ||
                    (requirements.containsKey(b.getName()) && requirements.get(b.getName()) < b.getLevelNeeded()))
                requirements.put(b.getName(),b.getLevelNeeded());
            getAllRequirements(b.getName());
        }
    }

    public static Action clickAction(String ID, String constant){
        return clickAction(ID,constant,"");
    }
    public static Action clickAction(String ID, String constant, String appender){
        String webName = appender+Initialize.getBuildableByName(constant).getWebName();
        UIMethods.clickOnAttributeAndValue(ID,webName);
        UIMethods.waitForText(constant,1, TimeUnit.MINUTES);
        try {  //DON't think this is required
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Action();
    }

    public static Resource readResource(){
        Integer metal = Integer.parseInt(UIMethods.getTextFromAttributeAndValue("id", "resources_metal").replaceAll("\\.", ""));
        Integer crystal = Integer.parseInt(UIMethods.getTextFromAttributeAndValue("id", "resources_crystal").replaceAll("\\.", ""));
        Integer deuterium = Integer.parseInt(UIMethods.getTextFromAttributeAndValue("id", "resources_deuterium").replaceAll("\\.", ""));
        Integer energy = Integer.parseInt(UIMethods.getTextFromAttributeAndValue("id", "resources_energy").replaceAll("\\.", ""));
        return new Resource(metal, crystal, deuterium, energy);
    }

    public static int readDarkMatter(){
        return Integer.parseInt(UIMethods.getTextFromAttributeAndValue("id", "resources_darkmatter").replaceAll("\\.", ""));
    }

    public static boolean canAfford(String name){
        return Resource.getBaseCost(name).canAfford(readResource());
    }public static boolean canAfford(String name, int level) throws IOException {
        return Resource.getCost(name,level).canAfford(readResource());
    }

    public static void build(String name) throws IOException{
        build(name, 0);
    }
    public static void build(String name, int number) throws IOException{
        String type = Initialize.getType(name);
        clickOnNewPage(type);
        if(Utility.getInProgressTime() != 0 && !type.equals(Shipyard.SHIPYARD))
            return;
        String id = "id";
        if(idFromType.containsKey(type))
            id = idFromType.get(type);
        String v = "";
        if(Shipyard.SHIPYARD.equals(type))
            v = Shipyard.WEB_ID_APPENDER;
        clickAction(id, name, v);
        if(UIMethods.doesPageContainAttributeAndValue(id,"number"))
            UIMethods.typeOnAttributeAndValue(id, "number", number + "");
        if(UIMethods.doesPageContainAttributeAndValue("class",Action.startWithDM))
            new Action().clickOnStartWithDM();
        else
            UIMethods.clickOnAttributeAndValue("class", "build-it");
    }

    public static String getCurrentPage(){
        return UIMethods.getTextFromAttributeAndValue("class","selected").trim();
    }

    public static int getOgniterUniverseNumber(String universe) {
        if("s129-en.ogame.gameforge.com".equals(universe))
            return 572;
        if("s117-en.ogame.gameforge.com".equals(universe))
            return 398;
        return 398;
    }

    public static List<Coordinates> getAllInactiveTargets(Coordinates yourCoordinates, int universeID) throws IOException, SQLException {
        List<Map<String, Object>> results = _HSQLDB.executeQuery(
                "select * from highscore_player a " +
                        "join players p on p.universe_id = a.universe_id and p.id = a.id " +
                        "join planets t on t.universe_id = a.universe_id and t.player = p.id " +
                        "where universe_id = "+universeID+" and status in ('I','i') " +
                        "order by position");

        List < Coordinates > coords = results.stream().map(a -> new Coordinates(a.get("COORDS").toString()))
                .collect(Collectors.toList());

        Collections.sort(coords,(a,b)->yourCoordinates.getDistance(a)-yourCoordinates.getDistance(b));
        return coords;
    }
    public static List<Coordinates> getInactiveTargets(Coordinates yourCoordinates, int universeID, int maxPoints) throws IOException, SQLException {
//        Coordinates coordinates = Initialize.getPlanetMap().get("Homeworld").getCoordinates();
//        List<Map<String, Object>> results = _HSQLDB.executeQuery(
//                "select coordinates from player p JOIN planet t ON p.player_name = t.player_name " +
//                        "where player_status in ('I','i') and " +
//                        "regexp_substring(coordinates,'[0-9]+')='" + yourCoordinates.getGalaxy() + "'"); //old way

        List<Map<String, Object>> results = _HSQLDB.executeQuery(
                "select * from highscore_player a " +
                        "join players p on p.universe_id = a.universe_id and p.id = a.id " +
                        "join planets t on t.universe_id = a.universe_id and t.player = p.id " +
                        "where universe_id = "+universeID+" and status in ('I','i') and score < "+maxPoints+" " +
                        "order by position");

                List < Coordinates > coords = results.stream().map(a -> new Coordinates(a.get("COORDS").toString()))
                        .collect(Collectors.toList());

        Collections.sort(coords,(a,b)->yourCoordinates.getDistance(a)-yourCoordinates.getDistance(b));
        return coords;
    }


    public static String getActivePlanetName(){
        Elements v = Jsoup.parse(UIMethods.getWebDriver().getPageSource()).select("div.smallplanet");
        if(v.size() > 1)
            v = v.select("a.active");
        Elements vv = v.get(0).select("span.planet-name  ");
        return vv.text().trim();
    }public static Coordinates getActivePlanetCoordinates(){
        Elements v = Jsoup.parse(UIMethods.getWebDriver().getPageSource()).select("div.smallplanet");
        if(v.size() > 1)
            v = v.select("a.active");
        Elements vv = v.get(0).select("span.planet-koords");
        return new Coordinates(vv.text().trim());
    }

    public static Planet getActivePlanet() throws IOException {
        return Initialize.getPlanet(getActivePlanetCoordinates());
    }


    public static void clickOnNewPage(String pageName) throws IOException {
        if(Overview.MESSAGES.equals(pageName))
            UIMethods.clickOnAttributeAndValue("class","messages");
        else
            UIMethods.clickOnText(pageName);

//        if(!getCurrentPage().equals(pageName) && !Overview.MESSAGES.equals(pageName)) {
//            clickOnNewPage(pageName);
//            return;
//        } //TODO if not on the page try again
        boolean initialize = true;
        if(getInProgressTime() != 0)
            initialize = false;

        if(Research.RESEARCH.equals(pageName)) {
            if(initialize)
                Initialize.getResearches().putAll(Initialize.getInstance().getValues(Research.ID, Research.RESEARCH));
            Initialize.setCurrentResearch(setBuildTime(Initialize.getCurrentResearch()));
        }else {
            Coordinates planetCoordinates = getActivePlanetCoordinates();
            HashMap<Coordinates, Planet> planetMap = Initialize.getPlanetMap();

            if (!planetMap.containsKey(planetCoordinates))
                planetMap.put(planetCoordinates, new Planet());
            Planet planet = planetMap.get(planetCoordinates);

            int dm = readDarkMatter();
            Resource res = readResource();

            planet.setCurrentResources(res);
            Initialize.setCurrentDarkMatter(dm);

            //TODO Overview
            if (Facilities.FACILITIES.equals(pageName)) {
                if(initialize)
                    planet.getFacilities().putAll(Initialize.getInstance().getValues(Facilities.ID, Facilities.FACILITIES));
                setBuildTime(planet.getCurrentFacilityBeingBuild());
            }else if (Resources.RESOURCES.equals(pageName)) {
                if(initialize)
                    planet.getBuildings().putAll(Initialize.getInstance().getValues(Resources.ID, Resources.RESOURCES));
                setBuildTime(planet.getCurrentBuildingBeingBuild());
            }else if (Shipyard.SHIPYARD.equals(pageName)) {
                planet.getShips().putAll(Initialize.getInstance().getValues(Shipyard.ID, Shipyard.SHIPYARD, Shipyard.WEB_ID_APPENDER));
                setBuildTime(planet.getCurrentShipyardBeingBuild());
            }else if (Fleet.FLEET.equals(pageName)) {
                getFleetSlots();

                planet.getShips().putAll(Initialize.getInstance().getValues(Fleet.ID, Shipyard.SHIPYARD, Fleet.BUTTON_ID_WEB_APPENDER));
            }
        }
    }

    public static int getMessageCount(){
        return Message.getMessageCount();
    }

    public static boolean isBeingAttack(){
        return !UIMethods.doesPageContainAttributeAndValue("class","tooltip eventToggle noAttack");
    }

    public static BuildTask setBuildTime(BuildTask buildTime) {
        Elements activeTable = Jsoup.parse(UIMethods.getWebDriver().getPageSource()).select("table.construction.active");
        if(activeTable.size() == 0 || activeTable.select("td > a").text().trim().contains("No buildings in construction.")
                || activeTable.select("td > a").text().trim().contains("There is no research in progress at the moment."))
            return null;
        Buildable buildable = Initialize.getBuildableByNameIgnoreCase(activeTable.select("th").text().trim());
        int level;
        try {
            level = Integer.parseInt(activeTable.select("span.level").text().split(" ")[1].trim());
        }catch (ArrayIndexOutOfBoundsException e){
            level = Integer.parseInt(activeTable.select("div.shipSumCount").text().trim());
        }

        long millis = getInProgressTime("id", "Countdown")+System.currentTimeMillis();
        LocalDateTime done = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
        buildTime = new BuildTask(buildable,done,level);
        return buildTime;
    }

    public static void setBuildTime(Set<BuildTask> buildTime) {
        BuildTask task = setBuildTime(new BuildTask());
        if(task == null) {
            buildTime.clear();
            return;
        }buildTime.add(task);
        Elements table = Jsoup.parse(UIMethods.getWebDriver().getPageSource()).select("#pqueue").select("li");
        for(Element e : table) {
            String[] contents = e.attr("title").split("<br>");
            String[] quantityName = contents[0].split(" ");
            String name = contents[0].replaceAll("[0-9]+","").trim();
            Buildable buildable = Initialize.getBuildableByName(name);
            int level = Integer.parseInt(quantityName[0]);
            String time = contents[1].replace("Building duration ","");
            long millis = getTimeConversion(time.split(" "))+System.currentTimeMillis();
            LocalDateTime done = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
            buildTime.add(new BuildTask(buildable,done,level));
        }
    }

    public static long getTimeConversion(String v){ return getTimeConversion(v.split(" ")); }
    public static long getTimeConversion(String[] v) {
        long timeLeft = 100;
        for(String s : v)
            if(s.contains("s"))
                timeLeft+=Long.parseLong(s.replace("s",""))*1000;
            else if(s.contains("m"))
                timeLeft+=Long.parseLong(s.replace("m",""))*1000*60;
            else if(s.contains("h"))
                timeLeft+=Long.parseLong(s.replace("h",""))*1000*60*60;
            else if(s.contains("d"))
                timeLeft+=Long.parseLong(s.replace("d",""))*1000*60*60*24;
            else if(s.contains("w"))
                timeLeft+=Long.parseLong(s.replace("w",""))*1000*60*60*24*7;

        return timeLeft;
    }

    public static List<Mission> getFleetInformation() {
        return Mission.getActiveMissions();
    }
    public static int getOwnMissionCount(){
        return EspionageMsg.parseNumber(Jsoup.parse(UIMethods.getWebDriver().getPageSource())
                .select("p.event_list > span.undermark").text()).intValue();
    }


    public static int getRandomIntRange(int min, int max){
        if(min > max){
            int temp = min;
            min = max;
            max = temp;
        }
        return new Random().nextInt(max - min)+min;
    }

    public static void markAsDontAttack(Coordinates targetCoordinates, int probeCount) throws IOException, SQLException {
        markAsDontAttack(targetCoordinates,probeCount,-1);
    }
    public static void markAsDontAttack(Coordinates targetCoordinates, int probeCount, long fleets) throws IOException, SQLException {
        markAsDontAttack(targetCoordinates,probeCount,fleets,-1);
    }
    public static void markAsDontAttack(Coordinates targetCoordinates, int probeCount, long fleets, long defense) throws IOException, SQLException {
        int espionageTechLevel = Initialize.getResearches().get(Research.ESPIONAGE);
        String query = "INSERT INTO DONT_ATTACK_LIST(coords,universe_id,probeCount,fleets,espionageTech) " +
                "VALUES('"+targetCoordinates.getStringValue()+"',"+Initialize.getUniverseID()+
                ","+probeCount+","+fleets+","+espionageTechLevel+")";
        try {
            _HSQLDB.executeQuery(query);
        }catch (Exception e){
            if (!e.getMessage().contains("unique constraint")) {
                System.err.println("FAILED QUERY: " + query);
                throw e;
            }else{
                String whereClause = " WHERE universe_id = "+Initialize.getUniverseID()+" and " +
                        "coords = '"+targetCoordinates.getStringValue()+"'";
                Map<String, Object> v = _HSQLDB.executeQuery("select * from DONT_ATTACK_LIST " + whereClause).get(0);
                int currentValue = v.get("ESPIONAGETECH")==null?0:Integer.parseInt(v.get("ESPIONAGETECH").toString());
                int currentProbes = v.get("PROBECOUNT")==null?0:Integer.parseInt(v.get("PROBECOUNT").toString());
                int tempValue, max = espionageTechLevel, min = currentValue;
                if(max < min){
                    tempValue = max;
                    max = min;
                    min = tempValue;
                }
                int v1 = currentProbes+(min-(max+1))*Math.abs(min-(max+1));
                int v2 = probeCount+(min-(max+1))*Math.abs(min-(max+1));
                boolean updateTechAndProbe = true;
                if(v1>=v2)
                    updateTechAndProbe = false;

                query = "UPDATE DONT_ATTACK_LIST SET "+
                        (updateTechAndProbe?("espionageTech = "+espionageTechLevel+", probeCount = "+probeCount+", "):"") +
                        "fleets = "+fleets+", defence = "+defense+whereClause;

                _HSQLDB.executeQuery(query);
            }
        }
    }

    public static int getFleetSlots() {
        String fleets = UIMethods.getTextFromAttributeAndValue("class", "tooltip advice");
        String[] totes = null;
        try {
            totes = fleets.split(":")[1].split("\\/");
        }catch (Exception e){
            return -1;
        }
        int used = Integer.parseInt(totes[0].trim());
        int possible = Integer.parseInt(totes[1].trim());
        int available = possible - used;

        Initialize.getInstance().setFleetSlotsAvailable(available);
        Initialize.getInstance().setTotalFleetSlots(possible);
        return available;
    }
}
