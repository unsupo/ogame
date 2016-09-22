package objects;

import utilities.Utility;
import utilities.filesystem.FileOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 8/8/16.
 */
public class Ship {
	
	
    public static final String SMALL_CARGO = "Small Cargo";

    private static List<Ship> allShips;
    public static List<Ship> getAllShips(String...pathToShipFile) throws IOException {
        String path = Utility.SHIP_INFO;
        if(pathToShipFile != null && pathToShipFile.length == 1)
            path = pathToShipFile[0];
        if(allShips == null)
            allShips = FileOptions.readFileIntoListString(path).stream().filter(a -> !a.contains("id")).map(a -> new Ship().parse(a)).collect(Collectors.toList());
        return allShips;
    }

    public static boolean isValidShip(String shipName,String...pathToShipFile) throws IOException {
        return getAllShips(pathToShipFile).stream().filter(a->a.getName().equals(shipName)).collect(Collectors.toList()).size() != 0;
    }public static Ship getShipByID(int ID,String...pathToShipFile) throws IOException {
        return getAllShips(pathToShipFile).stream().filter(a->a.getId() == ID).collect(Collectors.toList()).get(0);
    }public static Ship getShipByName(String shipName,String...pathToShipFile) throws IOException {
        return getAllShips(pathToShipFile).stream().filter(a->a.getName().equals(shipName)).collect(Collectors.toList()).get(0);
    }

    private int id, metal_cost,
            crystal_cost,
            deuterium_cost,
            structural_integrity,
            shield_power,
            weapon_power,
            cargo_capacity,
            base_speed,
            potential_speed,
            fuel_consuption,
            potential_fuel_consumption;

    private boolean isDefenseStructure = true;

    private String name;

    private HashMap<String,Integer> rapidFire;

    @Override
    public String toString() {
        return "Ship{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", metal_cost=" + metal_cost +
                ", crystal_cost=" + crystal_cost +
                ", deuterium_cost=" + deuterium_cost +
                ", structural_integrity=" + structural_integrity +
                ", shield_power=" + shield_power +
                ", weapon_power=" + weapon_power +
                ", cargo_capacity=" + cargo_capacity +
                ", base_speed=" + base_speed +
                ", potential_speed=" + potential_speed +
                ", fuel_consuption=" + fuel_consuption +
                ", potential_fuel_consumption=" + potential_fuel_consumption +
                ", rapidFire=" + rapidFire +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ship ship = (Ship) o;

        if (id != ship.id) return false;
        if (metal_cost != ship.metal_cost) return false;
        if (crystal_cost != ship.crystal_cost) return false;
        if (deuterium_cost != ship.deuterium_cost) return false;
        if (structural_integrity != ship.structural_integrity) return false;
        if (shield_power != ship.shield_power) return false;
        if (weapon_power != ship.weapon_power) return false;
        if (cargo_capacity != ship.cargo_capacity) return false;
        if (base_speed != ship.base_speed) return false;
        if (potential_speed != ship.potential_speed) return false;
        if (fuel_consuption != ship.fuel_consuption) return false;
        if (potential_fuel_consumption != ship.potential_fuel_consumption) return false;
        if (name != null ? !name.equals(ship.name) : ship.name != null) return false;
        return rapidFire != null ? rapidFire.equals(ship.rapidFire) : ship.rapidFire == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + metal_cost;
        result = 31 * result + crystal_cost;
        result = 31 * result + deuterium_cost;
        result = 31 * result + structural_integrity;
        result = 31 * result + shield_power;
        result = 31 * result + weapon_power;
        result = 31 * result + cargo_capacity;
        result = 31 * result + base_speed;
        result = 31 * result + potential_speed;
        result = 31 * result + fuel_consuption;
        result = 31 * result + potential_fuel_consumption;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (rapidFire != null ? rapidFire.hashCode() : 0);
        return result;
    }

    public int getPotential_speed() {

        return potential_speed;
    }

    public int getPotential_fuel_consumption() {
        return potential_fuel_consumption;
    }

    public int getId() {

        return id;
    }

    public int getMetal_cost() {
        return metal_cost;
    }

    public int getCrystal_cost() {
        return crystal_cost;
    }

    public int getDeuterium_cost() {
        return deuterium_cost;
    }

    public int getStructural_integrity() {
        return structural_integrity;
    }

    public int getShield_power() {
        return shield_power;
    }

    public int getWeapon_power() {
        return weapon_power;
    }

    public int getCargo_capacity() {
        return cargo_capacity;
    }

    public int getBase_speed() {
        return base_speed;
    }

    public int getFuel_consuption() {
        return fuel_consuption;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Integer> getRapidFire() {
        return rapidFire;
    }

    public boolean isDefenseStructure() {
        return isDefenseStructure;
    }

    public Ship(int id) throws IOException {
         clone(getAllShips().stream().filter(a->a.getId() == id).collect(Collectors.toList()).get(0));
    }

    public static void setAllShips(List<Ship> allShips) {
        Ship.allShips = allShips;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMetal_cost(int metal_cost) {
        this.metal_cost = metal_cost;
    }

    public void setCrystal_cost(int crystal_cost) {
        this.crystal_cost = crystal_cost;
    }

    public void setDeuterium_cost(int deuterium_cost) {
        this.deuterium_cost = deuterium_cost;
    }

    public void setStructural_integrity(int structural_integrity) {
        this.structural_integrity = structural_integrity;
    }

    public void setShield_power(int shield_power) {
        this.shield_power = shield_power;
    }

    public void setWeapon_power(int weapon_power) {
        this.weapon_power = weapon_power;
    }

    public void setCargo_capacity(int cargo_capacity) {
        this.cargo_capacity = cargo_capacity;
    }

    public void setBase_speed(int base_speed) {
        this.base_speed = base_speed;
    }

    public void setPotential_speed(int potential_speed) {
        this.potential_speed = potential_speed;
    }

    public void setFuel_consuption(int fuel_consuption) {
        this.fuel_consuption = fuel_consuption;
    }

    public void setPotential_fuel_consumption(int potential_fuel_consumption) {
        this.potential_fuel_consumption = potential_fuel_consumption;
    }

    public void setDefenseStructure(boolean defenseStructure) {
        isDefenseStructure = defenseStructure;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRapidFire(HashMap<String, Integer> rapidFire) {
        this.rapidFire = rapidFire;
    }

    public Ship(String name) throws IOException {
         clone(getAllShips().stream().filter(a->a.getName().equals(name)).collect(Collectors.toList()).get(0));
    }

    public Ship clone(Ship ship){
        id = ship.getId();
        metal_cost = ship.getMetal_cost();
        crystal_cost = ship.getCrystal_cost();
        deuterium_cost = ship.getDeuterium_cost();
        structural_integrity = ship.getStructural_integrity();
        shield_power = ship.getShield_power();
        weapon_power = ship.getWeapon_power();
        cargo_capacity = ship.getCargo_capacity();
        base_speed = ship.getBase_speed();
        potential_speed = ship.getPotential_speed();
        fuel_consuption = ship.getFuel_consuption();
        potential_fuel_consumption = ship.getPotential_fuel_consumption();
        name = ship.getName();
        isDefenseStructure = ship.isDefenseStructure();
        rapidFire = (HashMap<String, Integer>)ship.getRapidFire().clone();
        return this;
    }

    public Ship(){}
    
    public int getWeighedPrice(){
    	return (metal_cost+crystal_cost*4+deuterium_cost*5)/1000;
    }

    public Ship parse(String lineInCSVFile){
        rapidFire = new HashMap<>();
        String[] split = lineInCSVFile.split(",");
        id = Integer.parseInt(split[0]);
        name = split[1];
        metal_cost = Integer.parseInt(split[2]);
        crystal_cost = Integer.parseInt(split[3]);
        deuterium_cost = Integer.parseInt(split[4]);
        structural_integrity = Integer.parseInt(split[5]);
        shield_power  = Integer.parseInt(split[6]);
        weapon_power = Integer.parseInt(split[7]);
        if(split.length-1 < 8)
            return this;
        cargo_capacity = Integer.parseInt(split[8]);
        String speed = split[9];
        if(split[9].contains("/")){
            String[] s = split[9].split("/");
            speed = s[0];
            potential_speed = Integer.parseInt(s[1]);
        }
        base_speed = Integer.parseInt(speed);
        String fuel = split[10];
        if(split[10].contains("/")){
            String[] s = split[10].split("/");
            fuel = s[0];
            potential_fuel_consumption = Integer.parseInt(s[1]);
        }
        fuel_consuption = Integer.parseInt(fuel);

        isDefenseStructure = false;

        if(split.length-1 == 11)
        for(String s : split[11].split("/")){
            String[] rf = s.split("\\.");
            rapidFire.put(rf[0],Integer.parseInt(rf[1]));
        }
        return this;
    }

}
