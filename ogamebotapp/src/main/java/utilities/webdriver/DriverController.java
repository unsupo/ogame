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
import utilities.database.DatabaseCommons;
import utilities.fileio.FileOptions;
import utilities.fileio.JarUtility;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utilities.fileio.FileOptions.OS;

/**
 * Created by jarndt on 3/29/17.
 */
public class DriverController {
    public static final String      FIREFOX     = "gecko", GECKO = FIREFOX,
                                    CHROME      = "chrome",
                                    PHANTOMJS   = "phantomjs", DEFAULT_DRIVER = PHANTOMJS;

    private static Set<String> allowedDriverNames = new HashSet();
    static {
        allowedDriverNames.addAll(Arrays.asList(FIREFOX,CHROME,PHANTOMJS));
    }

    private String driverString, webDriverPath, proxy, driverPath, driverName;
    private Date startDate;

    private WebDriver driver;
    private DesiredCapabilities capabilities;
    private Dimension windowSize = new java.awt.Dimension(1440,900);
    private Point windowPosition = new Point(0,0);

    public DriverController(String driverString, String webDriverPath, String proxy, String driverPath, String driverName, Dimension windowSize, Point windowPosition) {
        this.driverName = UUID.randomUUID().toString();
        this.driverString = driverString;
        this.webDriverPath = webDriverPath;
        this.proxy = proxy;
        this.driverPath = driverPath;
        this.driverName = driverName;
        this.windowSize = windowSize;
        this.windowPosition = windowPosition;
        this.startDate = new Date();

        init(driverString,webDriverPath);
    }

    public DriverController(){init(null, null);}
    public DriverController(String driverName){init(driverName, null);}
    public DriverController(String driverName, String webDriverPath){init(driverName, webDriverPath);}
    private void init(String driverType, String webDriverPath){
        if(this.driverName == null)
            this.driverName = UUID.randomUUID().toString();
        this.startDate = new Date();

        DatabaseCommons.registerDriver(this);

        setDriverType(driverType);
        setWebDriverPath(webDriverPath);
    }



    public void setDriverType(String driverType){
        String driverNameValue = DEFAULT_DRIVER;
        if(driverString != null)
            driverNameValue = driverString.toLowerCase();
        if("firefox".equalsIgnoreCase(driverNameValue))
            driverNameValue = GECKO;
        if(!allowedDriverNames.contains(driverNameValue))
            throw new IllegalArgumentException("DriverController Type: "+driverNameValue+" is not an allowed type. \nAllowed Types: "+allowedDriverNames);

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

    public Date getStartDate() {
        return startDate;
    }

    public void takeScreenShot(String output) throws IOException {
        File scrShot = ((TakesScreenshot)getDriver()).getScreenshotAs(OutputType.FILE);
        FileOptions.copyFileUtil(scrShot, new File(output));
    }

    public Object executeJavaScript(String javascript){
        return ((JavascriptExecutor) getDriver()).executeScript(javascript);
    }

    public boolean waitForElement(By by, long time, TimeUnit timeUnit){
        ExecutorService exec = Executors.newSingleThreadExecutor();
        boolean b = true;
        try {
            exec.submit(new Callable<Boolean>(){
                @Override public Boolean call() throws Exception {
                    return _waitForText(by);
                }
            }).get(time, timeUnit);
            exec.shutdown();
            exec.awaitTermination(time, timeUnit);
        } catch (InterruptedException | ExecutionException | java.util.concurrent.TimeoutException e) {
            b = false;
        }finally{
            exec.shutdownNow();
        }
        return b;

    }private boolean _waitForText(By by){
        List<WebElement> e = driver.findElements(by);
        while((e = driver.findElements(by)).size() == 0)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) { /* DO NOTHING */ }
        return true;
    }

    public void clickWait(By by, long time, TimeUnit timeUnit){
        clickWait(by, 0, time, timeUnit);
    }public void clickWait(By by, int index, long time, TimeUnit timeUnit){
        waitForElement(by,time, timeUnit);
        getDriver().findElements(by).get(index).click();
    }
    public WebElement getElementWait(By by, long time, TimeUnit timeUnit){
        return getElementWait(by, 0, time, timeUnit);
    }public WebElement getElementWait(By by, int index, long time, TimeUnit timeUnit){
        waitForElement(by,time, timeUnit);
        return getDriver().findElements(by).get(index);
    }

    public String getDriverNameValue(){
        return this.driverName;
    }public void setDriverNameValue(String driverName){
        this.driverName = driverName;
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
        setDriverType(driverName != null && driverName.length == 1 ? driverName[0] : null);
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

        driverPath = path+driverValue;
        if(!PHANTOMJS.equalsIgnoreCase(driverString))
            System.setProperty("webdriver."+driverString+".driver",driverPath);
        else
            System.setProperty("phantomjs.binary.path",driverPath);

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
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, driverPath);
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DriverController driverController1 = (DriverController) o;

        if (driverString != null ? !driverString.equals(driverController1.driverString) : driverController1.driverString != null)
            return false;
        if (webDriverPath != null ? !webDriverPath.equals(driverController1.webDriverPath) : driverController1.webDriverPath != null)
            return false;
        if (proxy != null ? !proxy.equals(driverController1.proxy) : driverController1.proxy != null) return false;
        if (driverPath != null ? !driverPath.equals(driverController1.driverPath) : driverController1.driverPath != null) return false;
        if (driver != null ? !driver.equals(driverController1.driver) : driverController1.driver != null) return false;
        if (capabilities != null ? !capabilities.equals(driverController1.capabilities) : driverController1.capabilities != null)
            return false;
        if (windowSize != null ? !windowSize.equals(driverController1.windowSize) : driverController1.windowSize != null) return false;
        return windowPosition != null ? windowPosition.equals(driverController1.windowPosition) : driverController1.windowPosition == null;
    }

    @Override
    public int hashCode() {
        int result = driverString != null ? driverString.hashCode() : 0;
        result = 31 * result + (webDriverPath != null ? webDriverPath.hashCode() : 0);
        result = 31 * result + (proxy != null ? proxy.hashCode() : 0);
        result = 31 * result + (driverPath != null ? driverPath.hashCode() : 0);
        result = 31 * result + (driver != null ? driver.hashCode() : 0);
        result = 31 * result + (capabilities != null ? capabilities.hashCode() : 0);
        result = 31 * result + (windowSize != null ? windowSize.hashCode() : 0);
        result = 31 * result + (windowPosition != null ? windowPosition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DriverController{" +
                "driverString='" + driverString + '\'' +
                ", webDriverPath='" + webDriverPath + '\'' +
                ", proxy='" + proxy + '\'' +
                ", driverPath='" + driverPath + '\'' +
                ", driverName='" + driverName + '\'' +
                ", startDate=" + startDate +
                ", driver=" + driver +
                ", capabilities=" + capabilities +
                ", windowSize=" + windowSize +
                ", windowPosition=" + windowPosition +
                '}';
    }
}

