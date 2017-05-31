package ogame.objects.game;

import ogame.pages.Facilities;
import ogame.pages.Resources;
import utilities.database.Database;
import utilities.fileio.FileOptions;
import utilities.fileio.JarUtility;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 5/8/17.
 */
public class Buildable {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
//        Ship.getAllShips().forEach(a->System.out.println(a.getName()+Buildable.getBuildableRequirements(a.getName())));
//        Buildable.getDefense().forEach(System.out::println);
//        Buildable crystalMine = Buildable.getBuildableByName(Resources.CRYSTAL_MINE);
//        for (int i = 0; i < 34; i++) {
//            System.out.println((crystalMine.getCurrentLevel()+1)+"="+crystalMine.getNextLevelCost());
//            crystalMine.setCurrentLevel(crystalMine.getCurrentLevel()+1);
//        }
//        try {
//            getInstance().executor.awaitTermination(1L, TimeUnit.MINUTES);
//        }finally {
//            getInstance().executor.shutdownNow();
//        }//        }//            while (!getInstance().executor.isTerminated() && !getInstance().executor.isShutdown())
//                Thread.sleep(1000);
//        }
//        Database.newDatabaseConnection().executeQuery("select * from buildable;")
//            .forEach(System.out::println);

        System.out.println(Buildable.getBuildableByName(Ship.BATTLECRUISER));
    }

    public static final String
            RESOURCE_DIR    = FileOptions.cleanFilePath(JarUtility.getResourceDir()+"ogame/buildable_files/"),
            PROFILE_DIR     = RESOURCE_DIR+"/profile/",
            SHIP_INFO       = RESOURCE_DIR+"ogame_ship_info.csv",
            BATTLE_INFO     = RESOURCE_DIR+"battle_info",
            RESEARCH_INFO   = RESOURCE_DIR+"research_info.csv",
            FACILITIES_INFO = RESOURCE_DIR+"facilities_info.csv",
            BUILDING_INFO   = RESOURCE_DIR+"building_info.csv",
            SHIPYARD_INFO   = RESOURCE_DIR+"shipyard_info.csv",
            DEFENSE_INFO    = RESOURCE_DIR+"defense_info.csv",
            MAPPINGS        = RESOURCE_DIR+"mapper.csv",
            LAST_UPDATE     = RESOURCE_DIR+"last_update";

    private String name, webName, line, type, requires;
    private int id, levelNeeded, currentLevel;
    private double costMultiplier;

    private Resource baseCost, nextLevelCost, currentProduction, baseProduction, baseEnergyConsuption, energyConsumption;


    public int getCurrentLevel() {
        return currentLevel;
    }

    public Buildable setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
        return this;
    }

    public Resource getNextLevelCost() {
        if(currentLevel == 0)
            return baseCost;
        return baseCost.geometricPower(costMultiplier,currentLevel+1);
    }
    public Resource getLevelCost(int level){
        if(level == 0)
            return baseCost;
        return baseCost.geometricPower(costMultiplier,level);
    }

    public String getType() {
        return type;
    }

    public String getWebName() {
        return webName;
    }

    public int getLevelNeeded() {
        return levelNeeded;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getRequiresString() {
        return requires;
    }

    public Buildable setLevelNeeded(int levelNeeded) {
        this.levelNeeded = levelNeeded;
        return this;
    }

    @Override
    public String toString() {
        return "Buildable{" +
                "name='" + name + '\'' +
                ", webName='" + webName + '\'' +
                ", type='" + type + '\'' +
                ", requires='" + requires + '\'' +
                ", id=" + id +
                ", levelNeeded=" + levelNeeded +
                ", currentLevel=" + currentLevel +
                ", costMultiplier=" + costMultiplier +
                ", baseCost=" + baseCost +
                ", nextLevelCost=" + getNextLevelCost() +
                ", currentProduction=" + currentProduction +
                ", energyConsumption=" + energyConsumption +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Buildable building = (Buildable) o;

        if (id != building.id) return false;
        if (name != null ? !name.equals(building.name) : building.name != null) return false;
        return requires != null ? requires.equals(building.requires) : building.requires == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (requires != null ? requires.hashCode() : 0);
        return result;
    }

    public List<Buildable> getRequires(){
        String[] split = requires.split("\\/");
        List<Buildable> buildables = new ArrayList<>();
        for(String s : split){
            if(s == null)
                continue;
            if(s.trim().isEmpty())
                continue;
            String[] subSplit = s.split("\\.");
            Buildable b = getBuildableByID(Integer.parseInt(subSplit[0])).clone();
            b.setLevelNeeded(Integer.parseInt(subSplit[1]));
            buildables.add(b);
        }
        return buildables;
    }

    public Buildable(String line) {
        this.line = line;
        String[] obj = line.split(",");
        if("ID".equals(obj[0])) return;
        id = Integer.parseInt(obj[0]);
        Ship s = null;
        if(obj[1].contains("/"))
            s = getObjName(obj[1]);
        name = s == null?obj[1]:s.getName();
        webName = obj[2];
        requires = obj.length <= 3 ? "" : obj[3];
        try {
            type = getType(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(obj.length <= 8) {
            costMultiplier = 1;
            baseCost = s.getCost();
            return;
        }

        costMultiplier = Double.parseDouble(obj[4]);
        baseCost = new Resource(
            Long.parseLong(obj[5]),
            Long.parseLong(obj[6]),
            Long.parseLong(obj[7]),
            Long.parseLong(obj[8])
        );
    }
    //if it's a ship then get the ship's name, else null
    private Ship getObjName(String s) {
        String[] split = s.split("\\/");
        try {
            return Ship.getShipByID(Integer.parseInt(split[1]),RESOURCE_DIR+split[0]);//.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Buildable clone(){
        return new Buildable(this.line);
    }


    /*STATIC STUFF*/

    public static Map<String, Integer> getMissing(String goal, Map<String, Integer> current){
        return getMissing(getBuildableRequirements(goal), current);
    }

    public static Map<String, Integer> getMissing(Map<String, Integer> requirements, Map<String, Integer> current){
        HashMap<String, Integer> missingMap = new HashMap<String, Integer>();
        for(String requirement: requirements.keySet()){
            Integer cur = current.get(requirement);
            if(cur == null){
                cur = 0;
            }
            int missing = requirements.get(requirement) - cur;
            if(missing > 0){
                missingMap.put(requirement, missing);
            }
        }
        return missingMap;
    }
    public Buildable(){}

    private ExecutorService executor;
    private static HashMap<String,Integer> requirements;
    private static List<Buildable> buildables = new ArrayList<>();
    private static HashMap<String,List<Integer>> mappings = new HashMap<>();
    private Buildable(boolean b){
        try {
            loadFiles();

            executor = FileOptions.runConcurrentProcessNonBlocking((Callable)()->{sendBuildablesToDatabase(); return null; });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendBuildablesToDatabase() throws SQLException, IOException, ClassNotFoundException {
        Database d = Database.newDatabaseConnection();
        List<String> v = d.executeQuery("select id from buildable").stream().map(a -> a.get("id").toString()).collect(Collectors.toList());
        StringBuilder builder = new StringBuilder("");
        Buildable.getBuildableObjects().forEach(a->{
            if(!v.contains(a.getId()+""))
                builder.append("insert into buildable(id,buildable,type) values("+a.getId()+",'"+a.getName()+"','"+a.getType()+"'); ");
        });
        d.executeQuery(builder.toString());
    }

    private static Buildable instance;
    private static Buildable getInstance(){
        if(instance == null)
            instance = new Buildable(true);
        return instance;
    }
    private void loadFiles() throws IOException {
        addBuildingFromFile(BUILDING_INFO);
        addBuildingFromFile(RESEARCH_INFO);
        addBuildingFromFile(FACILITIES_INFO);
        addBuildingFromFile(SHIPYARD_INFO);
        addBuildingFromFile(DEFENSE_INFO);
    }private void addBuildingFromFile(String file) throws IOException {
        FileOptions.readFileIntoListString(file).stream().filter(a->!a.contains("ID")).forEach(a->buildables.add(new Buildable(a)));
    }
    public static HashMap<String,Integer> getBuildableRequirements(String buildableName){
        requirements = new HashMap<>();
        getAllRequirements(buildableName);
        return requirements;
    }private static void getAllRequirements(String buildable){
        for(Buildable b : getBuildableByName(buildable).getRequires()){
            if(!requirements.containsKey(b.getName()) ||
                    (requirements.containsKey(b.getName()) && requirements.get(b.getName()) < b.getLevelNeeded()))
                requirements.put(b.getName(),b.getLevelNeeded());
            getAllRequirements(b.getName());
        }
    }

    private Set<Buildable> facilitites, resources, research, shipyard, defense;

    public static Set<Buildable> getFacilitites() throws IOException {
        if(getInstance().facilitites == null){
            getInstance().facilitites = new HashSet<>();
            List<Integer> fac = getMappings().get("facilities");
            getInstance().facilitites.addAll(
                    getBuildableObjects().stream()
                            .filter(a->a.getId() <= fac.get(0) && a.getId() >= fac.get(1))
                            .collect(Collectors.toList())
            );
        }
        return getInstance().facilitites;
    }
    private static Set<Buildable> getBuildableType(String type, Set<Buildable> buildables) throws IOException {
        if(buildables == null){
            buildables = new HashSet<>();
            List<Integer> fac = getMappings().get(type);
            buildables.addAll(
                    getBuildableObjects().stream()
                            .filter(a->a.getId() <= fac.get(1) && a.getId() >= fac.get(0))
                            .collect(Collectors.toList())
            );
        }
        return buildables;
    }

    public static Set<Buildable> getResources() throws IOException {
        return getBuildableType("resources",getInstance().resources);
    }
    public static Set<Buildable> getResearch() throws IOException {
        return getBuildableType("research",getInstance().research);
    }
    public static Set<Buildable> getShipyard() throws IOException {
        return getBuildableType("shipyard",getInstance().shipyard);
    }
    public static Set<Buildable> getDefense() throws IOException {
        return getBuildableType("defense",getInstance().defense);
    }


    public static List<Buildable> getBuildableObjects(){
        return getInstance().buildables;
    }
    public static Buildable getBuildableByName(String name){
        return getBuildableObjects().stream().filter(a->name.equals(a.getName())).collect(Collectors.toList()).get(0);
    } public static Buildable getBuildableByNameIgnoreCase(String name){
        return getBuildableObjects().stream().filter(a->name.equalsIgnoreCase(a.getName())).collect(Collectors.toList()).get(0);
    }  public static Buildable getBuildableByID(int id) {
        return getBuildableObjects().stream().filter(a->a.getId() == id).collect(Collectors.toList()).get(0);
    }public static String getType(String name) throws IOException{
        return getType(getBuildableByName(name).getId());
    }public static String getType(int id) throws IOException {
        for(String key : getMappings().keySet()){
            List<Integer> l = getMappings().get(key);
            Integer v1 = l.get(0), v2 = l.get(1);
            if(id <= v2 && id >= v1)
                return key;
        }
        return null;
    }public static HashMap<String, List<Integer>> getMappings() throws IOException {
        if(mappings.isEmpty()) {
            List<String> v = FileOptions.readFileIntoListString(MAPPINGS);
            for(String s : v){
                String[] split = s.split(",");
                mappings.put(split[0].toLowerCase(), Arrays.asList(Integer.parseInt(split[1]),Integer.parseInt(split[2])));
            }
        }
        return mappings;
    }


}
