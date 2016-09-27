package utilities;

import objects.*;
import ogame.pages.*;
import ogame.pages.Fleet;
import ogame.utility.Initialize;
import ogame.utility.Resource;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import utilities.database._HSQLDB;
import utilities.selenium.UIMethods;

import java.io.IOException;
import java.sql.SQLException;
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
        String time;
        String[] v;
        try {
            time = UIMethods.getTextFromAttributeAndValue("class", "time");
            v = time.split(" ");
        }catch (Exception e){
            return 0;
        }
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
    }

    public static void build(String name) throws IOException{
        build(name, 0);
    }
    public static void build(String name, int number) throws IOException{
        String type = Initialize.getType(name);
        clickOnNewPage(type);
        if(Utility.getInProgressTime() != 0)
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

    public static int getOgniterUniverseNumber(String universe) {
        if("s129-en.ogame.gameforge.com".equals(universe))
            return 572;
        if("s117-en.ogame.gameforge.com".equals(universe))
            return 398;
        return 398;
    }

    public static List<Coordinates> getInactiveTargets(Coordinates yourCoordinates) throws IOException, SQLException {
//        Coordinates coordinates = Initialize.getPlanetMap().get("Homeworld").getCoordinates();
        List<Map<String, Object>> results = _HSQLDB.executeQuery(
                "select coordinates from player p JOIN planet t ON p.player_name = t.player_name " +
                        "where player_status in ('I','i') and " +
                        "regexp_substring(coordinates,'[0-9]+')='" + yourCoordinates.getGalaxy() + "'");

        List<Coordinates> coords = results.stream().map(a->new Coordinates(a.get("COORDINATES").toString()))
                .collect(Collectors.toList());
        Collections.sort(coords,(a,b)->yourCoordinates.compareTo(a)-yourCoordinates.compareTo(b));
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

        if(getInProgressTime() != 0)
            return;

        if(Research.RESEARCH.equals(pageName))
            Initialize.getResearches().putAll(Initialize.getInstance().getValues(Research.ID,Research.RESEARCH));
        else {
            Coordinates planetCoordinates = getActivePlanetCoordinates();
            HashMap<Coordinates, Planet> planetMap = Initialize.getPlanetMap();

            if (!planetMap.containsKey(planetCoordinates))
                planetMap.put(planetCoordinates, new Planet());
            Planet planet = planetMap.get(planetCoordinates);

            int dm = readDarkMatter();
            Resource res = readResource();

            planet.setCurrentResources(res);
            planet.setCurrentDarkMatter(dm);

            if (Facilities.FACILITIES.equals(pageName))
                planet.getFacilities().putAll(Initialize.getInstance().getValues(Facilities.ID, Facilities.FACILITIES));
            else if (Resources.RESOURCES.equals(pageName))
                planet.getBuildings().putAll(Initialize.getInstance().getValues(Resources.ID, Resources.RESOURCES));
            else if (Shipyard.SHIPYARD.equals(pageName))
                planet.getShips().putAll(Initialize.getInstance().getValues(Shipyard.ID, Shipyard.SHIPYARD, Shipyard.WEB_ID_APPENDER));
            else if (Fleet.FLEET.equals(pageName)) {
                String fleets = UIMethods.getTextFromAttributeAndValue("class", "tooltip advice");
                String[] totes = fleets.split(":")[1].split("\\/");
                int used = Integer.parseInt(totes[0].trim());
                int possible = Integer.parseInt(totes[1].trim());
                int available = possible - used;

                Initialize.getInstance().setFleetSlotsAvailable(available);

                planet.getShips().putAll(Initialize.getInstance().getValues(Fleet.ID, Shipyard.SHIPYARD, Fleet.BUTTON_ID_WEB_APPENDER));
            }
        }
    }

    public static int getMessageCount(){
        return Message.getMessageCount();
    }

    public static boolean isBeingAttack(){
        return UIMethods.doesPageContainAttributeAndValue("id","attack_alert");
    }
}
