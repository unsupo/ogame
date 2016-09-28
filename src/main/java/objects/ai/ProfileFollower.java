package objects.ai;

import objects.*;
import objects.Fleet;
import ogame.pages.*;
import ogame.utility.Initialize;
import utilities.Utility;
import utilities.ogame.MissionBuilder;
import utilities.selenium.Task;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private boolean couldntAttackLast = false;
    private String reason;
    @Override
    public Task getDefaultTask() {
        return new Task(new Runnable() {
            @Override
            public void run() {
                try {
                    Merchant.getItemOfDay();

                    if(targets == null)
                        targets = Utility.getInactiveTargets(Utility.getActivePlanet().getCoordinates());

                    if(!couldntAttackLast) {
                        Utility.clickOnNewPage(ogame.pages.Fleet.FLEET);
                        if (Initialize.getInstance().getFleetSlotsAvailable() == 0){//No Fleet Slots Available or n
                            System.out.println("No fleet slots available");
                            couldntAttackLast = true;
                            reason = Fleet.FLEET;
                            return;
                        }else if(Utility.getActivePlanet().getShips().get(Ship.SMALL_CARGO) == 0) {
                            System.out.println("No small cargos");
                            couldntAttackLast = true;
                            reason = Ship.SMALL_CARGO;
                            return;
                        }

                        new MissionBuilder()
                                .setMission(MissionBuilder.ATTACK)
                                .setDestination(targets.get(0))
                                .setFleet(new Fleet().addShip(Ship.SMALL_CARGO, 10))
                                .sendFleet();

                        targets.remove(0);
                    }else{
                        if(reason.equals(Fleet.FLEET)){
//                            List<Mission> missions = Utility.getFleetInformation();
                            if(Initialize.getInstance().getFleetSlotsAvailable() != 0)
                                couldntAttackLast = false;
                            else
                                System.out.println("No fleet slots available");
                        }else if(reason.equals(Ship.SMALL_CARGO)){
                            int count = Utility.getActivePlanet().getShips().get(Ship.SMALL_CARGO);
                            if(count != 0)
                                couldntAttackLast = false;
                            else
                                System.out.println("No small cargos");
                        }
                    }
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


    private BuildTask currentBuildTask;
    public Task getNextBuildTask() throws IOException {
        final BuildTask buildTask = BuildTask.getNextBuildTask();
        String type = Initialize.getType(buildTask.getBuildable().getName());
        Planet planetMap = Initialize.getPlanetMap().get(Utility.getActivePlanetCoordinates());

        int currentLevel = 0;
        switch (type){
            case Research.RESEARCH:
                currentLevel = Initialize.getResearches().get(buildTask.getBuildable().getName()); break;
            case Resources.RESOURCES:
                currentLevel = planetMap.getBuildings().get(buildTask.getBuildable().getName()); break;
            case Facilities.FACILITIES:
                currentLevel = planetMap.getFacilities().get(buildTask.getBuildable().getName()); break;
            case Shipyard.SHIPYARD:
                currentLevel = planetMap.getShips().get(buildTask.getBuildable().getName()); break;
        }


//        if(currentBuildTask.equals(buildTask)) {
//            BuildTask.markBuildTaskAsCompleted(buildTask);
//        }
//
        currentBuildTask = buildTask;

        Buildable build = buildTask.getBuildable();

        build = getBuildTask(build);
        if(build == null)
            return null;

        final String buildName = build.getName();
        return new Task(new Runnable() {
            @Override
            public void run() {
                try {
                    Utility.build(buildName,buildTask.getCountOrLevel());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }//        if (researchRequired.isEmpty() && facilitiesRequired.isEmpty())
    //TODO remove the task programically if requirements are met and it successfully builds it


    public Buildable getBuildTask(Buildable build) throws IOException {
        Map<String, Integer> researchRequired = Utility.getResearchRequirements(build.getName());
        Map<String, Integer> facilitiesRequired = Utility.getFacilityRequirements(build.getName());
        removeCurrentValues(researchRequired);
        removeCurrentValues(facilitiesRequired);

        boolean canBuild = true;
        if(!facilitiesRequired.isEmpty())
            for(String key : facilitiesRequired.keySet())
                if(!(Utility.getActivePlanet().getCurrentFacilityBeingBuild() != null &&
                        Utility.getActivePlanet().getCurrentFacilityBeingBuild().isInProgress()))
                    build = Initialize.getBuildableByName(key);
                else
                    canBuild = false;

        if(!researchRequired.isEmpty())
            for(String key : researchRequired.keySet())
//                if(Utility.canAfford(key)) //TODO check for dark matter
                if(!(Initialize.getCurrentResearch() != null &&
                        Initialize.getCurrentResearch().isInProgress())) {
                    build = Initialize.getBuildableByName(key);
                    canBuild = true;
                }else
                    canBuild = false;

        if(Initialize.getType(build.getName()).equals(Shipyard.SHIPYARD)
                && Utility.getActivePlanet().getCurrentFacilityBeingBuild() != null &&
                Shipyard.SHIPYARD.equals(Initialize.getType(
                        Utility.getActivePlanet().getCurrentFacilityBeingBuild().getBuildable().getName())))
            canBuild = false;

        if(!canBuild)
            return null;

        HashMap<String, Integer> map = Utility.getBuildableRequirements(build.getName());
        removeCurrentValues(map);

        if(map.isEmpty())
            return build;
        else
            return getBuildTask(build);
    }

    private void removeCurrentValues(Map<String, Integer> map) throws IOException {
        Initialize.getResearches().forEach((name,level)->{
            if(map.containsKey(name) && map.get(name) <= level)
                map.remove(name);
        });

        Utility.getActivePlanet().getFacilities().forEach((name,level)->{
            if(map.containsKey(name) && map.get(name) <= level)
                map.remove(name);
        });
    }
}