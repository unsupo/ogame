package runner;

import org.openqa.selenium.*;
import org.quartz.*;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;
import utilities.data.XMLAPIDownloader;
import utilities.database.XMLToDatabase;
import utilities.fileio.JarUtility;
import utilities.PasswordEncryptDecrypt;
import utilities.webdriver.DriverController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by jarndt on 5/2/17.
 */
public class Runner {
    public static void main(String[] args) throws IOException, GeneralSecurityException, URISyntaxException, InterruptedException, SchedulerException {
        run(args);
    }

    /**
     * Start the Ogame bot with this method and arguments.
     *
     * @param args
     * @throws IOException
     * @throws URISyntaxException
     * @throws GeneralSecurityException
     */
    public static void run(String[] args) throws IOException, URISyntaxException, GeneralSecurityException, SchedulerException {
        parseCommandLineArgs(args);
        List<String> files = JarUtility.extractFiles(Arrays.asList(
                "email_list.txt", "failed.txt", "working.txt",
                "ogame_ship_info.csv", "battle_info", "research_info.csv",
                "facilities_info.csv", "building_info.csv", "shipyard_info.csv",
                "defense_info.csv", "mapper.csv", "create_tables.sql","postgres_commands.sql"
        ));
        XMLAPIDownloader.startDownloadXMLThreads();
    }

    public static void extractFiles() throws IOException, URISyntaxException {
        JarUtility.extractFiles(Arrays.asList("email_list.txt","failed.txt","working.txt"));
    }

    private static void parseCommandLineArgs(String[] args) throws IOException, GeneralSecurityException {
        if(args!=null && args.length != 0){
            List<String> ecryptSwitches = new ArrayList<>(Arrays.asList("-encrypt","-e"));
            List<String> defaultDriver = new ArrayList<>(Arrays.asList("-defaultDriver","-dd"));
            List<String> exportPath = new ArrayList<>(Arrays.asList("-exportPath","-ep"));
            List<String> webDriverPath = new ArrayList<>(Arrays.asList("-webDriverPath","-wdp"));
            for(int i = 0; i<args.length; i++){
                if(ecryptSwitches.contains(args[i])){
                    try{
                        PasswordEncryptDecrypt.encryptReader();
                    }catch (IndexOutOfBoundsException e){
                        System.err.println("No value to encrypt");
                    }
                    System.exit(0);
                }
                try {
                    if(defaultDriver.contains(args[i]))
                        JarUtility.setDefaultDrivers(Arrays.asList(args[i + 1].split(",")));
                    if(exportPath.contains(args[i]))
                        JarUtility.setExportPath(args[i + 1]);
                    if(webDriverPath.contains(args[i]))
                        JarUtility.setWebDriverPath(args[i + 1]);
                }catch(IndexOutOfBoundsException e){
                    System.err.println("No given switch option for: "+args[i]);
                }
            }
        }
    }
}
