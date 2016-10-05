package objects.ai;

import objects.*;
import objects.Fleet;
import ogame.pages.*;
import ogame.utility.Initialize;
import utilities.Utility;
import utilities.database._HSQLDB;
import utilities.ogame.MissionBuilder;
import utilities.selenium.Task;
import utilities.selenium.UIMethods;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static utilities.database._HSQLDB.executeQuery;

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

    private List<CoordinateTime> targets;

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

                    getTargets();

                    if(!couldntAttackLast) {
                        Utility.clickOnNewPage(ogame.pages.Fleet.FLEET);
                        if (Initialize.getInstance().getFleetSlotsAvailable()-1  == 0){//No Fleet Slots Available
                            System.out.println("No fleet slots available");
                            couldntAttackLast = true;
                            reason = Fleet.FLEET;
                            return;
                        }else if(Utility.getActivePlanet().getShips().get(Ship.SMALL_CARGO) == 0) { //no small cargos
                            System.out.println("No small cargos");
                            couldntAttackLast = true;
                            reason = Ship.SMALL_CARGO;
                            return;
                        }

                        HashMap<String, Integer> map = Utility.getBuildableRequirements(Ship.ESPIONAGE_PROBE);
                        removeCurrentValues(map);
                        List<CoordinateTime> targetList = getTargets();
                        LocalDateTime lastAttackedTime = LocalDateTime.now(ZoneOffset.UTC);
                        int quantity = 10;
                        if(map.size() == 0) { //can build espionage probes
                            if (Utility.getActivePlanet().getShips().get(Ship.ESPIONAGE_PROBE) != 0) {
                                batchSpyOnTargetBeforeAttacking(targetList,Initialize.getInstance().getFleetSlotsAvailable()-1);
                                targetList = getEspionageTargets();
                                if(targetList.isEmpty()) //TODO fix bashing no more than 6 attacks per planet in 24 hour period
                                    return;

                                quantity = getQuantity(targetList.get(0).coords);
                            }else {
                                BuildTask.addABuildTask("build: "+Ship.ESPIONAGE_PROBE+",1");
                                return; //no probes, build 1
                            }
                        }else{ //can't spy so use a test small cargo if you don't know the target is safe
                            removeUnsafeTargets(); //no targets or no targets without defence or fleets
                            targetList = getSafeTargets();
                            quantity = 1;
                            if(!targetList.isEmpty()) //no targets, then attack with 1 test small cargo ship
                                quantity = getSmallCargosNeeded(targetList.get(0).getCoords());
                        }
                        if (Utility.getFleetSlots() == 0)//No Fleet Slots Available
                            return;

                        MissionBuilder v = new MissionBuilder().setMission(MissionBuilder.ATTACK)
                                .setDestination(targetList.get(0).getCoords())
                                .setFleet(new Fleet().addShip(Ship.SMALL_CARGO, quantity))
                                .sendFleet();
                        if(v == null)
                            return;

                        targets.get(targets.indexOf(targetList.get(0))).setTimeLastAttacked(lastAttackedTime);

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

    private int getQuantity(Coordinates coords) throws IOException, SQLException {
        List<Map<String, Object>> list = _HSQLDB.executeQuery(
                "select * from espionage_reports where universe_id = " + Initialize.getUniverseID() + " and " +
                "coords = '" + coords.getStringValue() + "' order by msgDate desc");

        if(list.isEmpty())
            return 10;

        Map<String, Object> v = list.get(0);
        long metal = Long.parseLong(v.get("METAL").toString()),
                crystal = Long.parseLong(v.get("CRYSTAL").toString()),
                dueterium = Long.parseLong(v.get("DEUTERIUM").toString());

        return (int) (Math.ceil((metal+crystal+dueterium)/5000));
    }

    private void removeUnsafeTargets() throws IOException, SQLException {
        //used before probes to remove all targets that you've lost a fleet to or in other words targets with defence
        executeQuery("select * from dont_attack_list")
                .forEach(a->targets.remove(new Coordinates(a.get("COORDS").toString())));
    }

    private void batchSpyOnTargetBeforeAttacking(List<CoordinateTime> targets, int batchCount) throws IOException, SQLException {
        int j = 0, i = 0;
        while(j++<batchCount && i++<100)
            if(getLastSpiedOnDate(targets.get(j).getCoords()).until(LocalDateTime.now(ZoneOffset.UTC),ChronoUnit.MINUTES) > 120) //if it's been more than two hours since last spied on date
                new MissionBuilder().setFleet(new Fleet().addShip(Ship.ESPIONAGE_PROBE,1))
                        .setMission(MissionBuilder.ESPIONAGE) //spy on target
                        .setDestination(targets.get(j).getCoords())
                        .sendFleet();
            else batchCount++;
    }

    private LocalDateTime getLastSpiedOnDate(Coordinates coordinates) throws IOException, SQLException {
        List<Map<String, Object>> listDates = executeQuery("select * from ESPIONAGE_REPORTS " +
                "where universe_id = " + Initialize.getUniverseID() + " and " +
                "coords in ('" + coordinates.getStringValue() + "','[" + coordinates.getStringValue() + "]') " +
                "order by msgDate desc");
        long lastDate = 0;
        if(listDates.size() != 0) {
//            if(listDates.get(0).get("DEFENCE") < 0){
//                System.out.println(listDates.get(0).get("COORDS")+" needs more probes");
//
//            }

            lastDate = (long) listDates.get(0).get("MSGDATE");
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(lastDate), ZoneOffset.UTC);
    }

    public void spyOnTargetBeforeAttacking(Coordinates coordinates) throws IOException {
        new MissionBuilder().setFleet(new Fleet().addShip(Ship.ESPIONAGE_PROBE,1))
                    .setMission(MissionBuilder.ESPIONAGE)
                    .setDestination(coordinates);
    }

    @Override
    public Task getTask() throws IOException {
        return getNextBuildTask();
    }

    LocalDateTime recallTime;
    @Override
    public Task getAttackedTask() {
        if(Utility.isBeingAttack()) { //change for testing
            new Task(new Runnable() {
                @Override
                public void run() {

                    List<Mission> missions = Mission.getActiveMissions()
                            .stream().filter(a -> a.getMissionType().equals(MissionBuilder.ATTACK) && !a.isOwnFleet())
                            .collect(Collectors.toList());
                    Collections.sort(missions,(a,b)->b.getArrivalTime().compareTo(a.getArrivalTime()));
                    try {
                        final Planet destination = Initialize.getPlanetMap().get(missions.get(0).getDestination());
                        UIMethods.clickOnAttributeAndValue("id",destination.getWebElement());
                        destination.setCurrentResources(Utility.readResource());
                        Utility.clickOnNewPage(Fleet.FLEET);

                        Coordinates deployment = null;
                        String mission = MissionBuilder.DEPLOYMENT;
                        if(Initialize.getPlanetMap().size() > 1)
                            deployment = Initialize.getPlanetMap().keySet().stream()
                                    .filter(a -> !a.equals(destination.getCoordinates()))
                                    .collect(Collectors.toList()).get(0);
                        else {
                            deployment = getTargets().get(0).coords;
                            mission = MissionBuilder.ATTACK;
                        }
                        if(missions.get(0).getArrivalTime().until(LocalDateTime.now(), ChronoUnit.SECONDS) < 60)
                            new MissionBuilder().setSource(destination.getCoordinates()) //set source to planet being attacked
                                    .setFleet(new Fleet().setShipsByName(destination.getShips())) //set source planets ships
                                    .setResourceToSend(destination.getCurrentResources()) //set source planets resources
                                    .setDestination(deployment)
                                    .setSpeed(10)
                                    .setMission(mission)
                                    .sendFleet();

                        recallTime = LocalDateTime.now().plus(10, ChronoUnit.MINUTES);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

//                    if(recallTime != null && recallTime.)//TODO recall the fleetsave
                }
            });
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
    }

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

    public List<CoordinateTime> getTargets() throws IOException, SQLException {
        if(targets == null)
            targets = Utility.getAllInactiveTargets(Utility.getActivePlanet().getCoordinates(),Initialize.getUniverseID())
                    .stream().map(a->new CoordinateTime(a)).collect(Collectors.toList());

        List<Map<String, Object>> lastAttackDates = executeQuery("select coords,max(msgdate) msgdate " +
                "from combat_reports where universe_id = " + Initialize.getUniverseID() + " and " +
                "defender != '" + Initialize.getUsername() + "' group by coords");
        Coordinates yourCoordinates = Utility.getActivePlanet().getCoordinates();

        targets.forEach(a->{
            List<Map<String, Object>> vv = lastAttackDates.stream()
                    .filter(b -> new Coordinates(b.get("COORDS").toString()).equals(a.getCoords()))
                    .collect(Collectors.toList());//.get(0);
            if(vv.size() == 0)
                return;
            Map<String, Object> v = vv.get(0);            
            
            long millis = Long.parseLong(v.get("MSGDATE").toString());
            LocalDateTime lastAttackDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
            if(lastAttackDate.getNano() > a.getTimeLastAttacked().getNano())
                a.setTimeLastAttacked(lastAttackDate);
        });
        
        Collections.sort(targets,(a,b)->{
            LocalDateTime now = LocalDateTime.now();
            long t = now.until(a.timeLastAttacked,ChronoUnit.MINUTES) - now.until(b.timeLastAttacked,ChronoUnit.MINUTES);
            long c = yourCoordinates.getDistance(a.coords)-yourCoordinates.getDistance(b.coords);
            if(t != 0)
                return (int)t;
            return (int)c;
        });
        return targets;
    }


    public List<CoordinateTime> getSafeTargets() throws IOException, SQLException {
        List<Map<String, Object>> safeTargetsList = executeQuery("select * from combat_reports " +
                "where universe_id = " + Initialize.getUniverseID() + " and " +
                "defender != '" + Initialize.getUsername() + "' and " +
                "defenderLosses = 0 and attackerLosses = 0 " +
                "order by lootedCrystal desc");
        List<CoordinateTime> safeTargets = new ArrayList<>();
        if(!safeTargetsList.isEmpty())
            safeTargetsList.forEach(a->safeTargets.add(new CoordinateTime(new Coordinates(a.get("COORDS").toString()))));
        return safeTargets;
    }

    public int getSmallCargosNeeded(Coordinates coordinates) throws IOException, SQLException {
        List<Map<String, Object>> safeTarget = executeQuery("select * from combat_reports " +
                "where universe_id = " + Initialize.getUniverseID() + " and " +
                "defender != '" + Initialize.getUsername() + "' and " +
                "defenderLosses = 0 and attackerLosses = 0 " +
                "and coords = '" + coordinates.getStringValue() + "' " +
                "order by lootedCrystal desc");
        int smallCargosNeeded = 10;
        if(!safeTarget.isEmpty()){
            Map<String, Object> vv = safeTarget.get(0);
            long m = Long.parseLong(vv.get("LOOTEDMETAL").toString()),
                    c = Long.parseLong(vv.get("LOOTEDCRYSTAL").toString()),
                    d = Long.parseLong(vv.get("LOOTEDDEUTERIUM").toString());
            long v = m+c+d;
            smallCargosNeeded = (int)v/5000;
        }
        return smallCargosNeeded;
    }

    public List<CoordinateTime> getEspionageTargets() throws IOException, SQLException {
        List<Map<String, Object>> list = _HSQLDB.executeQuery(
                "select * from espionage_reports " +
                        "where defence = 0 and fleets = 0 and universe_id = "+Initialize.getUniverseID()+
                        " order by crystal desc");
        List<CoordinateTime> inactiveTargets = getTargets();
        if(list.size() != 0)
            return list.stream()
                    .map(a->inactiveTargets.stream().filter(b->b.coords.equals(new Coordinates(a.get("COORDS").toString())))
                            .collect(Collectors.toList()).get(0))
                    .filter(a->a.getTimeLastAttacked()
                            .until(LocalDateTime.now(ZoneOffset.UTC), ChronoUnit.HOURS) > 6) //get only coords that haven't been attacked for two hours
                    .collect(Collectors.toList());

        checkTooFewProbes();

        return new ArrayList<>();
    }

    private void checkTooFewProbes() throws IOException, SQLException {
        List<Map<String, Object>> tooFewProbes = _HSQLDB.executeQuery(
                "select distinct(coords),fleets,defence from espionage_reports " +
                        "where defence = -1 and universe_id = "+Initialize.getUniverseID());

        if(!tooFewProbes.isEmpty()) {
            System.out.println("Too few probes used first time, trying more");
            int j = 0, i = 0, batchCount = Initialize.getInstance().getFleetSlotsAvailable();
            while (j < tooFewProbes.size() && j < batchCount && i++ < 100) {
                if (getLastSpiedOnDate(new Coordinates(tooFewProbes.get(j).get("COORDS").toString()))
                        .until(LocalDateTime.now(ZoneOffset.UTC), ChronoUnit.MINUTES) > 2) {
                    Map<String, Object> a = tooFewProbes.get(j);
                    Coordinates coords = new Coordinates(a.get("COORDS").toString());
                    long fleet = Long.parseLong(a.get("FLEETS").toString());
                    long defence = Long.parseLong(a.get("DEFENCE").toString());
                    int espionageTech = Initialize.getResearches().get(Research.ESPIONAGE);
                    int probeCount = 1;
                    Utility.markAsDontAttack(coords, probeCount, fleet, defence);

                    Map<String, Object> v = executeQuery(
                            "select * from dont_attack_list where universe_id = " + Initialize.getUniverseID() +
                                    " and coords = '" + coords.getStringValue() + "'").get(0); //must exist because of the method above
                    int o_nP = Integer.parseInt(v.get("PROBECOUNT").toString());
                    int o_mE = Integer.parseInt(v.get("ESPIONAGETECH").toString());
                    long d = Long.parseLong(v.get("DEFENCE").toString()),
                            f = Long.parseLong(v.get("FLEETS").toString());
                    int o_v = 2;
                    if (d == -1 && f == -1)
                        o_v = 1;

                    int mE = Initialize.getResearches().get(Research.ESPIONAGE);
                    int nP = (int) (3 + Math.pow(Math.sqrt(o_nP - o_v) + o_mE - mE, 2));

                    if (Utility.getActivePlanet().getShips().get(Ship.ESPIONAGE_PROBE) < nP) {
                        BuildTask.addABuildTask("build: "+Ship.ESPIONAGE_PROBE+"," + nP);
                        return;
                    }
                    if (Initialize.getInstance().getFleetSlotsAvailable() == 0)//No Fleet Slots Available
                        return;

                    new MissionBuilder().setMission(MissionBuilder.ESPIONAGE)
                            .setDestination(coords)
                            .setFleet(new Fleet().addShip(Ship.ESPIONAGE_PROBE, nP))
                            .sendFleet();

                    executeQuery("update dont_attack_list " +
                            "set espionageTech = " + mE + ", probeCount = " + nP + " " +
                            "where universe_id = " + Initialize.getUniverseID() +
                            " and coords = '" + coords.getStringValue() + "'");

                } else batchCount++;
                j++;
            }
        }
    }
}

class CoordinateTime{
    Coordinates coords;
    LocalDateTime timeLastAttacked = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneOffset.UTC);

    public Coordinates getCoords() {
        return coords;
    }

    public void setCoords(Coordinates coords) {
        this.coords = coords;
    }

    public LocalDateTime getTimeLastAttacked() {
        return timeLastAttacked;
    }

    public void setTimeLastAttacked(LocalDateTime timeLastAttacked) {
        this.timeLastAttacked = timeLastAttacked;
    }

    public CoordinateTime(Coordinates coords, LocalDateTime timeLastAttacked) {
        this.coords = coords;
        this.timeLastAttacked = timeLastAttacked;
    }

    public CoordinateTime(Coordinates coords) {
        this.coords = coords;
    }

    @Override
    public String toString() {
        return "CoordinateTime{" +
                "coords=" + coords +
                ", timeLastAttacked=" + timeLastAttacked +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoordinateTime that = (CoordinateTime) o;

        if (coords != null ? !coords.equals(that.coords) : that.coords != null) return false;
        return timeLastAttacked != null ? timeLastAttacked.equals(that.timeLastAttacked) : that.timeLastAttacked == null;

    }

    @Override
    public int hashCode() {
        int result = coords != null ? coords.hashCode() : 0;
        result = 31 * result + (timeLastAttacked != null ? timeLastAttacked.hashCode() : 0);
        return result;
    }
}
