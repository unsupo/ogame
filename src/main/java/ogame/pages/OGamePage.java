package ogame.pages;

import utilities.selenium.UIMethods;

public class OGamePage {


    public Overview clickOnOverview(){
        UIMethods.clickOnText("Overview");
        return new Overview();
    }

    public Resources clickOnResources(){
        UIMethods.clickOnText("Resources");
        return new Resources();//points, relocate, abondon/rename, buddies, notes, highscore, search, options, support, chat, log out, planet[1,2,3...]
    }

    public Facilities clickOnFacilities(){
        UIMethods.clickOnText("Facilities");
        return new Facilities();
    }

    public Merchant clickOnMerchant(){
        UIMethods.clickOnText("Merchant");
        return new Merchant();
    }

    public Research clickOnResearch(){
        UIMethods.clickOnText("Research");
        return new Research();
    }

    public Shipyard clickOnShipyard(){
        UIMethods.clickOnText("Shipyard");
        return new Shipyard();
    }

    public Defence clickOnDefence(){
        UIMethods.clickOnText("Defence");
        return new Defence();
    }

    public Fleet clickOnFleet(){
        UIMethods.clickOnText("Fleet");
        return new Fleet();
    }

    public Galaxy clickOnGalaxy(){
        UIMethods.clickOnText("Galaxy");
        return new Galaxy();
    }

    public Alliance clickOnAllaince(){
        UIMethods.clickOnText("Alliance");
        return new Alliance();
    }

    public RecruitOfficers clickOnRecruitOfficers(){
        UIMethods.clickOnText("Recruit Officers");
        return new RecruitOfficers();
    }

    public Shop clickOnShop(){
        UIMethods.clickOnText("Shop");
        return new Shop();
    }
    
    public OGamePage clickOnPlanet(int index){
    	UIMethods.clickOnAttributeAndValue("class", "planet-name  ", index);
		return new OGamePage();
    }

    public String getPageLoadedConstant(){
    	return null;
    }
}
