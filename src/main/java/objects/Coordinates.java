package objects;

/**
 * Created by jarndt on 8/8/16.
 */
public class Coordinates implements Comparable<Coordinates>{
    private String universe;
    private int galaxy;
    private int system;
    private int planet;
    private int type = PLANET;

    public int getType() {
        return type;
    }

    public Coordinates setType(int type) {
        this.type = type;
        return this;
    }
    
    public static final int PLANET = 0, MOON = 1, DEBRIS_FIELD = 2;

    public Coordinates(String coordinates) {
        coordinates = coordinates.replace("[","").replace("]","");
        String[] split = coordinates.split(":");
        galaxy = Integer.parseInt(split[0]);
        system = Integer.parseInt(split[1]);
        planet = Integer.parseInt(split[2]);
    }

    public Coordinates(Coordinates coordinates) {
        this.universe = coordinates.getUniverse();
        this.galaxy = coordinates.getGalaxy();
        this.system = coordinates.getSystem();
        this.planet = coordinates.getPlanet();
        this.type = coordinates.getType();
    }

    public Coordinates clone(){
        return new Coordinates(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;

        if (galaxy != that.galaxy) return false;
        if (system != that.system) return false;
        if (planet != that.planet) return false;
        if (type != that.type) return false;
        return universe != null ? universe.equals(that.universe) : that.universe == null;

    }

    @Override
    public int hashCode() {
        int result = universe != null ? universe.hashCode() : 0;
        result = 31 * result + galaxy;
        result = 31 * result + system;
        result = 31 * result + planet;
        result = 31 * result + type;
        return result;
    }

    @Override
    public String toString() {
        return "{'Coordinates':{" +
                "'universe':'" + universe + '\'' +
                ", 'galaxy':" + galaxy +
                ", 'system':" + system +
                ", 'planet':" + planet +
                ", 'type':" + type +
                "}}";
    }

    public Coordinates(String universe, int galaxy, int system, int planet) {

        this.universe = universe;
        this.galaxy = galaxy;
        this.system = system;
        this.planet = planet;
    }

    public Coordinates(int galaxy, int system, int planet) {
        this.galaxy = galaxy;
        this.system = system;
        this.planet = planet;
    }

    public String getUniverse() {
        return universe;
    }

    public void setUniverse(String universe) {
        this.universe = universe;
    }

    public int getGalaxy() {
        return galaxy;
    }

    public void setGalaxy(int galaxy) {
        this.galaxy = galaxy;
    }

    public int getSystem() {
        return system;
    }

    public void setSystem(int system) {
        this.system = system;
    }

    public int getPlanet() {
        return planet;
    }

    public void setPlanet(int planet) {
        this.planet = planet;
    }

    public String getCoordinates() {
        return galaxy+":"+system+":"+planet;
    }

    @Override
	public int compareTo(Coordinates o) {
        return Integer.compare(galaxy,o.galaxy)*8+
                Integer.compare(system,o.system)*4+
                Integer.compare(planet,o.planet)*2+
                Integer.compare(type,o.type)*1;
	}
	
	public int getDistance(Coordinates other){
		if(galaxy != other.galaxy){
			return getMinDistance(galaxy, other.galaxy, 9)*20000;
		}
		if(system != other.system){
			return 2700 + getMinDistance(system, other.system, 499)*95;
		}
		if(planet != other.planet){
			return 1000 + Math.abs(planet - other.planet)*5;
		}
		return 5;
	}
	
	public static int getTime(int distance, int speed){
		return 10 + (int)(3500*Math.sqrt(10*distance/speed));
	}
	
	public int getTime(Coordinates other, int speed){
		return Coordinates.getTime(getDistance(other), speed);
	}
	
	private int getMinDistance(int one, int two, int loop){
		int distance = Math.abs(one - two);
		return Math.min(distance, loop - distance);
	}

    public boolean isMoon() {
        return type == MOON;
    }
}
