package objects;

import java.io.IOException;

import ogame.pages.Facilities;
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
		buildMissing(Ship.SMALL_CARGO);
		if(Player.self.canAfford(new Ship().getCost())){
			Task.build(Ship.SMALL_CARGO, Player.self.numAffordable(new Ship().getCost()));
		}

		return null;
	}
	
	public void buildMissingType(String goal, String type, String queueType) throws IOException{
		if(!Player.self.isBusy(queueType)){
			Utility.clickOnNewPage(type);
			if(Player.self.buildables.get(type) == null){
				Player.self.buildables.put(type, Initialize.getInstance().getBuildables(type));
				System.out.println(type + " buildables: " + Player.self.buildables.get(type));
			}
			String nextBuildable = Player.self.getNextBuildableFor(goal);
			System.out.println(nextBuildable);
			if(nextBuildable != null){
				Task.build(nextBuildable);
			}
		}
	}
	
	
	public void buildMissing(String goal) throws IOException{
		Utility.clickOnNewPage("Overview");
		Player.self.curConstruction = Task.checkCurrentConstruction();
		buildMissingType(goal, Overview.RESEARCH, Overview.QUEUE_RESEARCH);
		buildMissingType(goal, Overview.FACILITIES, Overview.QUEUE_BUILDINGS);
	}
	

	@Override
	public Task getAttackedTask() {
		// TODO Auto-generated method stub
		return null;
	}
}
