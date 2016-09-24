package utilities.selenium;

import java.io.IOException;

import ogame.utility.Initialize;

public class Task {

	private Runnable runnable;
	
	public Task(Runnable runnable){
		this.runnable = runnable;
	}
	
	
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


	public static void readResearch() {
		
	}
	
}
