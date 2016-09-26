package objects;

import java.io.IOException;

import ogame.utility.Resource;

public class Producer {

	public static Resource getProduction(String type, int level){
		return Resource.getMonoResource(type, (int)(30 + (5*30*level*Math.pow(1.1, level))));
	}
	
	public static Resource getCost(String type, int level) throws IOException{
		return Resource.getCost(type, level);
	}
	
	public static Resource getImproveProduction(String type, int level) throws IOException{
		return Resource.getCost(type, level).subtract(Resource.getCost(type, level - 1));
	}
	
}
