package bot;

import bot.queue.QueueManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ogame.objects.User;
import ogame.objects.game.*;
import ogame.objects.game.fleet.FleetInfo;
import ogame.objects.game.planet.Planet;
import ogame.pages.*;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import utilities.database.Database;
import utilities.fileio.FileOptions;
import utilities.password.PasswordEncryptDecrypt;
import utilities.webdriver.DriverController;
import utilities.webdriver.DriverControllerBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static sun.jvm.hotspot.runtime.BasicObjectLock.size;

/**
 * Created by jarndt on 5/10/17.
 */
public class Bot {
    public static void main(String[] args) throws Exception {
//        new Bot(new Login(User.newRandomUser(Server.QUANTUM))).startBot();
        new Bot(new Login(new User("bc3ew9p4yh9qdv8wvj1h", "ib5f982wc4oedy2q1xfn",Server.QUANTUM))).startBot();
        //password: ib5f982wc4oedy2q1xfn
    }

    private transient DriverController driverController;
    private transient Login login;
    private HashMap<String,Planet> planets = new HashMap<>(); //cords->Planet
    private HashMap<String,Integer> research = new HashMap<>();
    private String currentPage;
    private Coordinates currentPlanetCoordinates;
    private transient PageController pageController;
    private FleetInfo fleetInfo = new FleetInfo();
//    private HashMap<String,OgamePage> pages = new HashMap<>(); //pageName->OgamePage

    private long darkMatter = 0;
    private int unreadMessages = 0;

    private transient Database d;

    private int id, ogameUserId, webdriverId;
    private String name;
    private boolean isActive, isBeingAttacked;
    private String startDate;
    private BuildTask currentResearchBeingBuilt;

    private transient ExecutorService buildTaskService;

    private List<BuildTask> buildTasks;

    private String botDataDirectory;

    public Database getDatabase() throws SQLException, IOException, ClassNotFoundException {
        if(d == null)
            d = Database.newDatabaseConnection();
        return d;
    }

    public Bot(DriverController driverController, Login login) throws SQLException, IOException, ClassNotFoundException {
        this.driverController = driverController;
        this.login = login;
        init();
    }

    public Bot(Login login) throws SQLException, IOException, ClassNotFoundException {
        /*This will use a provided email or ogame user to create or log into an account*/
        this.login = login;
        driverController =
                new DriverControllerBuilder()
                        .setName(login.getUser().getUniverse()+"_"+login.getUser().getUsername())
                        .setStartImageThread(true)
                        .setImageThreadValue(1)
                        .setImageThreadTimeUnit(TimeUnit.SECONDS)
                        .build();
        driverController.getDriver().manage().timeouts().pageLoadTimeout(10,TimeUnit.SECONDS);
        init();
    }

    public Bot(){
        /*This will use an unused email to create a new account*/
    }

    private void init() throws SQLException, IOException, ClassNotFoundException {
        pageController = new PageController(this);
        research = new HashMap<>();
        Buildable.getResearch().forEach(a->research.put(a.getName(),0));

        botDataDirectory = driverController.getImageOutputDirectory()+"../data";
        new File(botDataDirectory).mkdirs();

        this.login.setDriverController(driverController);

        FileOptions.runConcurrentProcessNonBlocking((Callable)()->{
            List<Map<String, Object>> v = getDatabase().executeQuery("select * from bot where ogame_user_id = " + login.getUser().getID() + " " +
                    "and webdriver_id = " + driverController.getId());

            if(v != null && v.size() > 0 && v.get(0) != null && v.get(0).size() > 0){
                Map<String, Object> bot = v.get(0);
                id = Integer.parseInt(bot.get("id").toString());
                ogameUserId = Integer.parseInt(bot.get("ogame_user_id").toString());
                webdriverId = Integer.parseInt(bot.get("webdriver_id").toString());
                name = bot.get("name").toString();
                isActive = bot.get("active").toString().equals("A") ? true : false;
                startDate = bot.get("start_date").toString();
            }else{
                getDatabase().executeQuery(
                        "insert into bot(ogame_user_id,webdriver_id,name,active)" +
                        "   values("+login.getUser().getID()+","+driverController.getId()+",'"+driverController.getDriverName()+"','A');");

                v = getDatabase().executeQuery("select * from bot where ogame_user_id = " + login.getUser().getID() + " " +
                        "and webdriver_id = " + driverController.getId());

                Map<String, Object> bot = v.get(0);
                id = Integer.parseInt(bot.get("id").toString());
                ogameUserId = Integer.parseInt(bot.get("ogame_user_id").toString());
                webdriverId = Integer.parseInt(bot.get("webdriver_id").toString());
                name = bot.get("name").toString();
                isActive = bot.get("active").toString().equals("A") ? true : false;
                startDate = bot.get("start_date").toString();
            }
            return null;
        });

        try {
            driverController.getDriver().navigate().to(Login.OGAME_HOMEPAGE);
        }catch (org.openqa.selenium.TimeoutException e){/*DO NOTHING*/}

        loadBot();
    }

    private void loadBot() throws IOException, SQLException, ClassNotFoundException {
        List<File> botData = FileOptions.findFile(botDataDirectory, getName() + "_data.json");
        Bot bot = null;
        if(botData != null && botData.size() > 0){
            bot = new Gson().fromJson(FileOptions.readFileIntoString(botData.get(0).getAbsolutePath()),
                    new TypeToken<Bot>() {}.getType());
        }else {
            List<Map<String, Object>> v = getDatabase().executeQuery("select * from json_data where bot_id = " + getId());
            if(v != null && v.size() > 0 && v.get(0) != null && v.get(0).size() > 0)
                bot = new Gson().fromJson(v.get(0).get("json_data").toString(),
                        new TypeToken<Bot>() {}.getType());
        }
        if(bot == null)
            return;

        this.planets = bot.planets;
        this.research = bot.research;
        this.currentPage = bot.currentPage;
        this.currentPlanetCoordinates = bot.currentPlanetCoordinates;
        fleetInfo = bot.fleetInfo;
        darkMatter = bot.darkMatter;
        unreadMessages = bot.unreadMessages;
        id = bot.id;
        ogameUserId = bot.ogameUserId;
        webdriverId = bot.webdriverId;
        name = bot.name;
        isActive = bot.isActive;
        startDate = bot.startDate;
    }

    public void startBot() throws InterruptedException, ClassNotFoundException, SQLException, URISyntaxException, IOException {
        String ogamePage = Overview.OVERVIEW;
        while (true){ //this is the main loop.  This will happen on every page change
            try {
                //try to log in, if not logged in then sleep for a second and then try to log in again.
                if (!login.login()) {
                    Thread.sleep(1000);
                    continue;
                }
                setBuildTaskService(FileOptions.runConcurrentProcessNonBlocking((Callable)()->{setBuildTasks(getNextBuildTask());return null;}));
                getDriverController().getDriver().navigate().refresh();

                performNextAction();

            }catch (Exception e){
                /*DO NOTHING*/
            }
        }
    }

    private void performNextAction() throws IOException, InterruptedException {
        //Check if being attacked perform attack action
        if(isBeingAttacked) {
            preformBeingAttackedAction();
            return;
        }

        //if the executor service is done and there are build task to do then do them
        //in other words build the next building that you can
        if(getBuildTaskService().isShutdown() && getBuildTaskService().isTerminated())
            if(getBuildTasks() != null && getBuildTasks().size() > 0) {
                performNextBuildTask();
                return;
            }

        performIdleTask();

        //Literally nothing to do, just go to the overview page.
        Thread.sleep(30000); //wait 30 seconds before updating page.  This helps from ogame logging you out
        getPageController().goToPage(Overview.OVERVIEW);
    }

    private void performIdleTask() throws IOException {
        //TODO
        //Unread messages
        if(getUnreadMessages() != 0){
            //TODO
            return;
        }

        List<Planet> merchantItems = getPlanets().values().stream().filter(a -> a.canGetMerchantItem()).collect(Collectors.toList());
        if(merchantItems.size() != 0){
            //TODO
            List<Planet> currentPlanet = merchantItems.stream().filter(a -> a.getCoordinates().equals(currentPlanetCoordinates)).collect(Collectors.toList());
            if(currentPlanet.size() == 1){
                //you are on the current planet
                //get the merchant item
                //TODO
                if(driverController.waitForElement(By.xpath(pageController.getPage(Merchant.MERCHANT).uniqueXPath()),1L,TimeUnit.MINUTES)){
                    //You're on the merchant page
                    //buy the merchant item
                    return;
                }
                //you're not on the merchant page, go to the merchant page
                //this will go to the merchant page then cycle back through the other more important events all over again.
                //TODO uncomment when if statement is complete or continuous loop
//                pageController.goToPage(Merchant.MERCHANT);
                return;
            }

            //go to next planet in merchantItems list that can get a merchant item and
//            pageController.goToPageOnPlanet(merchantItems.get(0).getCoordinates(),Merchant.MERCHANT);
        }

        //Send fleet out
        if(fleetInfo.getFleetsRemaining() == 1 || fleetInfo.getFleetsRemaining() - 1 > 0){
            //TODO
            //probably shouldn't loop over each planet?????
            //loop over each planet one by one and try to send out fleets.
            //first see if you can send a probe or check databse info etc
            //check if this bot should send fleet out
        }

    }

    private void performNextBuildTask() throws IOException {
        //TODO
        //TODO research at same time as buildings
        List<BuildTask> tasks = getBuildTasks();
        if(tasks != null && tasks.size() > 0){
            List<BuildTask> currentPlanetBuild = tasks.stream().filter(a -> a.getCoordinates().equals(currentPlanetCoordinates)).collect(Collectors.toList());
            if(currentPlanetBuild.size() == 0) {
                //TODO change planet
//                pageController.goToPageOnPlanet(tasks.get(0).getCoordinates(),tasks.get(0).getBuildable().getType());
                return;
            }
            BuildTask build = tasks.get(0);
            if(getDriverController().getDriver().findElements(By.xpath(pageController.getPage(build.getBuildable().getType()).uniqueXPath())).size() > 0) {
                //YOU are on the correct page build the item in question
                PageController.parseGenericBuildings(Jsoup.parse(getDriverController().getDriver().getPageSource()),this);
                getDriverController().executeJavaScript(build.getBuildable().getQuickBuildLink());
                //TODO dark matter
//                driverController.clickWait(By.cssSelector(build.getBuildable().getCssSelector()+" > a"),1L,TimeUnit.MINUTES);
//                driverController.waitForElement(By.cssSelector("#content"),1L,TimeUnit.MINUTES);
//                if(parseOpenedPanel(getCurrentPlanet(),driverController.getDriver().getPageSource()))
//                    driverController.clickWait(By.xpath("//*[@id=\"content\"]/div[2]/a"),1L,TimeUnit.MINUTES);

                return;
            }
            //you are not on the correct page, go to the correct page
            pageController.goToPage(build.getBuildable().getType());
            return;
        }
    }

    private boolean parseOpenedPanel(Planet currentPlanet, String pageSource) {
        Elements v = Jsoup.parse(pageSource).select("#content");
        String currentLevel = v.select("span.level").text().trim();

        String name = v.select("h2").text().trim();

        String time = v.select("#buildDuration").get(0).ownText().trim();

        try {
            String energyNeeded = v.select("span.time").get(1).text().trim();
        }catch (Exception e){/*DO NOTHING*/}

        Elements costs = v.select("#costs");
        List<String> resources = Arrays.asList("metal","crystal","deuterium");
        for(String r : resources){
            Elements res = v.select("li." + r + " > div.cost");
            String cost = res.text().trim();
            boolean hasEnough = !res.hasAttr("overmark");
        }

        Elements build = v.select("a.build-it");
        boolean canBuild = v.select("a.build-it_disabled").size() != 0 ? false : true;
        Buildable b = currentPlanet.getBuildable(name);
        b.setCurrentLevel(Integer.parseInt(currentLevel.replace("Level ","").trim()));
        BuildTask bt = new BuildTask(b, LocalDateTime.now().plusSeconds(parseTime(time)));
        if(canBuild)
            currentPlanet.setCurrentBuildingBeingBuild(bt);

        return canBuild;
    }

    private long parseTime(String time) {
        long t = 0;
        String[] split = time.split(" ");
        for(String s : split){
            if(s.contains("s"))
                t+=Integer.parseInt(s.replace("s","").trim());
            if(s.contains("m"))
                t+=Integer.parseInt(s.replace("m","").trim())*60;
            if(s.contains("h"))
                t+=Integer.parseInt(s.replace("h","").trim())*60*60;
            if(s.contains("d"))
                t+=Integer.parseInt(s.replace("m","").trim())*60*60*24;
            //TODO time larger than days
        }
        return t;
    }

    private void preformBeingAttackedAction() {
        //TODO
    }

    public List<BuildTask> getNextBuildTask() throws SQLException, IOException, ClassNotFoundException {
        //TODO buildable requirements
        List<BuildTask> buildTasks = new ArrayList<>();
        HashMap<String,Planet> planetIDMap = new HashMap<>();
        for(Map.Entry<String, Planet> planet : getPlanets().entrySet()) {
            List<BuildTask> queue = planet.getValue().getQueueManager(getOgameUserId()).getQueue(id)
                    .stream().filter(a->!isDone(a,planet.getValue())).collect(Collectors.toList());

            planetIDMap.put(planet.getValue().getBotPlanetID(),planet.getValue());
            ArrayList<BuildTask> removeList = new ArrayList<>();

            for(BuildTask b : queue){
                if(b.isDone())
                    removeList.add(b);
                Buildable buildable = planet.getValue().getBuildable(b.getBuildable().getName());
                b.setCoordinates(new Coordinates(planet.getKey(),getLogin().getUser().getUniverse()));
                if(b.getCountOrLevel() < 0)
                    buildable.setLevelNeeded(buildable.getCurrentLevel()+1);
                else
                    buildable.setLevelNeeded(b.getCountOrLevel());

                buildTasks.add(b);
            }
            //TODO if cost has energy then put solar sates to get that cost if you don't have it
            HashMap<String,Integer> totalRequirements = new HashMap<>();
            for(BuildTask b : buildTasks) {
                HashMap<String, Integer> requirements = getBotRemainingRequirements(b.getBuildable(), planet);
                for (BuildTask buildTask : queue) {
                    Buildable bb = buildTask.getBuildable();
                    if (requirements.containsKey(bb.getName()) && requirements.get(bb.getName()) <= bb.getCurrentLevel())
                        requirements.remove(bb.getName());
                }
                for(String name : requirements.keySet())
                    if(totalRequirements.containsKey(name)) {
                        if (totalRequirements.get(name) < requirements.get(name))
                            totalRequirements.put(name, requirements.get(name));
                    }else
                        totalRequirements.put(name,requirements.get(name));
            }
            if(totalRequirements.size()!=0) {
                addTotalRequirements(totalRequirements, planet.getValue());
                return getNextBuildTask();
            }
        }
        if(buildTasks.size() <= 0)
            return new ArrayList<>();

        Collections.sort(buildTasks);
        List<BuildTask> buildablePerPlanet = new ArrayList<>();
        String currentPlanetID = buildTasks.get(0).getBotPlanetID();
        buildablePerPlanet.add(buildTasks.get(0));
        for(BuildTask buildTask : buildTasks){
            if(buildTask.getBotPlanetID().equals(currentPlanetID))
                continue;
            buildablePerPlanet.add(buildTask);
            currentPlanetID = buildTask.getBotPlanetID();
        }

        //hopefully by this point, buildablePerPlanet has one buildTask per planet of the highest priority
        List<BuildTask> canBuild = new ArrayList<>();
        for(BuildTask buildTask : buildablePerPlanet) {
            Buildable b = buildTask.getBuildable();
            if(b.getType().toLowerCase().equals(Resources.RESOURCES.toLowerCase()) || b.getType().toLowerCase().equals(Facilities.FACILITIES.toLowerCase())) {
                if (planetIDMap.get(buildTask.getBotPlanetID()).canBuild(buildTask.getBuildable().getName()))
                    canBuild.add(buildTask);
            }else if(b.getType().toLowerCase().equals(Research.RESEARCH.toLowerCase())) {
                if (currentResearchBeingBuilt != null) {
                    if (currentResearchBeingBuilt.isDone() && currentResearchBeingBuilt.isComplete())
                        if (b.getNextLevelCost().canAfford(planetIDMap.get(buildTask.getBotPlanetID()).getResources()))
                            canBuild.add(buildTask);
                }else if (b.getNextLevelCost().canAfford(planetIDMap.get(buildTask.getBotPlanetID()).getResources()))
                    canBuild.add(buildTask);
            }else if(b.getNextLevelCost().canAfford(planetIDMap.get(buildTask.getBotPlanetID()).getResources()))
                canBuild.add(buildTask);
        }

        return canBuild;
    }

    private void addTotalRequirements(HashMap<String, Integer> totalRequirements, Planet value) throws SQLException, IOException, ClassNotFoundException {
        //TODO add to database the prerequisites
        for(String s : totalRequirements.keySet())
            getDatabase().executeQuery(
                    "insert into planet_queue(bot_planets_id,buildable_id,build_level,build_priority)" +
                    "   values("+value.getBotPlanetID()+","+Buildable.getBuildableByName(s).getId()+","+totalRequirements.get(s)+","+10+");"
            );
    }

    private HashMap<String, Integer> getBotRemainingRequirements(Buildable buildable, Map.Entry<String, Planet> planet) {
        HashMap<String, Integer> requirements = Buildable.getBuildableRequirements(buildable.getName());
        List<String> removeMe = new ArrayList<>();
        requirements.forEach((a,z)->{
            Buildable bb = Buildable.getBuildableByName(a);
            if(bb.getType().toLowerCase().equals(Research.RESEARCH)
                    && getResearch().get(a) >= z)
                requirements.remove(a);
            else if(planet.getValue().getAllBuildables().get(a).getCurrentLevel() >= z)
                requirements.remove(a);
        });
        return requirements;
    }

    private boolean isDone(BuildTask task, Planet value) {
        Buildable buildable = task.getBuildable();
        boolean b = true;
        if(buildable.getLevelNeeded() < 0)
            b = false;
        else if(buildable.getType().toLowerCase().equals(Research.RESEARCH.toLowerCase()))
            b = research.get(buildable.getName()) >= buildable.getLevelNeeded();
        else
            b = value.getBuildings().get(buildable.getName()) >= buildable.getLevelNeeded();
        if(b)
            FileOptions.runConcurrentProcessNonBlocking((Callable)()->{
               getDatabase().executeQuery(
                       "update planet_queue set done = 'Y' where bot_planet_id = "+task.getBotPlanetID()+" and buildable_id = "+buildable.getId()
               );
               return null;
            });
        return b;
    }

    public BuildTask getCurrentResearchBeingBuilt() {
        return currentResearchBeingBuilt;
    }

    public void setCurrentResearchBeingBuilt(BuildTask currentResearchBeingBuilt) {
        this.currentResearchBeingBuilt = currentResearchBeingBuilt;
    }

    public ExecutorService getBuildTaskService() {
        return buildTaskService;
    }

    public void setBuildTaskService(ExecutorService buildTaskService) {
        this.buildTaskService = buildTaskService;
    }

    public List<BuildTask> getBuildTasks() {
        return buildTasks;
    }

    public void setBuildTasks(List<BuildTask> buildTasks) {
        this.buildTasks = buildTasks;
    }

    public boolean isBeingAttacked() {
        return isBeingAttacked;
    }

    public void setBeingAttacked(boolean beingAttacked) {
        isBeingAttacked = beingAttacked;
    }

    public void addResearch(Buildable bb) {
        research.put(bb.getName(),bb.getCurrentLevel());
    }

    public Planet getCurrentPlanet(){
        return planets.get(currentPlanetCoordinates.getStringValue());
    }

    public DriverController getDriverController() {
        return driverController;
    }

    public void setDriverController(DriverController driverController) {
        this.driverController = driverController;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public HashMap<String, Planet> getPlanets() {
        return planets;
    }

    public void setPlanets(HashMap<String, Planet> planets) {
        this.planets = planets;
    }

    public HashMap<String, Integer> getResearch() {
        return research;
    }

    public void setResearch(HashMap<String, Integer> research) {
        this.research = research;
    }

    public long getDarkMatter() {
        return darkMatter;
    }

    public void setDarkMatter(long darkMatter) {
        this.darkMatter = darkMatter;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOgameUserId() {
        return ogameUserId;
    }

    public void setOgameUserId(int ogameUserId) {
        this.ogameUserId = ogameUserId;
    }

    public int getWebdriverId() {
        return webdriverId;
    }

    public void setWebdriverId(int webdriverId) {
        this.webdriverId = webdriverId;
    }

    public String getName() {
        if(name == null)
            name = login.getUser().getUniverse()+"_"+login.getUser().getUsername();
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getBotDataDirectory() {
        return botDataDirectory;
    }

    public void setBotDataDirectory(String botDataDirectory) {
        this.botDataDirectory = botDataDirectory;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public Coordinates getCurrentPlanetCoordinates() {
        return currentPlanetCoordinates;
    }

    public void setCurrentPlanetCoordinates(Coordinates currentPlanetCoordinates) {
        this.currentPlanetCoordinates = currentPlanetCoordinates;
    }

    public FleetInfo getFleetInfo() {
        return fleetInfo;
    }

    public void setFleetInfo(FleetInfo fleetInfo) {
        this.fleetInfo = fleetInfo;
    }

    public PageController getPageController() {
        return pageController;
    }

    public void setPageController(PageController pageController) {
        this.pageController = pageController;
    }

    @Override
    public String toString() {
        return "Bot{" +
                "driverController=" + driverController +
                ", login=" + login +
                ", planets=" + planets +
                ", research=" + research +
                ", darkMatter=" + darkMatter +
                ", unreadMessages=" + unreadMessages +
                ", id=" + id +
                ", ogameUserId=" + ogameUserId +
                ", webdriverId=" + webdriverId +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                ", startDate='" + startDate + '\'' +
                ", botDataDirectory='" + botDataDirectory + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bot bot = (Bot) o;

        if (darkMatter != bot.darkMatter) return false;
        if (unreadMessages != bot.unreadMessages) return false;
        if (id != bot.id) return false;
        if (ogameUserId != bot.ogameUserId) return false;
        if (webdriverId != bot.webdriverId) return false;
        if (isActive != bot.isActive) return false;
        if (driverController != null ? !driverController.equals(bot.driverController) : bot.driverController != null)
            return false;
        if (login != null ? !login.equals(bot.login) : bot.login != null) return false;
        if (planets != null ? !planets.equals(bot.planets) : bot.planets != null) return false;
        if (research != null ? !research.equals(bot.research) : bot.research != null) return false;
        if (d != null ? !d.equals(bot.d) : bot.d != null) return false;
        if (name != null ? !name.equals(bot.name) : bot.name != null) return false;
        if (startDate != null ? !startDate.equals(bot.startDate) : bot.startDate != null) return false;
        return botDataDirectory != null ? botDataDirectory.equals(bot.botDataDirectory) : bot.botDataDirectory == null;
    }

    @Override
    public int hashCode() {
        int result = driverController != null ? driverController.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (planets != null ? planets.hashCode() : 0);
        result = 31 * result + (research != null ? research.hashCode() : 0);
        result = 31 * result + (int) (darkMatter ^ (darkMatter >>> 32));
        result = 31 * result + unreadMessages;
        result = 31 * result + (d != null ? d.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + ogameUserId;
        result = 31 * result + webdriverId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (isActive ? 1 : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (botDataDirectory != null ? botDataDirectory.hashCode() : 0);
        return result;
    }
}
