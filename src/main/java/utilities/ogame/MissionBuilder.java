package utilities.ogame;

import objects.Coordinates;
import objects.Fleet;
import ogame.utility.Initialize;
import ogame.utility.Resource;
import utilities.selenium.UIMethods;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by jarndt on 9/23/16.
 */
public class MissionBuilder {
    public static final String  EXPEDITION              = "missionButton15",
                                COLONIZATION            = "missionButton7",
                                RECYCLE_DEBRIS_FIELD    = "missionButton8",
                                TRANSPORT               = "missionButton3",
                                DEPLOYMENT              = "missionButton4",
                                ESPIONAGE               = "missionButton6",
                                ACS_DEFEND              = "missionButton5",
                                ATTACK                  = "missionButton1",
                                ACS_ATTACK              = "missionButton2",
                                MOON_DESTRUCTION        = "missionButton9";

    private Fleet fleet;
    private String mission;
    private int speed;

    @Override
    public String toString() {
        return "MissionBuilder{" +
                " mission='" + mission + '\'' +
                ", speed=" + speed +
                ", destination=" + destination +
                ", resourceToSend=" + resourceToSend +
                ", fleet=" + fleet +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MissionBuilder that = (MissionBuilder) o;

        if (speed != that.speed) return false;
        if (fleet != null ? !fleet.equals(that.fleet) : that.fleet != null) return false;
        if (mission != null ? !mission.equals(that.mission) : that.mission != null) return false;
        if (destination != null ? !destination.equals(that.destination) : that.destination != null) return false;
        return resourceToSend != null ? resourceToSend.equals(that.resourceToSend) : that.resourceToSend == null;

    }

    @Override
    public int hashCode() {
        int result = fleet != null ? fleet.hashCode() : 0;
        result = 31 * result + (mission != null ? mission.hashCode() : 0);
        result = 31 * result + speed;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + (resourceToSend != null ? resourceToSend.hashCode() : 0);
        return result;
    }

    Coordinates destination;
    Resource resourceToSend;

    public Fleet getFleet() {
        return fleet;
    }

    public MissionBuilder setFleet(Fleet fleet) {
        this.fleet = fleet;
        return this;
    }

    public String getMission() {
        return mission;
    }

    public MissionBuilder setMission(String mission) {
        this.mission = mission;
        return this;
    }

    public int getSpeed() {
        return speed;
    }

    public MissionBuilder setSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    public Coordinates getDestination() {
        return destination;
    }

    public MissionBuilder setDestination(Coordinates destination) {
        this.destination = destination;
        return this;
    }

    public Resource getResourceToSend() {
        return resourceToSend;
    }

    public MissionBuilder setResourceToSend(Resource resourceToSend) {
        this.resourceToSend = resourceToSend;
        return this;
    }

    public static void attackTarget(String attackingFromPlanetName, Coordinates yourTargetsCoordinates) throws IOException {
        UIMethods.clickOnAttributeAndValue("id", Initialize.getPlanetMap().get(attackingFromPlanetName).getWebElement());

        UIMethods.clickOnText("Fleet");
        UIMethods.typeOnAttributeAndValue("id", "ship_202","9999");
        UIMethods.clickOnText("Next");


        System.out.println("Attacking: "+yourTargetsCoordinates);
        Coordinates attackTarget = yourTargetsCoordinates;//new Coordinates(results.get(0).get("COORDINATES").toString());

        UIMethods.typeOnAttributeAndValue("id","galaxy",attackTarget.getGalaxy()+"");
        UIMethods.typeOnAttributeAndValue("id","system",attackTarget.getSystem()+"");
        UIMethods.typeOnAttributeAndValue("id","position",attackTarget.getPlanet()+"");

        UIMethods.clickOnText("Next");

        UIMethods.waitForText("Select mission for target:",1, TimeUnit.MINUTES);
        UIMethods.clickOnText("Attack");//document.getElementById("missionButton1").click()
        UIMethods.clickOnText("Send fleet");
    }

}
