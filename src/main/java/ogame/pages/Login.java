package ogame.pages;

import org.openqa.selenium.WebDriver;
import utilities.selenium.UIMethods;

/**
 * Created by jarndt on 9/19/16.
 */
public class Login{
    public Overview login(String uni, String username, String password){
        WebDriver driver = UIMethods.getWebDriver();
        driver.navigate().to("http://ogame.org");
        UIMethods.clickOnAttributeAndValue("id","loginBtn");
        UIMethods.selectFromDropDown("id","serverLogin",uni); //"s117-en.ogame.gameforge.com"
        UIMethods.typeOnAttributeAndValue("id","usernameLogin",username);
        UIMethods.typeOnAttributeAndValue("id","passwordLogin",password);
        UIMethods.clickOnAttributeAndValue("id","loginSubmit");
        return new Overview();
    }
}
