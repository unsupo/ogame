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
import java.util.*;
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

    private boolean couldntAttackLast = false;
    private String reason;
    @Override
    public Task getDefaultTask() {
        return new Task(new Runnable() {
            @Override
            public void run() {
                try {
                    Merchant.getItemOfDay();

                    if(Utility.getMessageCount() != 0)
                        Message.parseAllMessages();

                    if(targets == null)
                        targets = Utility.getAllInactiveTargets(Utility.getActivePlanet().getCoordinates(),Initialize.getUniverseID());

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
                            int missions = Utility.getOwnMissionCount();
                            if(Initialize.getInstance().getTotalFleetSlots() - missions > 0)
                                couldntAttackLast = false;
                            else
                                System.out.println("Still No fleet slots available");
                        }else if(reason.equals(Ship.SMALL_CARGO)){
                            int count = Utility.getActivePlanet().getShips().get(Ship.SMALL_CARGO);
                            if(count != 0)
                                couldntAttackLast = false;
                            else
                                System.out.println("Still No small cargos");
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
        if(Utility.isBeingAttack()) { //change for testing
            List<Mission> missions = Mission.getActiveMissions();
//                    .stream().filter(a -> a.getMissionType().equals(MissionBuilder.ATTACK)).collect(Collectors.toList());
            Collections.sort(missions,(a,b)->a.getArrivalTime().compareTo(b.getArrivalTime()));

            System.out.println();
        }
        return null;
    }


    private BuildTask currentBuildTask;
    public Task getNextBuildTask() throws IOException {
        final BuildTask buildTask = BuildTask.getNextBuildTask();
        if(buildTask == null)
            return null;

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

        if(currentLevel >= buildTask.getCountOrLevel())
            BuildTask.markBuildTaskAsCompleted(buildTask);

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
    //              TODO done but needs testing


    public Buildable getBuildTask(Buildable build) throws IOException {
        Map<String, Integer> researchRequired = Utility.getResearchRequirements(build.getName());
        Map<String, Integer> facilitiesRequired = Utility.getFacilityRequirements(build.getName());
        removeCurrentValues(researchRequired);
        removeCurrentValues(facilitiesRequired);

        boolean canBuild = true;
        if(!facilitiesRequired.isEmpty())
            for(String key : facilitiesRequired.keySet())
                if(Initialize.getCurrentDarkMatter() >= 500 || Utility.canAfford(key,facilitiesRequired.get(key))) { //check if you can afford it (you can if you have at least 500 DM)
                    if (!(Utility.getActivePlanet().getCurrentFacilityBeingBuild() != null && //if no facilities are being built
                            Utility.getActivePlanet().getCurrentFacilityBeingBuild().isInProgress()))
                        build = Initialize.getBuildableByName(key); //build the facility
                }else
                    canBuild = false;

        if(!researchRequired.isEmpty())
            for(String key : researchRequired.keySet()) //for each required research
                if(Initialize.getCurrentDarkMatter() >= 500 || Utility.canAfford(key,researchRequired.get(key))){ //check if you can afford it (you can if you have at least 500 DM)
                    if(!(Initialize.getCurrentResearch() != null &&
                            Initialize.getCurrentResearch().isInProgress())) { //check if you have no researches in progress
                        build = Initialize.getBuildableByName(key);
                        canBuild = true; //if no researches in progress and you can afford it then do a research
                    }
                }else
                    canBuild = false; //else you can't build a research

        if(Initialize.getType(build.getName()).equals(Shipyard.SHIPYARD) //if the build item is built from the shipyard
                && (Utility.getActivePlanet().getCurrentFacilityBeingBuild() != null &&
                Shipyard.SHIPYARD.equals(Initialize.getType(//and the facility being built is a shipyard
                        Utility.getActivePlanet().getCurrentFacilityBeingBuild().getBuildable().getName()))))
            canBuild = false; //then you can't build it yet

        if((Initialize.getCurrentDarkMatter() >= 500 || Utility.canAfford(build.getName(),build.getLevelNeeded()))
                    && !isBeingBlocked(build))//if you can't build it, don't bother trying
            canBuild = true;
        else canBuild = false;

        if(!canBuild) {
            System.out.println("Can't build yet: "+build.getName());
            return null; //if you can't build anything then don't do anything
        }
        HashMap<String, Integer> map = Utility.getBuildableRequirements(build.getName());
        removeCurrentValues(map);

        if(map.isEmpty()) //if the item has no prerequisites then build it
            return build;
        else
            for(String prerequisites : map.keySet())
                return getBuildTask(Initialize.getBuildableByName(prerequisites).setLevelNeeded(map.get(prerequisites))); //otherwise build it's prerequisite
        return null;
    }

    private boolean isBeingBlocked(Buildable build) throws IOException {
        BuildTask buildTask  = null;
        switch (build.getType()){
            case Research.RESEARCH:
                buildTask = Initialize.getCurrentResearch();break;
            case Facilities.FACILITIES:
                buildTask = Utility.getActivePlanet().getCurrentFacilityBeingBuild(); break;
            case Resources.RESOURCES:
                buildTask = Utility.getActivePlanet().getCurrentBuildingBeingBuild(); break;
            case Shipyard.SHIPYARD:
                BuildTask tempBuildTask = Utility.getActivePlanet().getCurrentFacilityBeingBuild();
                if(tempBuildTask != null &&
                        isIn(tempBuildTask.getBuildable().getName(),
                                Arrays.asList(Facilities.NANITE_FACTORY,Facilities.SHIPYARD)))
                    buildTask = tempBuildTask;
        }
        return buildTask != null && !buildTask.isComplete();
    }

    private boolean isIn(String name, List<String> values) {
        return values.stream().filter(a->a.equals(name)).collect(Collectors.toList()).isEmpty();
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
