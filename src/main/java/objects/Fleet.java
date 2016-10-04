package objects;

import objects.messages.EspionageMsg;
import ogame.utility.Resource;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by jarndt on 9/23/16.
 */
public class Fleet {
    public static final String FLEET = "Fleet";
    private HashMap<Ship,Integer> ships = new HashMap<>();

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

    public Resource getResourcesBeingCarried() {
        return resourcesBeingCarried;
    }

    public void setResourcesBeingCarried(Resource resourcesBeingCarried) {
        this.resourcesBeingCarried = resourcesBeingCarried;
    }

    Resource resourcesBeingCarried = new Resource();

    public HashMap<Ship, Integer> getShips() {
        return ships;
    }

    public Fleet setShips(HashMap<Ship, Integer> ships) {
        this.ships = ships;
        return this;
    }

    public Fleet setShipsByName(HashMap<String,Integer> shipsByName) {
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
        ships.put(new Ship(shipName),i);
        return this;
    }

    public static Fleet parseFleets(Document title) {
        Fleet fleet = new Fleet();
        Elements tableObj = title.select("table.fleetinfo").select("tr");
        Resource resource = new Resource();
        for(Element e : tableObj){
            Elements td = e.select("td");
            if(td.size() == 0)
                continue;

            String shipName = td.get(0).text().trim().replace(":","");
            try {
                if(Ship.isValidShip(shipName)){
                    int shipCount = Integer.parseInt(td.get(1).text().trim());
                    fleet.addShip(shipName,shipCount);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if(Resource.METAL.equals(shipName))
                resource.metal = EspionageMsg.parseNumber(e.select("td.value").text());
            if(Resource.CRYSTAL.equals(shipName))
                resource.crystal = EspionageMsg.parseNumber(e.select("td.value").text());
            if(Resource.DEUTERIUM.equals(shipName))
                resource.deuterium = EspionageMsg.parseNumber(e.select("td.value").text());
        }
        fleet.setResourcesBeingCarried(resource);
        return fleet;
    }
}
