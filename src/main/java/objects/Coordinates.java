package objects;

/**
 * Created by jarndt on 8/8/16.
 */
public class Coordinates {
    String universe;
    int galaxy, system, planet;

    public Coordinates(String coordinates) {
        String[] split = coordinates.split(":");
        galaxy = Integer.parseInt(split[0]);
        system = Integer.parseInt(split[1]);
        planet = Integer.parseInt(split[2]);
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                (universe == null? "" : "universe='" + universe + '\'') +
                ", [ " + galaxy +
                " : " + system +
                " : " + planet +
                " ] }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;

        if (galaxy != that.galaxy) return false;
        if (system != that.system) return false;
        return planet == that.planet;

    }

    @Override
    public int hashCode() {
        int result = galaxy;
        result = 31 * result + system;
        result = 31 * result + planet;
        return result;
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
}
