package objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarndt on 8/8/16.
 */
public class PlayerSIM {
    public PlayerSIM() {
        ships = new ArrayList<Ship>();
    }public PlayerSIM(String name) {
        ships = new ArrayList<Ship>();
        this.name = name;
    }

    private List<Ship> ships;
    private int weaponsTech, armourTeach, shieldingTech;
    private int combustionTech, impulseTeach, hyperspaceTech;
    private String name;

    public boolean isDefender() {
        return isDefender;
    }

    public void setDefender(boolean defender) {
        isDefender = defender;
    }

    private boolean isDefender;

    @Override
    public String toString() {
        return "PlayerSIM{" +
                "name='" + name + '\'' +
                ", ships=" + ships +
                ", weaponsTech=" + weaponsTech +
                ", armourTeach=" + armourTeach +
                ", shieldingTech=" + shieldingTech +
                ", combustionTech=" + combustionTech +
                ", impulseTeach=" + impulseTeach +
                ", hyperspaceTech=" + hyperspaceTech +
                ", coordinates=" + coordinates +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerSIM player = (PlayerSIM) o;

        if (weaponsTech != player.weaponsTech) return false;
        if (armourTeach != player.armourTeach) return false;
        if (shieldingTech != player.shieldingTech) return false;
        if (combustionTech != player.combustionTech) return false;
        if (impulseTeach != player.impulseTeach) return false;
        if (hyperspaceTech != player.hyperspaceTech) return false;
        if (ships != null ? !ships.equals(player.ships) : player.ships != null) return false;
        if (name != null ? !name.equals(player.name) : player.name != null) return false;
        return coordinates != null ? coordinates.equals(player.coordinates) : player.coordinates == null;

    }

    @Override
    public int hashCode() {
        int result = ships != null ? ships.hashCode() : 0;
        result = 31 * result + weaponsTech;
        result = 31 * result + armourTeach;
        result = 31 * result + shieldingTech;
        result = 31 * result + combustionTech;
        result = 31 * result + impulseTeach;
        result = 31 * result + hyperspaceTech;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (coordinates != null ? coordinates.hashCode() : 0);
        return result;
    }

    private Coordinates coordinates;

    public List<Ship> getShips() {
        return ships;
    }

    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }

    public int getWeaponsTech() {
        return weaponsTech;
    }

    public void setWeaponsTech(int weaponsTech) {
        this.weaponsTech = weaponsTech;
    }

    public int getArmourTeach() {
        return armourTeach;
    }

    public void setArmourTeach(int armourTeach) {
        this.armourTeach = armourTeach;
    }

    public int getShieldingTech() {
        return shieldingTech;
    }

    public void setShieldingTech(int shieldingTech) {
        this.shieldingTech = shieldingTech;
    }

    public int getCombustionTech() {
        return combustionTech;
    }

    public void setCombustionTech(int combustionTech) {
        this.combustionTech = combustionTech;
    }

    public int getImpulseTeach() {
        return impulseTeach;
    }

    public void setImpulseTeach(int impulseTeach) {
        this.impulseTeach = impulseTeach;
    }

    public int getHyperspaceTech() {
        return hyperspaceTech;
    }

    public void setHyperspaceTech(int hyperspaceTech) {
        this.hyperspaceTech = hyperspaceTech;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void addShip(Ship ship, int count) {
    	try {
			this.addShip(ship.getName(), count);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void addShip(String shipName, int count) throws IOException {
    	for(int i=0;i<count;i++){
    		this.ships.add(new Ship(shipName));
    	}
    }
}
