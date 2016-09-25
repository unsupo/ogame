package objects;

import ogame.pages.Overview;
import ogame.utility.Resource;
import utilities.Utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jarndt on 9/19/16.
 */
public class Player {

	public static Player self = new Player();
	
	public Map<String, Integer> researches;
	
	public Map<String, Integer> facilities;
	
	public Map<String, Map<String, Integer>> buildables;
	
	public Map<String, Boolean> curConstruction;
	
	public Resource resources = new Resource(0,0,0,0);
	
	public int darkMatter = 0;

	public Player(){
		buildables = new HashMap<String, Map<String, Integer>>();
		buildables.put(Overview.RESEARCH, researches);
		buildables.put(Overview.FACILITIES, facilities);
	}
	
	
	
	
	public boolean isBusy(String constructionType){
		return curConstruction.containsKey(constructionType);
	}
	
	private Map<String,Integer> getBuildablesAsSimpleMap(){
		HashMap<String, Integer> simpleMap = new HashMap<String,Integer>();
		for(String key : buildables.keySet()){
			Map<String, Integer> buildablesSubMap = buildables.get(key);
			for(String subKey : buildablesSubMap.keySet()){
				simpleMap.put(subKey, buildablesSubMap.get(subKey));
			}
		}
		return simpleMap;
	}
	
	
	public Map<String, Integer> getMissingFacilities(Map<String, Integer> requirements){
		return Buildable.getMissing(requirements, facilities);
	}
	
	public Map<String, Integer> getMissingResearch(Map<String, Integer> requirements){
		return Buildable.getMissing(requirements, researches);
	}
	
	public boolean hasResearch(Map<String, Integer> requirements){
		return getMissingResearch(requirements).keySet().size() == 0;
	}
	
	public String getNextResearchFor(String buildable) throws IOException{
		return getNextFor(buildable, Utility.getResearchRequirements(buildable), researches);
	}
	
	public Map<String, Integer> getMissing(String goal){
		return Buildable.getMissing(Utility.getBuildableRequirements(goal), getBuildablesAsSimpleMap());
	}
	
	public String getNextBuildableFor(String buildable) throws IOException{
		String res = getNextResearchFor(buildable);
		if(res == null){
			return getNextFacilityFor(buildable);
		}
		return res;
	}
	
	private String getNextFor(String buildable, Map<String,Integer> requirements, Map<String, Integer> current) throws IOException{
		if(current == null){
			return null;
		}
		Map<String, Integer> missing = Buildable.getMissing(requirements, current);
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
	
	public boolean canAfford(String goal) throws IOException{
		return resources.canAfford(Resource.getCost(goal));
	}
	
	public boolean canAfford(Resource cost){
		return resources.canAfford(cost);
	}
	
	
	public boolean canMake(String goal) throws IOException{
		return getMissing(goal).isEmpty() && canAfford(goal);
	}
	
	public long numAffordable(Resource cost){
		return resources.numAffordable(cost);
	}
}
