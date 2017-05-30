package ogame.objects.game;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by jarndt on 5/10/17.
 */
public class Fleet {
    public static final String FLEET = "Fleet";
    private HashMap<Ship, Integer> ships = new HashMap<>();
    Resource resourcesBeingCarried = new Resource();

    public Resource getResourcesBeingCarried() {
        return resourcesBeingCarried;
    }

    public void setResourcesBeingCarried(Resource resourcesBeingCarried) {
        this.resourcesBeingCarried = resourcesBeingCarried;
    }


    public HashMap<Ship, Integer> getShips() {
        return ships;
    }

    public Fleet setShips(HashMap<Ship, Integer> ships) {
        this.ships = ships;
        return this;
    }

    public Fleet setShipsByName(HashMap<String, Integer> shipsByName) {
        shipsByName.forEach((a, b) -> {
            try {
                addShip(a, b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return this;
    }

    public Fleet addShip(String shipName, int i) throws IOException {
        ships.put(new Ship(shipName), i);
        return this;
    }

    @Override
    public String toString() {
        return "Fleet{" +
                "ships=" + ships +
                ", resourcesBeingCarried=" + resourcesBeingCarried +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fleet fleet = (Fleet) o;

        if (ships != null ? !ships.equals(fleet.ships) : fleet.ships != null) return false;
        return resourcesBeingCarried != null ? resourcesBeingCarried.equals(fleet.resourcesBeingCarried) : fleet.resourcesBeingCarried == null;

    }

    @Override
    public int hashCode() {
        int result = ships != null ? ships.hashCode() : 0;
        result = 31 * result + (resourcesBeingCarried != null ? resourcesBeingCarried.hashCode() : 0);
        return result;
    }

}
