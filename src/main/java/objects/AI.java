package objects;

import utilities.selenium.Task;

public interface AI {

	public Task getDefaultTask();
	
	public Task getTask();
	
	public Task getAttackedTask();
}
