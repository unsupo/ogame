package objects;

import java.io.IOException;

import utilities.selenium.Task;

public interface AI {

	public Task getDefaultTask();
	
	public Task getTask() throws IOException;
	
	public Task getAttackedTask();
}
