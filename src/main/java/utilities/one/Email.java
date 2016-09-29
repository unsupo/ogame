package utilities.one;

import org.openqa.selenium.WebDriver;
import utilities.selenium.UIMethods;

import java.util.concurrent.TimeUnit;

public class Email {

	public static void main(String[] args){
		create("firsttest","firsttest");
	}
	
	public static void create(String user, String password){
		login();
		UIMethods.typeOnAttributeAndValue("name", "displayUsername", "michael.gutin@gmail.com");
		UIMethods.typeOnAttributeAndValue("type", "password", "1Bobbill!");
		UIMethods.clickOnAttributeAndValue("type", "submit");
		UIMethods.waitForText("Mail Administration", 5, TimeUnit.SECONDS);
		UIMethods.clickOnAttributeAndValue("id", "frontpageMailLink");
		UIMethods.waitForText("New Account",5, TimeUnit.SECONDS);
		UIMethods.clickOnAttributeAndValue("title","New account");
		UIMethods.waitForText("Address", 5, TimeUnit.SECONDS);
		UIMethods.typeOnAttributeAndValue("id", "name", user);
		UIMethods.typeOnAttributeAndValue("id", "mailPassword1", password);
		UIMethods.typeOnAttributeAndValue("id", "mailPassword2", password);
		UIMethods.clickOnAttributeAndValue("name", "SaveNew");
//		UIMethods.clickOnAttributeAndValue("value", "save");

	}
	
	public static void login(){
		  WebDriver driver = UIMethods.getWebDriver();
	      driver.navigate().to("https://login.one.com/");
	}
}
