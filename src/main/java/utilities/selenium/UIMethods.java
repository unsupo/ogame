package utilities.selenium;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import utilities.OSProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jarndt on 9/19/16.
 */
public class UIMethods {

    private static String ss = OSProperties.SEPERATOR;

    public static final String 	SEPERATOR 		= System.getProperty("file.separator"),
                                PATH 			= System.getProperty("user.dir")+SEPERATOR;

    public static String 		CHROME_DRIVER_PATH 	= PATH+"src"+SEPERATOR+"main"+SEPERATOR+"resources"+SEPERATOR+"web_drivers"+SEPERATOR,
                                PHANTOM_PATH        = OSProperties.PATH+ss+"src"+ss+"main"+ss+"resources"+ss+"web_drivers"+ss,
                                DATA_XLSX 			= PATH+"data"+SEPERATOR+"testdata.xlsx";

    private static UIMethods instance;
    public WebDriver chrome;
    private String phantomPath;
    private boolean headless;



    private UIMethods(){
        setProperty();
    }
    private void setProperty() {
        if(OSProperties.isWindows()){
            System.setProperty("webdriver.chrome.driver",CHROME_DRIVER_PATH+"chromedriver.exe");
            phantomPath = PHANTOM_PATH+"phantomjs.exe";
        }else if(OSProperties.isMac()){
            System.setProperty("webdriver.chrome.driver",CHROME_DRIVER_PATH+"chromedriver_mac");
            phantomPath = PHANTOM_PATH+"phantomjs_mac";
        }else if(OSProperties.isUnix()){
            System.setProperty("webdriver.chrome.driver",CHROME_DRIVER_PATH+"chromedriver_linux");
            phantomPath = PHANTOM_PATH+"phantomjs_linux";
        }
    }

    public static UIMethods getInstance(){
        if(instance == null)
            instance = new UIMethods();
        return instance;
    }

    private DesiredCapabilities getCaps(){
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        String[] args1 = {"--ignore-ssl-errors=yes","--webdriver-loglevel=NONE"};
        caps.setJavascriptEnabled(true);
        caps.setCapability("takesScreenshot", true);
        caps.setCapability("screen-resolution", "1280x1024");
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, args1);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "loadImages", true);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "javascriptEnabled", true);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "resourceTimeout", 60000);
        caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 5.1; rv:22.0) Gecko/20100101 Firefox/22.0");
//	    caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] {"--webdriver-loglevel=ERROR"});//NONE,ERROR
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomPath);
        Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
        return caps;
    }

    public static WebDriver getWebDriver(){
        return getInstance()._getWebDriver();
    }
    private WebDriver _getWebDriver() {
        if(chrome == null){
            setProperty();
            chrome = headless?new PhantomJSDriver(getCaps()): new FirefoxDriver();//new ChromeDriver();
            WebDriverRunner.setWebDriver(chrome);
        }
        chrome.manage().window().maximize();
        return chrome;
    }


    public static WebElement clickOnText(String text){
        List<WebElement> e = getWebDriver().findElements(By.xpath("//*[text()='"+text+"']"));
        for(WebElement el : e)
            try{
                el.click();
                return el;
            }catch(Exception ee){
                return _clickOnText(text, getWebDriver(), 0);
            }
        return null;
    }public static WebElement _clickOnText(String text, WebDriver driver, int level){
        String path = "//*[contains(text(),'"+text+"')]/parent::*";
        for(int i = 0; i<level; i++)
            path+="/parent::*";

        List<WebElement> e = driver.findElements(By.xpath(path));
        for(WebElement el : e)
            try{
                el.click();
                return el;
            }catch(Exception ee){
                return _clickOnText(text, driver, ++level);
            }
        return null;
    }
    
    public static WebElement clickOnAttributeAndValue(String name, String value) {
    	return clickOnAttributeAndValue(name, value, 0);
    }
    
    public static WebElement clickOnAttributeAndValue(String name, String value, int index) {
        try{
            List<WebElement> list = getWebDriver().findElements(By.xpath("//*[@"+name+"='"+value+"']"));
            list.get(index).click();
            return list.get(index);
        }catch(Exception e){
            return _clickOnAttributeAndValue(name, value, getWebDriver(), 0);
        }
    }
    
    
    public static WebElement submitOnAttributeAndValue(String name, String value, int index) {
        try{
            List<WebElement> list = getWebDriver().findElements(By.xpath("//*[@"+name+"='"+value+"']"));
            list.get(index).submit();
            return list.get(index);
        }catch(Exception e){
            return _clickOnAttributeAndValue(name, value, getWebDriver(), 0);
        }
    }
    
    private static WebElement _clickOnAttributeAndValue(String name, String value, WebDriver driver, int level) {
        String path = "//*[@"+name+"='"+value+"']/parent::*";
        for(int i = 0; i<level; i++)
            path+="/parent::*";

        List<WebElement> e = driver.findElements(By.xpath(path));
        for(WebElement el : e)
            try{
                el.click();
                return el;
            }catch(Exception ee){
                return _clickOnAttributeAndValue(name, value, driver, ++level);
            }
        return null;
    }

    public static WebElement typeOnAttributeAndValue(String name, String value, String text) {
        try{
            WebElement e = getWebDriver().findElement(By.xpath("//*[@"+name+"='"+value+"']"));//.click();
            e.click();
            e.clear();
            e.sendKeys(text);
            return e;
        }catch(Exception e){
            return _typeOnAttributeAndValue(text, name, value, getWebDriver(), 0);
        }
    }
    private static WebElement _typeOnAttributeAndValue(String text, String name, String value,
                                                       WebDriver driver, int level) {
        String path = "//*[@"+name+"='"+value+"']/parent::*";
        for(int i = 0; i<level; i++)
            path+="/parent::*";

        List<WebElement> e = driver.findElements(By.xpath(path));
        for(WebElement el : e)
            try{
                el.click();
                el.clear();
                el.sendKeys(text);
                return el;
            }catch(Exception ee){
                return _typeOnAttributeAndValue(text, name, value, driver, ++level);
            }
        return null;
    }

    public static Select selectFromDropDown(String text, String dropDownValue){
        Select select = new Select(getWebDriver().findElement(By.xpath("//*[text()='"+text+"']")));
        select.deselectAll();
        select.selectByVisibleText(dropDownValue);
        return select;
    }public static Select selectFromDropDown(String name, String value, String dropDownValue){
        Select select = new Select(getWebDriver().findElement(By.xpath("//*[@"+name+"='"+value+"']")));
        select.selectByValue(dropDownValue);
        return select;
    }

    public boolean isPageHasText(String text, long timeLength, TimeUnit timeunit){
        return waitForText(text, timeLength, timeunit);
    }
    public static boolean waitForText(String text, long i,
                                      TimeUnit timeunit) {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        boolean b = true;
        try {
            exec.submit(new Callable<Boolean>(){
                @Override public Boolean call() throws Exception {
                    return waitForText(text, getWebDriver());
                }
            }).get(i, timeunit);
            exec.shutdown();
            exec.awaitTermination(i, timeunit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            b = false;
        }finally{
            exec.shutdownNow();
        }
        return b;
    }public static boolean waitForText(String text, WebDriver driver) {
        List<WebElement> e = driver.findElements(By.xpath("//*[contains(text(),'"+text+"')]"));
        while((e = driver.findElements(By.xpath("//*[contains(text(),'"+text+"')]"))).isEmpty())
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {

            }
        return true;
    }

    public static String getTextFromAttributeAndValue(String attribute, String value){
    	List<String> texts = getTextsFromAttributeAndValue(attribute, value);
        return texts == null || texts.size() == 0 ? null : texts.get(0);
    }
    
    public static List<String> getTextsFromAttributeAndValue(String attribute, String value){
        List<WebElement> els = getWebDriver().findElements(By.xpath("//*[@"+attribute+"='"+value+"']"));//.click();
        List<String> texts = new ArrayList<String>();
        for(WebElement el : els ){
        	texts.add(el.getText());
        }
        return texts;
    }

}
