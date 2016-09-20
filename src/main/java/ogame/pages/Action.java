package ogame.pages;

import utilities.selenium.UIMethods;

import java.util.concurrent.TimeUnit;

/**
 * Created by jarndt on 9/19/16.
 */
public class Action {
    public String purchase = "Purchase Dark Matter", tearDown = "Tear down", improve = "Improve", startWithDM = "build-it_premium", premiumConfirm = "premiumConfirmButton";

    public void clickOnPurchase() {
        UIMethods.clickOnText(purchase);
    }

    public void clickOnTearDown() {
        UIMethods.clickOnText(tearDown);
    }

    public void clickOnImprove() {
        UIMethods.clickOnText(improve);
    }

    public void clickOnStartWithDM() {
        UIMethods.waitForText("Start with DM",1, TimeUnit.MINUTES);
        UIMethods.clickOnAttributeAndValue("class",startWithDM);
        clickOnYesStartWithDM();
    }

    public void clickOnYesStartWithDM(){
        UIMethods.waitForText("Start now",1, TimeUnit.MINUTES);
        UIMethods.clickOnAttributeAndValue("id",premiumConfirm);
    }
}
