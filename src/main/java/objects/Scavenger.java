package objects;

import ogame.pages.OGamePage;
import ogame.utility.Initialize;
import utilities.selenium.Task;

public class Scavenger implements AI {
	

	@Override
	public Task getDefaultTask() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task getTask() {
		if(Player.self.researches == null){
			new OGamePage().clickOnResearch();
			System.out.println("Researches : " + Player.self.researches);
		}
		return null;
	}

	@Override
	public Task getAttackedTask() {
		// TODO Auto-generated method stub
		return null;
	}
}
