package ogame.objects;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jarndt on 5/10/17.
 */
public class Planet {
    private PlanetProperties planetSize;
    private String planetName, webElement, attribute, value;
    private Planet moon;
    private Resource resources;

    private HashMap<String, Integer>
            buildings   = new HashMap<>(), //building name, level, facilities are included
            facilities  = new HashMap<>(),
            defense     = new HashMap<>(), //defense name, count
            ships       = new HashMap<>(); //ship name, count

    private long metalProduction, crystalProduction, dueteriumProduction;

    private Coordinates coordinates;

    private BuildTask currentFacilityBeingBuild, currentBuildingBeingBuild;
    private Set<BuildTask> currentShipyardBeingBuild = new HashSet<>();

    private void init() throws IOException {
        Buildable.getResources().forEach(a->buildings.put(a.getName(),0));
        Buildable.getFacilitites().forEach(a->facilities.put(a.getName(),0));
        Buildable.getDefense().forEach(a->defense.put(a.getName(),0));
        Buildable.getShipyard().forEach(a->ships.put(a.getName(),0));
    }

    public Resource getResources() {
        return resources;
    }

    public long getMetal(){
        return resources.getMetal();
    }public long getCrystal(){
        return resources.getCrystal();
    }public long getDueterium(){
        return resources.getDeuterium();
    }public long getEnergy(){
        return resources.getEnergy();
    }
    public void setResources(Resource resources) {
        this.resources = resources;
    }

    public PlanetProperties getPlanetSize() {
        return planetSize;
    }

    public void setPlanetSize(PlanetProperties planetSize) {
        this.planetSize = planetSize;
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

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Planet getMoon() {
        return moon;
    }

    public void setMoon(Planet moon) {
        this.moon = moon;
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

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public BuildTask getCurrentFacilityBeingBuild() {
        return currentFacilityBeingBuild;
    }

    public void setCurrentFacilityBeingBuild(BuildTask currentFacilityBeingBuild) {
        this.currentFacilityBeingBuild = currentFacilityBeingBuild;
    }

    public BuildTask getCurrentBuildingBeingBuild() {
        return currentBuildingBeingBuild;
    }

    public void setCurrentBuildingBeingBuild(BuildTask currentBuildingBeingBuild) {
        this.currentBuildingBeingBuild = currentBuildingBeingBuild;
    }

    public Set<BuildTask> getCurrentShipyardBeingBuild() {
        return currentShipyardBeingBuild;
    }

    public void setCurrentShipyardBeingBuild(Set<BuildTask> currentShipyardBeingBuild) {
        this.currentShipyardBeingBuild = currentShipyardBeingBuild;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Planet planet = (Planet) o;

        if (metalProduction != planet.metalProduction) return false;
        if (crystalProduction != planet.crystalProduction) return false;
        if (dueteriumProduction != planet.dueteriumProduction) return false;
        if (planetSize != null ? !planetSize.equals(planet.planetSize) : planet.planetSize != null) return false;
        if (planetName != null ? !planetName.equals(planet.planetName) : planet.planetName != null) return false;
        if (webElement != null ? !webElement.equals(planet.webElement) : planet.webElement != null) return false;
        if (attribute != null ? !attribute.equals(planet.attribute) : planet.attribute != null) return false;
        if (value != null ? !value.equals(planet.value) : planet.value != null) return false;
        if (moon != null ? !moon.equals(planet.moon) : planet.moon != null) return false;
        if (buildings != null ? !buildings.equals(planet.buildings) : planet.buildings != null) return false;
        if (facilities != null ? !facilities.equals(planet.facilities) : planet.facilities != null) return false;
        if (defense != null ? !defense.equals(planet.defense) : planet.defense != null) return false;
        if (ships != null ? !ships.equals(planet.ships) : planet.ships != null) return false;
        if (coordinates != null ? !coordinates.equals(planet.coordinates) : planet.coordinates != null) return false;
        if (currentFacilityBeingBuild != null ? !currentFacilityBeingBuild.equals(planet.currentFacilityBeingBuild) : planet.currentFacilityBeingBuild != null)
            return false;
        if (currentBuildingBeingBuild != null ? !currentBuildingBeingBuild.equals(planet.currentBuildingBeingBuild) : planet.currentBuildingBeingBuild != null)
            return false;
        return currentShipyardBeingBuild != null ? currentShipyardBeingBuild.equals(planet.currentShipyardBeingBuild) : planet.currentShipyardBeingBuild == null;
    }

    @Override
    public int hashCode() {
        int result = planetSize != null ? planetSize.hashCode() : 0;
        result = 31 * result + (planetName != null ? planetName.hashCode() : 0);
        result = 31 * result + (webElement != null ? webElement.hashCode() : 0);
        result = 31 * result + (attribute != null ? attribute.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (moon != null ? moon.hashCode() : 0);
        result = 31 * result + (buildings != null ? buildings.hashCode() : 0);
        result = 31 * result + (facilities != null ? facilities.hashCode() : 0);
        result = 31 * result + (defense != null ? defense.hashCode() : 0);
        result = 31 * result + (ships != null ? ships.hashCode() : 0);
        result = 31 * result + (int) (metalProduction ^ (metalProduction >>> 32));
        result = 31 * result + (int) (crystalProduction ^ (crystalProduction >>> 32));
        result = 31 * result + (int) (dueteriumProduction ^ (dueteriumProduction >>> 32));
        result = 31 * result + (coordinates != null ? coordinates.hashCode() : 0);
        result = 31 * result + (currentFacilityBeingBuild != null ? currentFacilityBeingBuild.hashCode() : 0);
        result = 31 * result + (currentBuildingBeingBuild != null ? currentBuildingBeingBuild.hashCode() : 0);
        result = 31 * result + (currentShipyardBeingBuild != null ? currentShipyardBeingBuild.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Planet{" +
                "planetSize=" + planetSize +
                ", planetName='" + planetName + '\'' +
                ", webElement='" + webElement + '\'' +
                ", attribute='" + attribute + '\'' +
                ", value='" + value + '\'' +
                ", moon=" + moon +
                ", buildings=" + buildings +
                ", facilities=" + facilities +
                ", defense=" + defense +
                ", ships=" + ships +
                ", metalProduction=" + metalProduction +
                ", crystalProduction=" + crystalProduction +
                ", dueteriumProduction=" + dueteriumProduction +
                ", coordinates=" + coordinates +
                ", currentFacilityBeingBuild=" + currentFacilityBeingBuild +
                ", currentBuildingBeingBuild=" + currentBuildingBeingBuild +
                ", currentShipyardBeingBuild=" + currentShipyardBeingBuild +
                '}';
    }
}
