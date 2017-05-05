package utilities.fileio;

import utilities.webdriver.Driver;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by jarndt on 5/2/17.
 */
public class JarUtility {
    public static final String  WINDOWS = "win", LINUX = "linux", MAC = "mac",
                                CHROME = Driver.CHROME, GECKO = Driver.FIREFOX, FIREFOX = GECKO, PHANTOMJS = Driver.PHANTOMJS;
    public static final String s = FileOptions.SEPERATOR;

    private String  webDriverPath   = FileOptions.cleanFilePath(System.getProperty("user.dir")+"/ogamebotapp/src/main/resources/web_drivers"),
                    exportPath      = System.getProperty("user.dir")+s+"jarResources";

    private HashMap<String,HashMap<String,String>> drivers = new HashMap<>();
    private List<String> defaultDrivers = new ArrayList<>(Arrays.asList(PHANTOMJS));
    private List<String> extractedFiles = new ArrayList<>();

    private void init(){
        List<String> os = Arrays.asList(WINDOWS,LINUX,MAC);
        List<String> driverName = Arrays.asList(CHROME,GECKO,PHANTOMJS);
        for(String s : os) {
            HashMap<String, String> map = new HashMap<>();
            String name = s.substring(0,3);
            for(String driver : driverName)
                map.put(driver,name+"_"+driver+"driver"+(s.equals(WINDOWS)?".exe":""));
            drivers.put(s, map);
        }

    }
    private static JarUtility instance;
    private JarUtility(){init();}
    public static JarUtility getInstance(){
        if(instance == null)
            instance = new JarUtility();
        return instance;
    }
    public static String getWebDriverPath(){
        return getInstance().webDriverPath;
    }
    public static void setWebDriverPath(String webDriverPath){
        getInstance().webDriverPath = webDriverPath;
    }

    public static List<String> getDefaultDrivers() {
        return getInstance().defaultDrivers;
    }

    public static void setDefaultDrivers(List<String> defaultDrivers) {
        getInstance().defaultDrivers = defaultDrivers;
    }

    public static HashMap<String, HashMap<String, String>> getDrivers() {
        return getInstance().drivers;
    }

    public static String getExportPath() {
        return getInstance().exportPath;
    }

    public static void setExportPath(String exportPath) {
        getInstance().exportPath = exportPath;
    }
    public static List<String> extractFiles() throws IOException, URISyntaxException {
        return extractFiles(new ArrayList<>(), null);
    }
    public static List<String> extractFiles(List<String> resourceFiles) throws IOException, URISyntaxException {
        return extractFiles(new ArrayList<>(), resourceFiles);
    }
    public static List<String> getExtractedFiles(){
        return getInstance().extractedFiles;
    }

    public static List<String> extractFiles(List<String> files, List<String>...resourceFiles) throws URISyntaxException, IOException {
        List<String> resourcesFileList = new ArrayList<>();
        if(resourceFiles != null && resourceFiles.length != 0)
            resourcesFileList.addAll(resourceFiles[0]);
        if(JarUtility.class.getResource("JarUtility.class").toString().startsWith("jar:")){
            System.out.println("Extracting jar resources, this is only done once");
            //YOU ARE IN A JAR FILE
            String path = getExportPath();
            new File(path).mkdir();
            List<String> findFiles = new ArrayList<>();
            findFiles.addAll(resourcesFileList);
            String jarFileLocation = new File(JarUtility.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
            //you're in a jar file
            List<String> fil = new ArrayList<>();
            for(String v : findFiles)
                if(!new File(path+"/"+v).exists())
                    try{fil.add(exportJarResources(path,jarFileLocation,v).getAbsolutePath());}
                    catch (NullPointerException e){}
                else
                    fil.add(path+"/"+v);
            files.addAll(fil);

            //extract all webdrivers for your perticular os
            String os = FileOptions.OS;
            String nPath = path+s+"web_drivers";
            if(new File(getWebDriverPath()).exists())
                nPath = getWebDriverPath();
            setWebDriverPath(nPath);

            if(!new File(nPath).exists())
                new File(nPath).mkdir();
            Map<String,String> filesDirs = new HashMap<>();
            for(String driver : getDefaultDrivers())
                filesDirs.put(driver,getDrivers().get(os).get(driver));

            final String p = path;
            for(String dir : filesDirs.keySet()){
                if(!new File(p).exists())
                    new File(p).mkdirs();
                if(new File(nPath+s+dir+s+filesDirs.get(dir)).exists())
                    continue;
                File s = exportJarResources(p,jarFileLocation,filesDirs.get(dir));//.getAbsolutePath();
                s.setExecutable(true);
                if(s != null)
                    System.out.println("Extracted: "+s.getAbsolutePath());
            }

        }else{
            //YOU ARE RUNNING FROM AN IDE
            List<String> v = resourcesFileList;
            for(String s : v)
                files.add(FileOptions.findFile(System.getProperty("user.dir"),s)
                        .get(0).getAbsolutePath());
        }
        getExtractedFiles().addAll(files);
        return files;
    }
    public static File exportJarResources(String destDir, String jarFileLocation, String fileToExtract) throws IOException {
        if(fileToExtract == null)
            return null;
//		if(new File(destDir+s+fileToExtract).exists())
//			return null;
        java.util.jar.JarFile jarfile = new java.util.jar.JarFile(new java.io.File(jarFileLocation)); //jar file path(here sqljdbc4.jar)
        java.util.Enumeration<java.util.jar.JarEntry> enu= jarfile.entries();
        File fl = null;
        while(enu.hasMoreElements()){
            java.util.jar.JarEntry je = enu.nextElement();

            if(!je.getName().contains(fileToExtract))
                continue;
//				System.out.println(je.getName());

            fl = new java.io.File(destDir, je.getName());
            if(!fl.exists()){
                fl.getParentFile().mkdirs();
                fl = new java.io.File(destDir, je.getName());
            }
            if(je.isDirectory())
                continue;

            java.io.InputStream is = jarfile.getInputStream(je);
            java.io.FileOutputStream fo = new java.io.FileOutputStream(fl);
            while(is.available()>0)
                fo.write(is.read());

            fo.close();
            is.close();
        }
        return fl;
    }
}
