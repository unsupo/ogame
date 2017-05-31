package bot;

import ogame.objects.User;
import ogame.objects.game.*;
import ogame.objects.game.fleet.FleetInfo;
import ogame.objects.game.planet.Planet;
import ogame.pages.*;
import utilities.database.Database;
import utilities.fileio.FileOptions;
import utilities.webdriver.DriverController;
import utilities.webdriver.DriverControllerBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by jarndt on 5/10/17.
 */
public class Bot {
    public static void main(String[] args) throws Exception {
//        new Bot(new Login(User.newRandomUser(Server.QUANTUM))).startBot();
        new Bot(new Login(new User("bc3ew9p4yh9qdv8wvj1h",Server.QUANTUM))).startBot();
        //password: ib5f982wc4oedy2q1xfn
    }

    private transient DriverController driverController;
    private transient Login login;
    private HashMap<Coordinates,Planet> planets = new HashMap<>(); //cords->Planet
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
    private boolean isActive;
    private String startDate;

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

        this.login.setDriverController(driverController);
        botDataDirectory = driverController.getImageOutputDirectory()+"../data";
        new File(botDataDirectory).mkdirs();
        try {
            driverController.getDriver().navigate().to(Login.OGAME_HOMEPAGE);
        }catch (org.openqa.selenium.TimeoutException e){/*DO NOTHING*/}

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
    }

    public void startBot() throws InterruptedException, ClassNotFoundException, SQLException, URISyntaxException, IOException {
        String ogamePage = Overview.OVERVIEW;
        while (true){ //this is the main loop.  This will happen on every page change
            try {
                //try to log in, if not logged in then sleep for a second and then try to log in again.
                if (!login.login()) {
                    Thread.sleep(1000);
                    continue;
                }else //parse information on current page
                    pageController.goToPage(ogamePage);


                //Check if being attacked perform attack action
                //beingAttackedAction();


//                //are there any unread messages
//                if(unreadMessages != 0){
//                    //parse those messages
//                    //read messages which involves parsing the messages
//                    //probably do this in a new tab
//                }

                ogamePage = getNextBuildTask();


            }catch (Exception e){
                /*DO NOTHING*/
            }
        }
    }

    public String getNextBuildTask() throws SQLException, IOException, ClassNotFoundException {
//        List<>
//        for(Map.Entry<Coordinates, Planet> planet : getPlanets().entrySet())

        //TODO
        return Overview.OVERVIEW;
    }

    public void addResearch(Buildable bb) {
        research.put(bb.getName(),bb.getCurrentLevel());
    }

    public Planet getCurrentPlanet(){
        return planets.get(currentPlanetCoordinates);
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

    public HashMap<Coordinates, Planet> getPlanets() {
        return planets;
    }

    public void setPlanets(HashMap<Coordinates, Planet> planets) {
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
