package objects;

import java.io.IOException;

import ogame.pages.OGamePage;
import ogame.pages.Overview;
import ogame.pages.Research;
import ogame.utility.Initialize;
import utilities.Utility;
import utilities.selenium.Task;

public class Scavenger implements AI {
	

	public boolean login = false;
	
	@Override
	public Task getDefaultTask() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task getTask() throws IOException {
		//login check
		if(!login){
			Initialize.justLogin("s117-en.ogame.gameforge.com", "mgutin", "1bobbill");			
			Player.self.resources = Task.readResource();
			Player.self.darkMatter = Task.readDarkMatter();
			login = true;
		}
		Player.self.curConstruction = Task.checkCurrentConstruction();
		//check research requirments if not already researching
		if(!Player.self.isBusy(Overview.RESEARCH)){
			new OGamePage().clickOnResearch();
			if(Player.self.researches == null){
				Player.self.researches = Initialize.getInstance().getResearch();
				System.out.println("Researches : " + Player.self.researches);
			}
//			//check if research needed
//			if(!Player.self.hasResearch(Utility.getResearchRequirements(Ship.SMALL_CARGO))){
//				String nextResearch = Player.self.getNextResearchFor(Ship.SMALL_CARGO);
//				if(nextResearch != null){
//					Task.research(nextResearch);
//					
//				}
//			}
			Task.research(Research.ION);
			new OGamePage().clickOnOverview();
		}else{
			System.out.print(".");
		}

		return null;
	}
	

	@Override
	public Task getAttackedTask() {
		// TODO Auto-generated method stub
		return null;
	}
}
