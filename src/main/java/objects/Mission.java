package objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utilities.Utility;
import utilities.ogame.MissionBuilder;
import utilities.selenium.UIMethods;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jarndt on 9/26/16.
 */
public class Mission {

    private boolean isReturnFlight, isOwnFleet;

    String missionType;
    Coordinates source;
    Coordinates destination;
    Fleet fleet;
    LocalDateTime arrivalTime;

//    public Mission() {    }
    static private HashMap<String,String> missionTypeMap = new HashMap<>();
    static {
        missionTypeMap.put("https://gf1.geo.gfsrv.net/cdn38/2af2939219d8227a11a50ff4df7b51.gif", MissionBuilder.TRANSPORT);
        missionTypeMap.put("https://gf3.geo.gfsrv.net/cdnb0/4dab966bded2d26f89992b2c6feb4c.gif", MissionBuilder.DEPLOYMENT);
        missionTypeMap.put("https://gf1.geo.gfsrv.net/cdn9a/cd360bccfc35b10966323c56ca8aac.gif", MissionBuilder.ATTACK);
        //TODO get attack mission's gif link
    }

    public Mission(String missionType, Coordinates source, Coordinates destination, Fleet fleet, LocalDateTime arrivalTime, boolean flight, boolean returnFlight) {
        this.missionType = missionType;
        this.source = source;
        this.destination = destination;
        this.fleet = fleet;
        this.arrivalTime = arrivalTime;
        this.isReturnFlight = returnFlight;
        this.isOwnFleet = returnFlight;
    }

    public String getMissionType() {
        return missionType;
    }

    public Mission setMissionType(String missionType) {
        this.missionType = missionType;
        return this;
    }

    public Coordinates getSource() {
        return source;
    }

    public Mission setSource(Coordinates source) {
        this.source = source;
        return this;
    }

    public Coordinates getDestination() {
        return destination;
    }

    public Mission setDestination(Coordinates destination) {
        this.destination = destination;
        return this;
    }

    public Fleet getFleet() {
        return fleet;
    }

    public Mission setFleet(Fleet fleet) {
        this.fleet = fleet;
        return this;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public Mission setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
        return this;
    }

    public boolean isReturnFlight() {
        return isReturnFlight;
    }

    public boolean isOwnFleet() {
        return isOwnFleet;
    }

    public void setOwnFleet(boolean ownFleet) {
        isOwnFleet = ownFleet;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "isReturnFlight=" + isReturnFlight +
                ", isOwnFleet=" + isOwnFleet +
                ", missionType='" + missionType + '\'' +
                ", source=" + source +
                ", destination=" + destination +
                ", fleet=" + fleet +
                ", arrivalTime=" + arrivalTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mission mission = (Mission) o;

        if (isReturnFlight != mission.isReturnFlight) return false;
        if (isOwnFleet != mission.isOwnFleet) return false;
        if (missionType != null ? !missionType.equals(mission.missionType) : mission.missionType != null) return false;
        if (source != null ? !source.equals(mission.source) : mission.source != null) return false;
        if (destination != null ? !destination.equals(mission.destination) : mission.destination != null) return false;
        if (fleet != null ? !fleet.equals(mission.fleet) : mission.fleet != null) return false;
        return arrivalTime != null ? arrivalTime.equals(mission.arrivalTime) : mission.arrivalTime == null;

    }

    @Override
    public int hashCode() {
        int result = (isReturnFlight ? 1 : 0);
        result = 31 * result + (isOwnFleet ? 1 : 0);
        result = 31 * result + (missionType != null ? missionType.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + (fleet != null ? fleet.hashCode() : 0);
        result = 31 * result + (arrivalTime != null ? arrivalTime.hashCode() : 0);
        return result;
    }

    public void setReturnFlight(boolean returnFlight) {
        isReturnFlight = returnFlight;
    }

    public static List<Mission> getActiveMissions(){
        if(!Jsoup.parse(UIMethods.getWebDriver().getPageSource()).select("#eventboxBlank").attr("style").equals("display: none;"))
            return Arrays.asList();
        UIMethods.clickOnAttributeAndValue("id","eventboxFilled");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Elements table = Jsoup.parse(UIMethods.getWebDriver().getPageSource())
                .select("#eventContent").select("tr");
        List<Mission> missions = new ArrayList<>();
        for(Element e : table) {
            long millis = Utility.getTimeConversion(e.select("td.countdown").text())+System.currentTimeMillis();
            LocalDateTime arrivalTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
            Element vv = e.select("td.missionFleet > img").get(0);
            boolean isOwnFleet = vv.attr("title").split("\\|")[0].equals("Own fleet"); //could use this to get attack type and isReturnFlight too
            String missionType = missionTypeMap.get(vv.attr("src").trim());
            Coordinates source = new Coordinates(e.select("td.coordsOrigin").text().trim());
            Fleet fleet = Fleet.parseFleets(Jsoup.parse(e.select("td.icon_movement_reserve > span").attr("title")));
            Coordinates destination = new Coordinates(e.select("td.destCoords").text().trim());
            boolean returnFlight = Boolean.parseBoolean(e.attr("data-return-flight").trim());

            missions.add(new Mission(missionType,source,destination,fleet,arrivalTime,returnFlight,isOwnFleet));
        }

        return missions;
    }
}
