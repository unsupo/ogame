package ogame.objects.game.fleet;

import ogame.objects.game.Coordinates;
import ogame.objects.game.Resource;
import ogame.objects.game.Ship;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Created by jarndt on 5/31/17.
 */
public class FleetObject {
    private HashMap<String, Integer> ships; //the ships being sent in this mission
    private Coordinates toCoordinates, fromCoordinates;
    private String mission;
    private Resource resourcesBeingCarried;

    LocalDateTime returnTime, sentTime, landTime;

    public FleetObject() {
    }

    public HashMap<String, Integer> getShips() {
        return ships;
    }

    public void setShips(HashMap<String, Integer> ships) {
        this.ships = ships;
    }

    public Coordinates getToCoordinates() {
        return toCoordinates;
    }

    public void setToCoordinates(Coordinates toCoordinates) {
        this.toCoordinates = toCoordinates;
    }

    public Coordinates getFromCoordinates() {
        return fromCoordinates;
    }

    public void setFromCoordinates(Coordinates fromCoordinates) {
        this.fromCoordinates = fromCoordinates;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public Resource getResourcesBeingCarried() {
        return resourcesBeingCarried;
    }

    public void setResourcesBeingCarried(Resource resourcesBeingCarried) {
        this.resourcesBeingCarried = resourcesBeingCarried;
    }

    public LocalDateTime getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(LocalDateTime returnTime) {
        this.returnTime = returnTime;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public LocalDateTime getLandTime() {
        return landTime;
    }

    public void setLandTime(LocalDateTime landTime) {
        this.landTime = landTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FleetObject that = (FleetObject) o;

        if (ships != null ? !ships.equals(that.ships) : that.ships != null) return false;
        if (toCoordinates != null ? !toCoordinates.equals(that.toCoordinates) : that.toCoordinates != null)
            return false;
        if (fromCoordinates != null ? !fromCoordinates.equals(that.fromCoordinates) : that.fromCoordinates != null)
            return false;
        if (mission != null ? !mission.equals(that.mission) : that.mission != null) return false;
        if (resourcesBeingCarried != null ? !resourcesBeingCarried.equals(that.resourcesBeingCarried) : that.resourcesBeingCarried != null)
            return false;
        if (returnTime != null ? !returnTime.equals(that.returnTime) : that.returnTime != null) return false;
        if (sentTime != null ? !sentTime.equals(that.sentTime) : that.sentTime != null) return false;
        return landTime != null ? landTime.equals(that.landTime) : that.landTime == null;
    }

    @Override
    public int hashCode() {
        int result = ships != null ? ships.hashCode() : 0;
        result = 31 * result + (toCoordinates != null ? toCoordinates.hashCode() : 0);
        result = 31 * result + (fromCoordinates != null ? fromCoordinates.hashCode() : 0);
        result = 31 * result + (mission != null ? mission.hashCode() : 0);
        result = 31 * result + (resourcesBeingCarried != null ? resourcesBeingCarried.hashCode() : 0);
        result = 31 * result + (returnTime != null ? returnTime.hashCode() : 0);
        result = 31 * result + (sentTime != null ? sentTime.hashCode() : 0);
        result = 31 * result + (landTime != null ? landTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FleetObject{" +
                "ships=" + ships +
                ", toCoordinates=" + toCoordinates +
                ", fromCoordinates=" + fromCoordinates +
                ", mission='" + mission + '\'' +
                ", resourcesBeingCarried=" + resourcesBeingCarried +
                ", returnTime=" + returnTime +
                ", sentTime=" + sentTime +
                ", landTime=" + landTime +
                '}';
    }
}
