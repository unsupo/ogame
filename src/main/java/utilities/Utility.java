package utilities;

import utilities.selenium.UIMethods;

/**
 * Created by jarndt on 8/8/16.
 */
public class Utility {
    public static final String  DIR             = System.getProperty("user.dir"),
                                RESOURCE_DIR    = DIR+"/src/main/resources/",
                                SHIP_INFO       = RESOURCE_DIR+"ogame_ship_info.cvs",
                                BATTLE_INFO     = RESOURCE_DIR+"battle_info",
                                RESEARCH_INFO   = RESOURCE_DIR+"research_info.cvs",
                                FACILITIES_INFO = RESOURCE_DIR+"facilities_info.cvs",
                                BUILDING_INFO   = RESOURCE_DIR+"building_info.cvs";

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
}
