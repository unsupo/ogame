import org.openqa.selenium.*;
import utilities.ProxyList;
import utilities.fileio.FileOptions;
import utilities.fileio.JarUtility;
import utilities.PasswordEncryptDecrypt;
import utilities.webdriver.Driver;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 5/2/17.
 */
public class Runner {
    public static void main(String[] args) throws IOException, GeneralSecurityException, URISyntaxException, InterruptedException {
        parseCommandLineArgs(args);
        JarUtility.extractFiles();
        ProxyList.parallelTestIPS();

//        List<Driver> drivers = new ArrayList<>();
//        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
//        Driver driver = new Driver();
//        driver.setDriverName(Driver.PHANTOMJS);
//        driver.getDriver().navigate().to("http://www.whatsmyip.org/");
          //working list: "60.250.81.118:8080","1.179.189.217:8080","190.104.245.39:8080"
        //maybe list: "212.200.126.226:8080","222.124.129.178:8080"
//        List<String> proxyList = Arrays.asList("1.179.189.217:8080","60.250.81.118:8080","222.124.129.178:8080","190.104.245.39:8080");
//        int wx = 2, wy = 2;
//        IntStream.range(0,wx).parallel().forEach(i->{
//            IntStream.range(0,wy).parallel().forEach(j->{
//                Driver driver = new Driver();
//                driver.setProxy(proxyList.get(i+j));
//                driver.setWindowSize(new Dimension(size.width / wx, size.height / wy));
//                driver.setWindowPosition(new Point(i*size.width/wx, j*size.height/wy));
//                drivers.add(driver);
//            });
//        });
//        List<String> sites = Arrays.asList("http://google.com", "https://github.com/", "http://www.cs.usu.edu/", "https://mail.google.com/mail/u/0/#inbox");
//        IntStream.range(0,sites.size()).parallel().forEach(i->drivers.get(i).getDriver().navigate().to("http://www.whatsmyip.org/"));


//        drivers.parallelStream().forEach(a->a.getDriver().navigate().to("http://www.whatsmyip.org/"));
//
//        Thread.sleep(100000);
//        drivers.parallelStream().forEach(a->a.getDriver().quit());
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
