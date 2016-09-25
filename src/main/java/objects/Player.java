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
	
	public Map<String, Integer> facilities;
	
	
	public Map<String, Boolean> curConstruction;
	
	public Resource resources = new Resource(0,0,0,0);
	
	public int darkMatter = 0;

	
	public boolean isBusy(String constructionType){
		return curConstruction.get(constructionType);
	}
	
	private Map<String, Integer> getMissing(Map<String, Integer> requirements, Map<String, Integer> current){
		HashMap<String, Integer> missingMap = new HashMap<String, Integer>();
		for(String requirement: requirements.keySet()){
			Integer cur = current.get(requirement);
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
	
	public Map<String, Integer> getMissingFacilities(Map<String, Integer> requirements){
		return getMissing(requirements, facilities);
	}
	
	public Map<String, Integer> getMissingResearch(Map<String, Integer> requirements){
		return getMissing(requirements, researches);
	}
	
	public boolean hasResearch(Map<String, Integer> requirements){
		return getMissingResearch(requirements).keySet().size() == 0;
	}
	
	public String getNextResearchFor(String buildable) throws IOException{
		return getNextFor(buildable, Utility.getResearchRequirements(buildable), researches);
	}
	
	private String getNextFor(String buildable, Map<String,Integer> requirements, Map<String, Integer> current) throws IOException{
		if(current == null){
			return null;
		}
		Map<String, Integer> missing = getMissing(requirements, current);
		for(String requirement : missing.keySet()){
			Resource cost = Resource.getCost(requirement, current.get(requirement)+1);
			if(resources.canAfford(cost)){
				return requirement;
			}
		}
		return null;
	}

	public String getNextFacilityFor(String buildable) throws IOException {
		return getNextFor(buildable, Utility.getFacilityRequirements(buildable), facilities);
	}
	
	public boolean canAfford(Resource cost){
		return resources.canAfford(cost);
	}
	
	public long numAffordable(Resource cost){
		return resources.numAffordable(cost);
	}
}
