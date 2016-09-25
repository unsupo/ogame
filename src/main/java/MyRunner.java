import java.io.IOException;

import objects.AI;
import objects.Scavenger;
import utilities.selenium.Task;

public class MyRunner {

	public static void main(String[] args) throws IOException{
		AI ai = new Scavenger();
		while(true){
			Task nextTask = ai.getTask();
			if(nextTask == Task.quitTask) break;
		}
	}
}
