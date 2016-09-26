package objects.ai;

import objects.Buildable;
import objects.Coordinates;
import objects.Fleet;
import objects.Ship;
import ogame.utility.Initialize;
import ogame.utility.QueueManager;
import utilities.Utility;
import utilities.ogame.MissionBuilder;
import utilities.selenium.Task;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 9/25/16.
 */
public class ProfileFollower implements AI {
    public static void main(String[] args) throws IOException {
        new ProfileFollower().getTask();
    }

    public ProfileFollower() throws IOException {
        Initialize.login();
    }

    private List<Coordinates> targets;

    @Override
    public Task getDefaultTask() {
        return new Task(new Runnable() {
            @Override
            public void run() {
                try {
                    if(targets == null)
                        targets = Utility.getInactiveTargets(Utility.getActivePlanet().getCoordinates());

                    Utility.clickOnNewPage(ogame.pages.Fleet.FLEET);
                    if(Initialize.getInstance().getFleetSlotsAvailable() == 0 ||
                            Utility.getActivePlanet().getShips().get(Ship.SMALL_CARGO) == 0) {
                        System.out.println("No Fleet Slots Available or no small cargos");
                        return;
                    }

                    new MissionBuilder()
                            .setMission(MissionBuilder.ATTACK)
                            .setDestination(targets.get(0))
                            .setFleet(new Fleet().addShip(Ship.SMALL_CARGO,10))
                            .sendFleet();

                    targets.remove(0);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public Task getTask() throws IOException {
        return getNextBuildTask();
    }

    @Override
    public Task getAttackedTask() {
        return null;
    }

    public Task getNextBuildTask() throws IOException {
        String nextProfileBuild = QueueManager.getProfileFileContents().stream()
                                    .filter(a->a.contains("build"))
                                    .collect(Collectors.toList()).get(0);

        String[] buildAndQuantity = nextProfileBuild.split(":")[1].trim().split(",");
        Buildable build = Initialize.getBuildableByName(buildAndQuantity[0]);

        Map<String, Integer> researchRequired = Utility.getResearchRequirements(build.getName());
        Map<String, Integer> facilitiesRequired = Utility.getFacilityRequirements(build.getName());

        Initialize.getResearches().forEach((name,level)->{
            if(researchRequired.containsKey(name) && researchRequired.get(name) <= level)
                researchRequired.remove(name);
        });

        Utility.getActivePlanet().getFacilities().forEach((name,level)->{
            if(facilitiesRequired.containsKey(name) && facilitiesRequired.get(name) <= level)
                facilitiesRequired.remove(name);
        });

        if(!facilitiesRequired.isEmpty())
            for(String key : facilitiesRequired.keySet())
                build = Initialize.getBuildableByName(key);


        if(!researchRequired.isEmpty())
            for(String key : researchRequired.keySet())
                if(Utility.canAfford(key)) //TODO check for dark matter
                    build = Initialize.getBuildableByName(key);

//        if (researchRequired.isEmpty() && facilitiesRequired.isEmpty())
        //TODO remove the task programically if requirements are met and it successfully builds it

        final String buildName = build.getName();
        return new Task(new Runnable() {
            @Override
            public void run() {
                try {
                    Utility.build(buildName,Integer.parseInt(buildAndQuantity[1]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
