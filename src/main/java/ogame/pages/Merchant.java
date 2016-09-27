package ogame.pages;

import ogame.utility.QueueManager;
import org.openqa.selenium.JavascriptExecutor;
import utilities.Utility;
import utilities.filesystem.FileOptions;
import utilities.selenium.UIMethods;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static ogame.utility.Initialize.f;

/**
 * Created by jarndt on 9/19/16.
 */
public class Merchant extends OGamePage{
    public static final String  MERCHANT        = "Merchant", ID = "id",
                                IMPORT_EXPORT   = "js_traderImportExport",
                                AUCTIONEER      = "js_traderAuctioneer",
                                RESOURCE_MARKET = "js_traderResources",
                                SCRAP_MERCHANT  = "js_traderScrap";


    @Override
    public String getPageLoadedConstant() {
        return null;
    }


    public static void getItemOfDay() throws IOException {
        String[] params = QueueManager.getInstance().getLoginParameters();
        String fName = params[1]+"_";
        fName += Utility.getOgniterUniverseNumber(params[0])+"";
        String merchantFile = Utility.PROFILE_DIR+fName+"_merchant";

        String dateValue = LocalDateTime.now().minusMonths(1).format(f);
        if(new File(merchantFile).exists())
            dateValue = FileOptions.readFileIntoString(merchantFile).trim();
        LocalDateTime dateTime = LocalDateTime.from(f.parse(dateValue));
        if(LocalDateTime.now().isBefore(dateTime.plusDays(1)))
            return;

        Utility.clickOnNewPage(MERCHANT);
        UIMethods.clickOnAttributeAndValue("id",IMPORT_EXPORT);
        UIMethods.waitForText("Containers with unknown contents are sold here for resources every day.", 1 , TimeUnit.MINUTES);
        UIMethods.clickOnAttributeAndValue("class","detail_button");
//        UIMethods.clickOnAttributeAndValue("class","js_sliderMetalMax");
        ((JavascriptExecutor)UIMethods.getWebDriver()).executeScript("document.getElementsByClassName(\"js_sliderMetalMax\")[0].click()");
        UIMethods.clickOnAttributeAndValue("class","pay");
        UIMethods.waitForText("Take item", 1 , TimeUnit.MINUTES);
        UIMethods.clickOnText("Take item");


        FileOptions.writeToFileOverWrite(merchantFile, LocalDateTime.now().format(f));
    }
}
