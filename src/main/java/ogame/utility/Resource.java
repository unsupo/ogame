package ogame.utility;

import ogame.pages.Facilities;
import ogame.pages.Research;
import ogame.pages.Resources;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

public class Resource {

	public long metal;
	public long crystal;
	public long deuterium;
	public long energy = 0;
	
	private static final String[] names = (String[])ArrayUtils.addAll(Research.names, Facilities.names);
	private static final Resource[] baseCosts = (Resource[]) ArrayUtils.addAll(Research.baseCosts, Facilities.baseCosts);

	
	public static Resource getCost(String name, int level) throws IOException{
		return getCumulativeCost(name, level, level);
	}
	
	public static Resource getCumulativeCost(String name, int level) throws IOException{
		return getCumulativeCost(name, 1, level);
	}

	public static Resource getCumulativeCost(String name, int min, int max) throws IOException{
		double multiplier = 2;
		switch (name){
			case Research.ASTROPHYSICS: 			multiplier = 1.75; break;
			case Resources.METAL_MINE:				//fall through
			case Resources.DUETERIUM_SYNTHESIZER:	//fall through
			case Resources.SOLAR_PLANET: 			multiplier = 1.5; break;
			case Resources.CRYSTAL_MINE: 			multiplier = 1.6; break;
			case Resources.FUSION_REACTOR: 			multiplier = 1.8; break;
		}
		return getCumulativeCost(getBaseCost(name), min, max, multiplier);
	}
	
	private static Resource getCumulativeCost(Resource base, int min, int max){
		return base.multiply(Math.pow(2, max)-Math.pow(2, min -1));
	}
	private static Resource getCumulativeCost(Resource base, int min, int max, double power){
		return base.multiply(Math.pow(power, max)-Math.pow(power, min -1));
	}
	
	public static Resource getBaseCost(String name) {
		return baseCosts[getIndexOf(name)];
	}
	
	private static int getIndexOf(String name){
		for(int i =0;i< names.length;i++){
			if(names[i] == name){
				return i;
			}
		}
		return -1;
		
	}

	public static Resource[] convertCosts(long[] costs){
		Resource[] resources = new Resource[costs.length/4];
		for(int i=0;i<costs.length;i+=4){
			resources[i/4] = new Resource(costs[i], costs[i+1], costs[i+2], costs[i+3]);
		}
		return resources;
	}
	
	
	public Resource(long metal, long crystal, long deuterium){
		this.metal = metal;
		this.crystal = crystal;
		this.deuterium = deuterium;
	}public Resource(long metal, long crystal, long deuterium, long energy){
		this.metal = metal;
		this.crystal = crystal;
		this.deuterium = deuterium;
		this.energy = energy;
	}
	
	public Resource add(Resource other){
		return new Resource(metal + other.metal, crystal + other.crystal, deuterium + other.deuterium, energy + other.energy);
	}
	
	public Resource subtract(Resource other){
		return new Resource(metal - other.metal, crystal - other.crystal, deuterium - other.deuterium, energy - other.energy);
	}
	
	public Resource getDeficit(Resource goal){
		return goal.subtract(this).removeNegative();
	}
	
	private Resource removeNegative(){
		return new Resource(Math.max(0, metal), Math.max(0, crystal), Math.max(0, deuterium), Math.max(0, energy));
	}
	
	public boolean isZero(){
		return metal == 0 && crystal == 0 && deuterium == 0 && energy == 0;
	}
	
	public boolean canAfford(Resource other){
		return getDeficit(other).isZero();
	}
	
	public Resource multiply(int multiple){
		return multiply((double) multiple);
	}
	
	
	private Resource multiply(double multiple) {
		return new Resource((long)(metal * multiple),(long)(crystal * multiple),(long)(deuterium * multiple), (long)(energy*multiple));
	}
	
	public String toString(){	
		return "Metal: " + metal + "\nCrystal: " + crystal + "\nDeuterium: " + deuterium + "\nEnergy: " + energy;
	}
	
	public long numAffordable(Resource cost){
		long metalAmount = cost.metal == 0? Long.MAX_VALUE : metal/cost.metal;
		long crystalAmount = cost.crystal == 0? Long.MAX_VALUE : crystal/cost.crystal;
		long deuteriumAmount = cost.deuterium == 0? Long.MAX_VALUE : deuterium/cost.deuterium;
		long energyAmount = cost.energy == 0? Long.MAX_VALUE : energy/cost.energy;
		return Math.min(energyAmount, Math.min(deuteriumAmount, Math.min(metalAmount, crystalAmount)));
	}
	
	public Resource clone(){
		return new Resource(metal, crystal, deuterium, energy);
	}
}
