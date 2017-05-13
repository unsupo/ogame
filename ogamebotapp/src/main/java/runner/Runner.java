package runner;

import org.openqa.selenium.*;
import org.quartz.*;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;
import utilities.data.XMLAPIDownloader;
import utilities.database.Database;
import utilities.database.XMLToDatabase;
import utilities.fileio.FileOptions;
import utilities.fileio.JarUtility;
import utilities.PasswordEncryptDecrypt;
import utilities.webdriver.DriverController;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by jarndt on 5/2/17.
 */
public class Runner {
    public static void main(String[] args) throws IOException, GeneralSecurityException, URISyntaxException, InterruptedException, SchedulerException, SQLException, ClassNotFoundException {
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
        String f = JarUtility.exportJarDirectory("postgres");
        if(f!=null) {
            String postgresDir = FileOptions.cleanFilePath(JarUtility.getResourceDir() + "/databases/postgres/");
            FileOptions.runConcurrentProcess(
                    FileOptions.readFileIntoListString(postgresDir + "permissions.txt")
                            .stream().map(a -> (Callable) () -> {
                        String[] file = a.split(",");
                        file[0] = FileOptions.cleanFilePath(postgresDir + file[0].substring(1));
//                            System.out.println("Working on : "+file[1]+"\t"+file[0]);
                        if (file[3].equals("d"))
                            new File(file[0]).mkdirs();
                        else
                            new File(file[0]).createNewFile();

                        return FileOptions.setPermissionUnix(Integer.parseInt(file[2]), file[0]);
                    }).collect(Collectors.toList())
                    , 20);
        }
        Database.startDatabase();
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
