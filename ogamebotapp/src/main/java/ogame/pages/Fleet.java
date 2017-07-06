package ogame.pages;

import bot.Bot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import utilities.webdriver.DriverController;

import java.util.concurrent.*;

/**
 * Created by jarndt on 5/30/17.
 */
public class Fleet implements OgamePage{
    public static final String FLEET = "FleetInfo";

    @Override
    public String getPageName() {
        return FLEET;
    }
    @Override
    public String getXPathSelector() {
        return "//*[@id='menuTable']/li[8]/a/span";
    }

    @Override
    public String getCssSelector() {
        return "#menuTable > li:nth-child(8) > a";
    }

    @Override
    public String uniqueCssSelector() {
        return getCssSelector()+".selected";
    }

    @Override
    public boolean isPageLoaded(DriverController driverController) {
        return driverController.getDriver().findElements(By.cssSelector("#button203")).size() > 0 ||
                driverController.getDriver().findElements(By.cssSelector("#warning > p > span")).size() > 0;
    }

    @Override
    public boolean waitForPageToLoad(DriverController driverController, TimeUnit timeUnit, long l) {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        boolean b = true;
        try {
            exec.submit(new Callable<Boolean>(){
                @Override public Boolean call() throws Exception {
                    return _waitForText(driverController);
                }
            }).get(l, timeUnit);
            exec.shutdown();
            exec.awaitTermination(l, timeUnit);
        } catch (InterruptedException | ExecutionException | java.util.concurrent.TimeoutException e) {
            b = false;
        }finally{
            exec.shutdownNow();
        }
        return b;
    }private boolean _waitForText(DriverController driverController){
        while(!isPageLoaded(driverController))
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) { /* DO NOTHING */ }
        return true;
    }

    @Override
    public void parsePage(Bot b, Document document) {
        //TODO expedition, dueterium cost
        String[] fleets = document.select("#slots > div:nth-child(1) > span").text().replaceAll("[A-Za-z: ]","").trim().split("/");
        int fleetSlots = Integer.parseInt(fleets[1]), used = Integer.parseInt(fleets[0]);
        b.getFleetInfo().setFleetsTotal(fleetSlots);
        b.getFleetInfo().setFleetsUsed(used);
    }
}
