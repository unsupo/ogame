package ogame.utility;

import utilities.Utility;
import utilities.database.XMLAPIDownloader;
import utilities.filesystem.FileOptions;
import utilities.jsoup.OgniterGalaxyParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 9/22/16.
 */
public class QueueManager {
    private static final String LOGIN = "login", BUILD = "build";
    private static QueueManager instance;

    private PriorityBlockingQueue<QueuedJob> queuedJobs = new PriorityBlockingQueue<>();
    private HashMap<File,List<String>> fileContents = new HashMap<>();

    public static List<String> getProfileFileContents() throws IOException {
        return getInstance()._getProfileFileContents();
    }

    public HashMap<File,List<String>> _getFileContents() throws IOException {
        while (fileContents.isEmpty())
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        return fileContents;
    }

    public static QueueManager getInstance() throws IOException {
        if(instance == null)
            instance = new QueueManager();
        return instance;
    }

    public static PriorityBlockingQueue<QueuedJob> getPriorityQueue() throws IOException {
        return getInstance().queuedJobs;
    }

    private QueueManager() throws IOException {
        WatchServiceCreator.start();
        startFileContentReader();
//        _HSQLDB.setDbName();
//        parseUniverse();
        XMLAPIDownloader.parseFiles(Integer.parseInt(_getLoginParameters()[0].replaceAll("[^0-9]","")));
    }

    public void parseUniverse() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        OgniterGalaxyParser.parseEntireUniverse(Utility.getOgniterUniverseNumber(_getLoginParameters()[0]));
                        Thread.sleep(1000*3600*24);

                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private Thread fileReaderThread;
    private void startFileContentReader() {
        fileReaderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    while (!WatchServiceCreator.fileChanges.isEmpty())
                        try {
                            File f = WatchServiceCreator.fileChanges.poll();
                            f = new File(Utility.RESOURCE_DIR+"/runner_scripts/"+f.getName());
                            fileContents.put(f, FileOptions.readFileIntoListString(f.getAbsolutePath()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        if(!fileReaderThread.isAlive())
            fileReaderThread.start();
    }

    String[] loginParams;

    public static String[] getLoginParams() throws IOException {
        return getInstance()._getLoginParameters();
    }

    private List<String> _getProfileFileContents() throws IOException {
        List<String> values = new ArrayList<>();
        _getFileContents().values().forEach(a->values.addAll(a));
        return values;
    }

    private String[] _getLoginParameters() throws IOException {
        if(loginParams == null) {
            List<String> values = _getProfileFileContents();
            String loginInfo = values.stream().filter(a->a.contains(LOGIN)).collect(Collectors.toList()).get(0);
            loginParams = loginInfo.split(":")[1].trim().split(",");
        }
        return loginParams;
    }

    public static void start() throws IOException {
        getInstance();
    }

    public static HashMap<File,List<String>> getFileContents() throws IOException {
        return getInstance()._getFileContents();
    }

    public String[] getLoginParameters() throws IOException {
        return getInstance()._getLoginParameters();
    }
}
