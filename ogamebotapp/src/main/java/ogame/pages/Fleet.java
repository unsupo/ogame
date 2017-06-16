package ogame.pages;

import bot.Bot;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import utilities.webdriver.DriverController;

import java.util.concurrent.TimeUnit;

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
        return driverController.getDriver().findElements(By.cssSelector("#button203")).size() > 0;
    }

    @Override
    public boolean waitForPageToLoad(DriverController driverController, TimeUnit timeUnit, long l) {
        return driverController.waitForElement(By.cssSelector("#button203"),l,timeUnit);
    }

    @Override
    public void parsePage(Bot b, Document document) {
        //TODO expedition, dueterium cost
        String[] fleets = document.select("#slots > div:nth-child(1) > span").text().replace("Fleets: ","").trim().split("/");
        int fleetSlots = Integer.parseInt(fleets[1]), used = Integer.parseInt(fleets[0]);
        b.getFleetInfo().setFleetsTotal(fleetSlots);
        b.getFleetInfo().setFleetsUsed(used);
    }
}
