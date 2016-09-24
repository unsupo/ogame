package objects;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ogame.utility.Resource;
import utilities.Utility;

/**
 * Created by jarndt on 9/19/16.
 */
public class Player {

	public static Player self = new Player();
	
	public Map<String, Integer> researches;
	
	public Map<String, Boolean> curConstruction;
	
	public Resource resources = new Resource(0,0,0,0);
	
	public int darkMatter = 0;
	
	public boolean isBusy(String constructionType){
		return curConstruction.get(constructionType);
	}
	
	public Map<String, Integer> getMissingResearch(Map<String, Integer> requirements){
		HashMap<String, Integer> missingMap = new HashMap<String, Integer>();
		for(String requirement: requirements.keySet()){
			Integer cur = researches.get(requirement);
			if(cur == null){
				cur = 0;
			}
			int missing = requirements.get(requirement) - cur;
			if(missing > 0){
				missingMap.put(requirement, missing);
			}
		}
		return missingMap;
	}
	
	public boolean hasResearch(Map<String, Integer> requirements){
		return getMissingResearch(requirements).keySet().size() == 0;
	}
	
	public String getNextResearchFor(String buildable) throws IOException{
		Map<String, Integer> missing = getMissingResearch(Utility.getResearchRequirements(buildable));
		for(String requirement : missing.keySet()){
			Resource cost = Resource.getCost(requirement, researches.get(requirement)+1);
			if(resources.canAfford(cost)){
				return requirement;
			}
		}
		return null;
	}
}
