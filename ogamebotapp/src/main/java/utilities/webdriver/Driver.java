package utilities.webdriver;

/**
 * Created by jarndt on 5/2/17.
 */

import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import utilities.fileio.FileOptions;
import utilities.fileio.JarUtility;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utilities.fileio.FileOptions.OS;

/**
 * Created by jarndt on 3/29/17.
 */
public class Driver {
    public static final String      FIREFOX     = "gecko", GECKO = FIREFOX,
                                    CHROME      = "chrome",
                                    PHANTOMJS   = "phantomjs", DEFAULT_DRIVER = CHROME;

    private String driverString, webDriverPath, proxy;

    private WebDriver driver;
    private DesiredCapabilities capabilities;
    private Dimension windowSize = new java.awt.Dimension(1440,900);
    private Point windowPosition = new Point(0,0);

    public Driver(){init(null, null);}
    public Driver(String driverName){init(driverName, null);}
    public Driver(String driverName, String webDriverPath){init(driverName, webDriverPath);}
    private void init(String driverName, String webDriverPath){
        setDriverName(driverName);
        setWebDriverPath(webDriverPath);
    }
    public void setDriverName(String driverName){
        String driverNameValue = DEFAULT_DRIVER;
        if(driverName != null)
            driverNameValue = driverName.toLowerCase();
        if("firefox".equalsIgnoreCase(driverNameValue))
            driverNameValue = GECKO;

        driverString = driverNameValue;
//        setWebDriverPath(this.webDriverPath);
    }public String getDriverName(){
        return driverString;
    }
    public void setWebDriverPath(String webDriverPath){
        this.webDriverPath = webDriverPath != null ? webDriverPath :
                FileOptions.cleanFilePath(JarUtility.getWebDriverPath()+"/"+driverString+"/");
    }
    public void setWindowSize(Dimension dimension){
        windowSize = dimension;
        if(driver == null)
            getDriver().manage().window().setSize(new org.openqa.selenium.Dimension((int)windowSize.getWidth(),(int)windowSize.getHeight()));
    }
    public WebDriver getDriver(){
        if(driver == null)
            initDriver();
        return driver;
    }

    public void setProxy(String proxyString){
        if(!checkProxy(proxyString))
            throw new IllegalArgumentException("Proxy passed in is wrong format must be host:port, given: "+proxyString);
        this.proxy = proxyString;
    }

    private boolean checkProxy(String proxyString) {
        if(!proxyString.contains(":"))
            return false;
        String[] split = proxyString.split(":");
        try{ int port = Integer.parseInt(split[1]);}
        catch (Exception e){
            return false;
        }
        return true;
    }

    public WebDriver getDriver(String webDriverPath, String...driverName){
        setDriverName(driverName != null && driverName.length == 1 ? driverName[0] : null);
        setWebDriverPath(webDriverPath);
        return getDriver();
    }

    public void setWindowPosition(Point p){
        windowPosition = p;
        if(driver != null)
            getDriver().manage().window().setPosition(p);
    }

    public void switchTabs(int tabNumber){
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        if(tabNumber > tabs.size() - 1) {
            ((JavascriptExecutor) driver).executeScript("window.open();");
            switchTabs(tabNumber);
            return;
        }
        driver.switchTo().window(tabs.get(tabNumber));
    }


    private void initDriver() {
        setWebDriverPath(null);
        String  path         = this.webDriverPath,
                driverValue  = JarUtility.getDrivers().get(OS.substring(0,3)).get(driverString);

        if(!PHANTOMJS.equalsIgnoreCase(driverString))
            System.setProperty("webdriver."+driverString+".driver",path+driverValue);
        else
            System.setProperty("phantomjs.binary.path",path+driverValue);

        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("acceptInsecureCerts", true);

        if(GECKO.equalsIgnoreCase(driverString))
            driver = new FirefoxDriver(capabilities);
        if(CHROME.equalsIgnoreCase(driverString))
            driver = new ChromeDriver(getCaps());
        if(PHANTOMJS.equalsIgnoreCase(driverString))
            driver = new PhantomJSDriver(getCaps());

        driver.manage().window().setPosition(windowPosition);
        driver.manage().window().maximize();
    }

    private DesiredCapabilities getCaps(){
//        java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        String resolution = screenSize.getWidth()+"x"+screenSize.getHeight();
//        System.out.println(resolution);
        if(capabilities != null)
            return capabilities;

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

        if(proxy != null)
            caps.setCapability(CapabilityType.PROXY, setUpProxy(proxy));
        capabilities = caps;
        return capabilities;
    }

    private Proxy setUpProxy(String proxyKey){
        org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
        proxy.setHttpProxy(proxyKey)
                .setFtpProxy(proxyKey)
                .setSslProxy(proxyKey);
        return proxy;
    }
}

