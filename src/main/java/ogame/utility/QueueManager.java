package ogame.utility;

import utilities.Utility;
import utilities.filesystem.FileOptions;
import utilities.jsoup.OgniterGalaxyParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 9/22/16.
 */
public class QueueManager {
    private static final String LOGIN = "login";
    private static QueueManager instance;
    private HashMap<File,List<String>> fileContents = new HashMap<>();

    public static HashMap<File,List<String>> getFileContents() throws IOException {
        return getInstance().fileContents;
    }

    public static QueueManager getInstance() throws IOException {
        if(instance == null)
            instance = new QueueManager();
        return instance;
    }

    private QueueManager() throws IOException {
        WatchServiceCreator.start();
        startFileContentReader();
        parseUniverse();
    }

    public void parseUniverse() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    OgniterGalaxyParser.parseEntireUniverse(398);

                    try {
                        Thread.sleep(1000*3600*24);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void startFileContentReader() {
        new Thread(new Runnable() {
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
        }).start();
    }

    String[] loginParams;

    public static String[] getLoginParams() throws IOException {
        return getInstance().getLoginParameters();
    }

    private String[] getLoginParameters() {
        if(loginParams == null) {
            List<String> values = new ArrayList<>();
            while (fileContents.isEmpty())
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            fileContents.values().forEach(a->values.addAll(a));
            String loginInfo = values.stream().filter(a->a.contains(LOGIN)).collect(Collectors.toList()).get(0);
            loginParams = loginInfo.split(":")[1].trim().split(",");
        }
        return loginParams;
    }

    public static void start() throws IOException {
        getInstance();
    }
}
