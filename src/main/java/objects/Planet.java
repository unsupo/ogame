package objects;

import ogame.pages.Facilities;
import ogame.pages.Resources;
import ogame.pages.Shipyard;
import ogame.utility.Resource;

import java.util.*;

/**
 * Created by jarndt on 9/19/16.
 */
public class Planet {

    private PlanetProperties planetSize;
    private String planetName, webElement, attribute, value;
    private Planet moon;

    private BuildTask currentFacilityBeingBuild, currentBuildingBeingBuild;
    private Set<BuildTask> currentShipyardBeingBuild = new HashSet<>();

    private HashMap<String, Integer>
            buildings   = new HashMap<>(), //building name, level, facilities are included
            facilities  = new HashMap<>(),
            defense     = new HashMap<>(), //defense name, count
            ships       = new HashMap<>(); //ship name, count

    private long metalProduction, crystalProduction, dueteriumProduction;

    private Coordinates coordinates;

    public BuildTask getCurrentFacilityBeingBuild() {
        return currentFacilityBeingBuild;
    }

    public void setCurrentFacilityBeingBuild(BuildTask currentFacilityBeingBuild) {
        purgeCompletedTasks();
        this.currentFacilityBeingBuild = currentFacilityBeingBuild;
    }

    private void purgeCompletedTasks() {
        if(currentBuildingBeingBuild != null && currentBuildingBeingBuild.isComplete()) {
            completed(currentBuildingBeingBuild);
            currentBuildingBeingBuild = null;
        }if(currentFacilityBeingBuild != null && currentFacilityBeingBuild.isComplete()) {
            completed(currentBuildingBeingBuild);
            currentFacilityBeingBuild = null;
        }
        List<BuildTask> removeList = new ArrayList<>();
        currentShipyardBeingBuild.stream().filter(b->b.isComplete()).forEach(b->{completed(b);removeList.add(b);});
        currentShipyardBeingBuild.removeAll(removeList);
    }

    private void completed(BuildTask currentBuildableBeingBuild) { //this adds the new level to the planet's map
        Buildable build = currentBuildableBeingBuild.getBuildable();
        int level = currentBuildableBeingBuild.getCountOrLevel();
        switch (build.getType()){
            case Facilities.FACILITIES:
                facilities.put(build.getName(),level); break;
            case Resources.RESOURCES:
                buildings.put(build.getName(),level); break;
            case Shipyard.SHIPYARD:
                ships.put(build.getName(),ships.get(build.getName())+level); break;
        }
    }

    public BuildTask getCurrentBuildingBeingBuild() {
        purgeCompletedTasks();
        return currentBuildingBeingBuild;
    }

    public void setCurrentBuildingBeingBuild(BuildTask currentBuildingBeingBuild) {
        this.currentBuildingBeingBuild = currentBuildingBeingBuild;
    }

    public Set<BuildTask> getCurrentShipyardBeingBuild() {
        purgeCompletedTasks();
        return currentShipyardBeingBuild;
    }

    public void setCurrentShipyardBeingBuild(Set<BuildTask> currentShipyardBeingBuild) {
        this.currentShipyardBeingBuild = currentShipyardBeingBuild;
    }

    public PlanetProperties getPlanetSize() {
        return planetSize;
    }

    public void setPlanetProperties(PlanetProperties planetSize) {
        this.planetSize = planetSize;
    }

    public Resource getCurrentResources() {
        return currentResources;
    }

    public void setCurrentResources(Resource currentResources) {
        this.currentResources = currentResources;
    }

    public void setPlanetSize(PlanetProperties planetSize) {
        this.planetSize = planetSize;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private Resource currentResources;

    public long getCurrentDarkMatter() {
        return currentDarkMatter;
    }

    public void setCurrentDarkMatter(long currentDarkMatter) {
        this.currentDarkMatter = currentDarkMatter;
    }

    private long currentDarkMatter;

    @Override
    public String toString() {
        return "Planet{" +
                "currentResources=" + currentResources +
                ", currentDarkMatter=" + currentDarkMatter +
                ", planetSize=" + planetSize +
                ", planetName='" + planetName + '\'' +
                ", webElement='" + webElement + '\'' +
                ", attribute='" + attribute + '\'' +
                ", value='" + value + '\'' +
                ", moon=" + moon +
                ", coordinates=" + coordinates +
                ", buildings=" + buildings +
                ", facilities=" + facilities +
                ", defense=" + defense +
                ", ships=" + ships +
                ", metalProduction=" + metalProduction +
                ", crystalProduction=" + crystalProduction +
                ", dueteriumProduction=" + dueteriumProduction +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Planet planet = (Planet) o;

        if (currentDarkMatter != planet.currentDarkMatter) return false;
        if (metalProduction != planet.metalProduction) return false;
        if (crystalProduction != planet.crystalProduction) return false;
        if (dueteriumProduction != planet.dueteriumProduction) return false;
        if (currentResources != null ? !currentResources.equals(planet.currentResources) : planet.currentResources != null)
            return false;
        if (planetSize != null ? !planetSize.equals(planet.planetSize) : planet.planetSize != null) return false;
        if (planetName != null ? !planetName.equals(planet.planetName) : planet.planetName != null) return false;
        if (webElement != null ? !webElement.equals(planet.webElement) : planet.webElement != null) return false;
        if (attribute != null ? !attribute.equals(planet.attribute) : planet.attribute != null) return false;
        if (value != null ? !value.equals(planet.value) : planet.value != null) return false;
        if (moon != null ? !moon.equals(planet.moon) : planet.moon != null) return false;
        if (coordinates != null ? !coordinates.equals(planet.coordinates) : planet.coordinates != null) return false;
        if (buildings != null ? !buildings.equals(planet.buildings) : planet.buildings != null) return false;
        if (facilities != null ? !facilities.equals(planet.facilities) : planet.facilities != null) return false;
        if (defense != null ? !defense.equals(planet.defense) : planet.defense != null) return false;
        return ships != null ? ships.equals(planet.ships) : planet.ships == null;

    }

    @Override
    public int hashCode() {
        int result = currentResources != null ? currentResources.hashCode() : 0;
        result = 31 * result + (int) (currentDarkMatter ^ (currentDarkMatter >>> 32));
        result = 31 * result + (planetSize != null ? planetSize.hashCode() : 0);
        result = 31 * result + (planetName != null ? planetName.hashCode() : 0);
        result = 31 * result + (webElement != null ? webElement.hashCode() : 0);
        result = 31 * result + (attribute != null ? attribute.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (moon != null ? moon.hashCode() : 0);
        result = 31 * result + (coordinates != null ? coordinates.hashCode() : 0);
        result = 31 * result + (buildings != null ? buildings.hashCode() : 0);
        result = 31 * result + (facilities != null ? facilities.hashCode() : 0);
        result = 31 * result + (defense != null ? defense.hashCode() : 0);
        result = 31 * result + (ships != null ? ships.hashCode() : 0);
        result = 31 * result + (int) (metalProduction ^ (metalProduction >>> 32));
        result = 31 * result + (int) (crystalProduction ^ (crystalProduction >>> 32));
        result = 31 * result + (int) (dueteriumProduction ^ (dueteriumProduction >>> 32));
        return result;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }

    public Planet getMoon() {
        return moon;
    }

    public void setMoon(Planet moon) {
        this.moon = moon;
    }

    public String getPlanetName() {

        return planetName;
    }

    public void setPlanetName(String planetName) {
        this.planetName = planetName;
    }

    public String getWebElement() {
        return webElement;
    }

    public void setWebElement(String webElement) {
        this.webElement = webElement;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public HashMap<String, Integer> getBuildings() {
        return buildings;
    }

    public void setBuildings(HashMap<String, Integer> buildings) {
        this.buildings = buildings;
    }

    public HashMap<String, Integer> getFacilities() {
        return facilities;
    }

    public void setFacilities(HashMap<String, Integer> facilities) {
        this.facilities = facilities;
    }

    public HashMap<String, Integer> getDefense() {
        return defense;
    }

    public void setDefense(HashMap<String, Integer> defense) {
        this.defense = defense;
    }

    public HashMap<String, Integer> getShips() {
        getCurrentShipyardBeingBuild();
        return ships;
    }

    public void setShips(HashMap<String, Integer> ships) {
        this.ships = ships;
    }

    public long getMetalProduction() {
        return metalProduction;
    }

    public void setMetalProduction(long metalProduction) {
        this.metalProduction = metalProduction;
    }

    public long getCrystalProduction() {
        return crystalProduction;
    }

    public void setCrystalProduction(long crystalProduction) {
        this.crystalProduction = crystalProduction;
    }

    public long getDueteriumProduction() {
        return dueteriumProduction;
    }

    public void setDueteriumProduction(long dueteriumProduction) {
        this.dueteriumProduction = dueteriumProduction;
    }

    public void setAttributeAndValue(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }
}
