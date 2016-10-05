package ogame.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import utilities.one.Email;
import utilities.selenium.UIMethods;

/**
 * Created by jarndt on 9/19/16.
 */
public class Login{
	
	public static String emailAppend = "@michaelgutin.one";
	
	public static void main(String[] args) throws Exception {
        if(args != null && args.length == 1)
            createNewAccount(args[0]);
        else
        	System.out.println("Must give a name 6 characters long");
		
//		try {
//			String user = "test";
//			for(int i=0;i<6;i++){
//				user = user + (int)(Math.random()*9);
//			}
//			System.out.println(user);
//			Email.create(user, user);
//			verify(user);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		verify("coolerthanyou");

//	    createNewAccount("a1234567");
//
//        String name = "a1234567";
//        new Login().register(name+emailAppend, name);
	}
	
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
    
    public void register(String email, String password){
    	String username = email.split("@")[0];
    	WebDriver driver = UIMethods.getWebDriver();
        driver.navigate().to("http://ogame.org");
        UIMethods.typeOnAttributeAndValue("id","username",username);
        UIMethods.typeOnAttributeAndValue("id","password",password);
        UIMethods.typeOnAttributeAndValue("id", "email", email);
        UIMethods.clickOnAttributeAndValue("id", "agb");
        UIMethods.clickOnAttributeAndValue("id","regSubmit");
    }
    
    public static void verify(String name){
    	UIMethods.getWebDriver().navigate().to("https://mail.one.com/");
        UIMethods.typeOnAttributeAndValue("name", "displayUsername", name+emailAppend);
        UIMethods.typeOnAttributeAndValue("type", "password", name);
        UIMethods.clickOnAttributeAndValue("class", "oneButton");
        int count = 0;
        while (count++ < 60000 && !UIMethods.doesPageContainText("OGame password")){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ((JavascriptExecutor)UIMethods.getWebDriver()).executeScript("document.getElementsByClassName(\"overlay-container active\")[0].className = \"overlay-container\"");

        UIMethods.clickOnText("OGame password");
        count = 0;
        while (count++ < 1000 && !UIMethods.doesPageContainAttributeAndValue("class","mailPart")){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        UIMethods.clickOnAttributeAndValue("target","_blank");
    }

    public Overview reLogin(String uni, String username, String password) {
        UIMethods.clickOnAttributeAndValue("id", "loginBtn");
        UIMethods.selectFromDropDown("id", "serverLogin", uni); //"s117-en.ogame.gameforge.com"
        UIMethods.typeOnAttributeAndValue("id", "usernameLogin", username);
        UIMethods.typeOnAttributeAndValue("id", "passwordLogin", password);
        UIMethods.clickOnAttributeAndValue("id", "loginSubmit");
        return new Overview();
    }

    public static void createNewAccount(String name) throws Exception{
    	if(name.length() < 8){
    		throw new Exception("Must be 8 letters long or longer");
    	}
    	Email.create(name, name);
    	new Login().register(name+emailAppend, name);
        UIMethods.getInstance().chrome.close();
        UIMethods.getInstance().chrome = null;
        verify(name);
    }
    
    
}
