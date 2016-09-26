package utilities.selenium;

import ogame.pages.Action;
import ogame.pages.Overview;
import ogame.utility.Initialize;
import ogame.utility.Resource;
import utilities.Utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task {

	private Runnable runnable;
	
	public Task(Runnable runnable){
		this.runnable = runnable;
	}
	
	private Task(){};
	
	
	public void execute(){
		runnable.run();
	}
	
	public static Task getBuyResearchTask(String name){
		Runnable researchRunnable = new Runnable(){

			@Override
			public void run() {
				
			}
			
		};
		return new Task(researchRunnable);
	}
	
	public static Task login(String universe, String username, String password){
		Runnable loginRun = new Runnable(){

			@Override
			public void run() {
				try {
					Initialize.login(universe, username, password);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		};
		return new Task(loginRun);
	}


	public static void readResearch() throws IOException {
		Initialize.getInstance().getResearch();
	}
	
	//TODO: only safe in overview page for now, need to fix
	public static Map<String, Boolean> checkCurrentConstruction() throws IOException {
		Utility.clickOnNewPage(Overview.OVERVIEW);
		Map<String, Boolean> constructionMap = new HashMap<String, Boolean>();
		List<String> texts = UIMethods.getTextsFromAttributeAndValue("class", "construction active");
		if(texts.size() > 2){
			constructionMap.put(Overview.QUEUE_BUILDINGS, !texts.get(0).contains("No "));
			constructionMap.put(Overview.QUEUE_RESEARCH, !texts.get(1).contains("no "));
			constructionMap.put(Overview.QUEUE_SHIPYARD, !texts.get(2).contains("No "));
		}
		return constructionMap;
	}
	
	public static Resource readResource(){
		return Utility.readResource();
	}
	
	public static int readDarkMatter(){
		return Utility.readDarkMatter();
	}
	
	public static Task quitTask = new Task();
	
	public static void build(String nextBuild, long num) throws IOException {
		String type = Initialize.getType(nextBuild);
        UIMethods.clickOnText(type);
        Utility.clickAction("id", nextBuild);
        UIMethods.typeOnAttributeAndValue("id", "number", num + "");
        new Action().clickOnStartWithDM();
        UIMethods.clickOnAttributeAndValue("class", "build-it");
	}

	public static void build(String nextBuild) throws IOException {
		build(nextBuild, 1);
	}

}
