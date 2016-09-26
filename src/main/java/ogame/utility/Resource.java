package ogame.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import objects.Ship;
import ogame.pages.Facilities;
import ogame.pages.Research;
import ogame.pages.Resources;

public class Resource {
	
	public static final String METAL = "metal";
	public static final String CRYSTAL = "crystal";
	public static final String DEUTERIUM = "deuterium";
	public static final String ENERGY = "energy";

	public long metal;
	public long crystal;
	public long deuterium;
	public long energy = 0;
	
	private static List<String> names;
	private static final List<Resource> baseCosts;
	
	static{
		names = new ArrayList<String>();
		names.addAll(Arrays.asList(Research.names));
		names.addAll(Arrays.asList(Facilities.names));
		names.addAll(Arrays.asList(Ship.names));
		names.addAll(Arrays.asList(Resources.names));
	}
	
	static{
		baseCosts = new ArrayList<Resource>();
		baseCosts.addAll(Arrays.asList(Research.baseCosts));
		baseCosts.addAll(Arrays.asList(Facilities.baseCosts));
		baseCosts.addAll(Arrays.asList(Ship.baseCosts));
		baseCosts.addAll(Arrays.asList(Resources.baseCosts));
	}

	public static Resource getCost(String name) throws IOException{
		return getCost(name, 1);
	}
	
	public static Resource getMonoResource(String type, int amount){
		if(type == METAL || type == Resources.METAL_MINE){
			return new Resource(amount,0,0);
		}
		if(type == CRYSTAL || type == Resources.CRYSTAL_MINE){
			return new Resource(0,amount,0);
		}
		if(type == DEUTERIUM || type == Resources.DUETERIUM_SYNTHESIZER){
			return new Resource(0,0,amount);
		}
		if(type.equals(ENERGY)){
			return new Resource(0,0,0,amount);
		}
		return null;
	}
	
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
	private static Resource getCumulativeCost(Resource base, int min, int max, double power){
		min--;
		Resource cost = base.multiply((Math.pow(power, max)-Math.pow(power, min))/(power-1));
		if(cost.energy > 0){
			cost.energy = (long) Math.ceil(base.energy*(max*Math.pow(1.1, max) - Math.pow(1.1, min)*min));
		}
		return cost;
	}
	
	public static Resource getBaseCost(String name) {
		return baseCosts.get(getIndexOf(name));
	}
	
	private static int getIndexOf(String name){
		for(int i =0;i< names.size();i++){
			if(names.get(i) == name){
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
		return goal.removeNegative().subtract(this.removeNegative()).removeNegative();
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
