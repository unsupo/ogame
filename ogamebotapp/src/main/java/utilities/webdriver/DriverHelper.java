package utilities.webdriver;

/**
 * Created by jarndt on 5/2/17.
 */

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jarndt on 3/29/17.
 */
public class DriverHelper {
    public static String    FIREFOX     = "gecko",
                            CHROME      = "chrome",
                            PHANTOMJS   = "phantomjs";

    public final static String OS = System.getProperty("os.name").toLowerCase();

    public static String driverString;

    public static WebDriver getDriver(String webDriverPath, String...driverName){
        String driverNameValue = "chrome";
        if(driverName != null && driverName.length != 0 && driverName[0] != null)
            driverNameValue = driverName[0].toLowerCase();
        if("firefox".equalsIgnoreCase(driverNameValue))
            driverNameValue = "gecko";

        driverString = driverNameValue;
        String path = webDriverPath+"/"+driverNameValue+"/", driverValue = driverNameValue+"driver";
        if(OS.indexOf("win") >= 0 )
            driverValue = "win_"+driverValue+".exe";
        else if(OS.indexOf("mac") >= 0)
            driverValue = "mac_"+driverValue;
        else if(OS.indexOf("linux") >= 0)
            driverValue = "lin_"+driverValue;

        if(!"phantomjs".equalsIgnoreCase(driverNameValue))
            System.setProperty("webdriver."+driverNameValue+".driver",path+driverValue);
        else
            System.setProperty("phantomjs.binary.path",path+driverValue);

        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("acceptInsecureCerts", true);

        WebDriver driver = null;
        if("gecko".equalsIgnoreCase(driverNameValue))
            driver = new FirefoxDriver(capabilities);
        if("chrome".equalsIgnoreCase(driverNameValue))
            driver = new ChromeDriver();
        if("phantomjs".equalsIgnoreCase(driverNameValue))
            driver = new PhantomJSDriver(getCaps());


        java.awt.Dimension screenSize = new java.awt.Dimension(1440,900);
        try{  Toolkit.getDefaultToolkit().getScreenSize(); }
        catch (Exception headlessException){ /* Do Nothing */}
        driver.manage().window().setSize(new org.openqa.selenium.Dimension((int)screenSize.getWidth(),(int)screenSize.getHeight()));
        return driver;
    }

    private static DesiredCapabilities getCaps(){
//        java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        String resolution = screenSize.getWidth()+"x"+screenSize.getHeight();
//        System.out.println(resolution);

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        String[] args1 = {"--ignore-ssl-errors=yes","--webdriver-loglevel=NONE"};
        caps.setJavascriptEnabled(true);
        caps.setCapability("takesScreenshot", true);
//        caps.setCapability("screen-resolution", resolution);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, args1);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "loadImages", true);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "javascriptEnabled", true);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "resourceTimeout", 60000);
        caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 5.1; rv:22.0) Gecko/20100101 Firefox/22.0");
//	    caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] {"--webdriver-loglevel=ERROR"});//NONE,ERROR
//        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomPath);
        Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
        return caps;
    }
}

