package ogame.utility;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;

import ogame.pages.Facilities;
import ogame.pages.Research;

public class Resource {

	public long metal;
	public long crystal;
	public long deuterium;
	
	private static final String[] names = (String[])ArrayUtils.addAll(Research.names, Facilities.names);
	private static final Resource[] baseCosts = (Resource[]) ArrayUtils.addAll(Research.baseCosts, Facilities.baseCosts);

	
	public static Resource getCost(String name, int level) throws IOException{
		return getCumulativeCost(name, level, level);
	}
	
	public static Resource getCumulativeCost(String name, int level) throws IOException{
		return getCumulativeCost(name, 1, level);
	}

	public static Resource getCumulativeCost(String name, int min, int max) throws IOException{
		if(name.equals(Research.ASTROPHYSICS)){
			
		}else{
			return getCumulativeCost(getBaseCost(name), min, max);
		}
		return null;
	}
	
	private static Resource getCumulativeCost(Resource base, int min, int max){
		return base.multiply(Math.pow(2, max)-Math.pow(2, min -1));
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
		Resource[] resources = new Resource[costs.length/3];
		for(int i=0;i<3;i+=3){
			resources[i/3] = new Resource(costs[i], costs[i+1], costs[i+2]);
		}
		return resources;
	}
	
	
	public Resource(long metal, long crystal, long deuterium){
		this.metal = metal;
		this.crystal = crystal;
		this.deuterium = deuterium;
	}
	
	public Resource add(Resource other){
		return new Resource(metal + other.metal, crystal + other.crystal, deuterium + other.deuterium);
	}
	
	public Resource subtract(Resource other){
		return new Resource(metal - other.metal, crystal - other.crystal, deuterium - other.deuterium);
	}
	
	public Resource getDeficit(Resource goal){
		return goal.subtract(this).removeNegative();
	}
	
	private Resource removeNegative(){
		return new Resource(Math.max(0, metal), Math.max(0, crystal), Math.max(0, deuterium));
	}
	
	public boolean isZero(){
		return metal == 0 && crystal == 0 && deuterium == 0;
	}
	
	public boolean canAfford(Resource other){
		return getDeficit(other).isZero();
	}
	
	public Resource multiply(int multiple){
		return multiply((double) multiple);
	}
	
	
	private Resource multiply(double multiple) {
		return new Resource((long)(metal * multiple),(long)(crystal * multiple),(long)(deuterium * multiple));
	}
	
	public String toString(){	
		return "Metal: " + metal + "\nCrystal: " + crystal + "\nDeuterium: " + deuterium;
	}
}
