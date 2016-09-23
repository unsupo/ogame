package utilities;

import objects.Buildable;
import ogame.pages.Action;
import ogame.utility.Initialize;
import utilities.selenium.UIMethods;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by jarndt on 8/8/16.
 */
public class Utility {
    public static final int MAX_THREAD_COUNT = 200;

    public static final String  DIR             = System.getProperty("user.dir"),
                                RESOURCE_DIR    = DIR+"/src/main/resources/",
                                SHIP_INFO       = RESOURCE_DIR+"ogame_ship_info.cvs",
                                BATTLE_INFO     = RESOURCE_DIR+"battle_info",
                                RESEARCH_INFO   = RESOURCE_DIR+"research_info.cvs",
                                FACILITIES_INFO = RESOURCE_DIR+"facilities_info.cvs",
                                BUILDING_INFO   = RESOURCE_DIR+"building_info.cvs",
                                SHIPYARD_INFO   = RESOURCE_DIR+"shipyard_info.cvs",
                                MAPPINGS        = RESOURCE_DIR+"mapper.cvs",
                                LAST_UPDATE     = RESOURCE_DIR+"last_update";

    public static long getInProgressTime(){
        String time;
        try {
            time = UIMethods.getTextFromAttributeAndValue("class", "time");
        }catch (Exception e){
            return 0;
        }
        String[] v = time.split(" ");
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
        String webName = Initialize.getBuildableByName(constant).getWebName();
        UIMethods.clickOnAttributeAndValue(ID,webName);
        UIMethods.waitForText(constant,30, TimeUnit.SECONDS);
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new Action();
    }
    
    
    public static void build(String name, int number) throws IOException{
    	String type = Initialize.getType(name);
        UIMethods.clickOnText(type);
        clickAction("id", name);
        UIMethods.typeOnAttributeAndValue("id", "number", number + "");
        new Action().clickOnStartWithDM();
        UIMethods.clickOnAttributeAndValue("class", "build-it");
    }

    public static int getOgniterUniverseNumber(String universe) {
        if("s129-en.ogame.gameforge.com".equals(universe))
            return 645;
        if("s117-en.ogame.gameforge.com".equals(universe))
            return 398;
        return 398;
    }
}
