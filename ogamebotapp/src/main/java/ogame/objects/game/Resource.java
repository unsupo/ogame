package ogame.objects.game;
import ogame.pages.Facilities;
import ogame.pages.Research;
import ogame.pages.Resources;
import org.apache.regexp.RE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class Resource {
    public static void main(String[] args) throws Exception {

    }

    public static final Resource[] shipBaseCost = Resource.convertCosts(new long[] {
            2000L,		2000L,		0L,			0L,
            6000L,		6000L,		0L,			0L,
            3000L,		1000L,		0L,			0L,
            6000L,		4000L,		0L,			0L,
            20000L,		7000L,		2000L,		0L,
            45000L,		15000L,		0L,			0L,
            30000L,	 	40000L,		15000L,		0L,
            60000L,	 	50000L,		15000L,		0L,
            5000000L,	4000000L,	1000000L,	0L,
            50000L,		25000L,		15000L,		0L,
            10000L,		6000L,		2000L,		0L,
            0L,			1000L,		0L,			0L,
            0L,			2000L,		500L,		0L,
            10000L,		20000L,		10000L,		0L,
            2000L,		0L,			0L,			0L,
            1500L,		500L,		0L,			0L,
            6000L,		2000L,		0L,			0L,
            2000L,		6000L,		0L,			0L,
            20000L,		15000L, 	2000L,		0L,
            50000L,		50000L,		30000L,		0L,
            10000L,		10000L,		0L,			0L,
            500000L,	50000L,		0L,			0L
    });

    public static final Resource[] researchBaseCosts = Resource.convertCosts(new long[] {
            0, 800, 400,0,
            200, 100, 0,0,
            1000, 300, 100,0,
            0, 4000, 2000,0,
            2000, 4000, 1000,0,
            400, 0, 1200,0,
            2000, 4000, 600,0,
            10000, 20000, 6000,0,
            200, 1000, 200,0,
            0, 400, 600,0,
            4000, 8000, 4000,0,
            240000, 400000, 160000,0,
            0, 0, 0,300000,
            800, 200, 0,0,
            200, 600, 0,0,
            1000, 0, 0,0
    });

    public static final Resource[] facilitiesBaseCosts = Resource.convertCosts(new long[] {
            400,        120,    200,0,
            200,        400,    200,0,
            200,        400,    200,0,
            20000,      40000,  0,0,
            20000,      40000,  1000,0,
            1000000,    500000, 100000,0,
            0,          50000,  100000,1000,
            200,        0,      50,50,
            20000,      40000,  20000,0,
            20000,      40000,  20000,0,
            2000000,    4000000,2000000,0
    });

    public static final String METAL = "Metal";
    public static final String CRYSTAL = "Crystal";
    public static final String DEUTERIUM = "Deuterium";
    public static final String ENERGY = "energy";

    public long metal = 0;
    public long crystal = 0;
    public long deuterium = 0;
    public long energy = 0;

    private static List<String> names;
    private static final List<Resource> baseCosts;

    static{
        names = new ArrayList<String>();
        names.addAll(Arrays.asList(Ship.names));
        names.addAll(Arrays.asList(Research.names));
        names.addAll(Arrays.asList(Facilities.names));
        names.addAll(Arrays.asList(Resources.names));
    }

    static{
        baseCosts = new ArrayList<Resource>();
        baseCosts.addAll(Arrays.asList(shipBaseCost));
        baseCosts.addAll(Arrays.asList(researchBaseCosts));
        baseCosts.addAll(Arrays.asList(facilitiesBaseCosts));
        baseCosts.addAll(Arrays.asList(Resources.baseCosts));
    }

    public Resource() {
        this.metal = 0;
        this.crystal = 0;
        this.deuterium = 0;
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
            if(names.get(i).equals(name)){
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
        return other.getDeficit(this).isZero();
    }

    public Resource multiply(int multiple){
        return multiply((double) multiple);
    }

    public Resource geometricPower(double multiplier, int nextLevel){
        return new Resource(
                (long)(metal*Math.pow(multiplier,nextLevel-1)),
                (long)(crystal*Math.pow(multiplier,nextLevel-1)),
                (long)(deuterium*Math.pow(multiplier,nextLevel-1)),
                (long)(energy*Math.pow(multiplier,nextLevel-1))
        );
    }


    private Resource multiply(double multiple) {
        return new Resource((long)(metal * multiple),(long)(crystal * multiple),(long)(deuterium * multiple), (long)(energy*multiple));
    }

    @Override
    public String toString() {
        return "Resource{" +
                "metal=" + metal +
                ", crystal=" + crystal +
                ", deuterium=" + deuterium +
                ", energy=" + energy +
                '}';
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

    private double multiplier = 2;

    public Resource setMultiplier(double multiplier){
        this.multiplier = multiplier;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;

        if (metal != resource.metal) return false;
        if (crystal != resource.crystal) return false;
        if (deuterium != resource.deuterium) return false;
        return energy == resource.energy;

    }

    @Override
    public int hashCode() {
        int result = (int) (metal ^ (metal >>> 32));
        result = 31 * result + (int) (crystal ^ (crystal >>> 32));
        result = 31 * result + (int) (deuterium ^ (deuterium >>> 32));
        result = 31 * result + (int) (energy ^ (energy >>> 32));
        return result;
    }

    public long getMetal() {
        return metal;
    }

    public void setMetal(long metal) {
        this.metal = metal;
    }

    public long getCrystal() {
        return crystal;
    }

    public void setCrystal(long crystal) {
        this.crystal = crystal;
    }

    public long getDeuterium() {
        return deuterium;
    }

    public void setDeuterium(long deuterium) {
        this.deuterium = deuterium;
    }

    public long getEnergy() {
        return energy;
    }

    public void setEnergy(long energy) {
        this.energy = energy;
    }

}
