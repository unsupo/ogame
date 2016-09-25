package objects.ai;

import utilities.selenium.Task;

import java.io.IOException;

public interface AI {

	public Task getDefaultTask();
	
	public Task getTask() throws IOException;
	
	public Task getAttackedTask();
}
