package utilities.jsoup;

import objects.Coordinates;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by jarndt on 9/21/16.
 */
public class PlanetPlayer {
    public static void main(String[] args) {
        for(String s : "&nbsp;Moon&nbsp;(8717 km)".split("&nbsp;"))
            System.out.println(s);
    }

    private String planetName, moonName, moonSize, playerName, playerStatus, allianceName, playerLink, allianceLink, playerRank;
    private Coordinates coordinates;

    @Override
    public String toString() {
        return "PlanetPlayer{" +
                "planetName='" + planetName + '\'' +
                ", moonName='" + moonName + '\'' +
                ", moonSize='" + moonSize + '\'' +
                ", playerName='" + playerName + '\'' +
                ", playerStatus='" + playerStatus + '\'' +
                ", allianceName='" + allianceName + '\'' +
                ", playerLink='" + playerLink + '\'' +
                ", allianceLink='" + allianceLink + '\'' +
                ", playerRank='" + playerRank + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanetPlayer that = (PlanetPlayer) o;

        if (planetName != null ? !planetName.equals(that.planetName) : that.planetName != null) return false;
        if (moonName != null ? !moonName.equals(that.moonName) : that.moonName != null) return false;
        if (moonSize != null ? !moonSize.equals(that.moonSize) : that.moonSize != null) return false;
        if (playerName != null ? !playerName.equals(that.playerName) : that.playerName != null) return false;
        if (playerStatus != null ? !playerStatus.equals(that.playerStatus) : that.playerStatus != null) return false;
        if (allianceName != null ? !allianceName.equals(that.allianceName) : that.allianceName != null) return false;
        if (playerLink != null ? !playerLink.equals(that.playerLink) : that.playerLink != null) return false;
        if (allianceLink != null ? !allianceLink.equals(that.allianceLink) : that.allianceLink != null) return false;
        if (playerRank != null ? !playerRank.equals(that.playerRank) : that.playerRank != null) return false;
        return coordinates != null ? coordinates.equals(that.coordinates) : that.coordinates == null;

    }

    @Override
    public int hashCode() {
        int result = planetName != null ? planetName.hashCode() : 0;
        result = 31 * result + (moonName != null ? moonName.hashCode() : 0);
        result = 31 * result + (moonSize != null ? moonSize.hashCode() : 0);
        result = 31 * result + (playerName != null ? playerName.hashCode() : 0);
        result = 31 * result + (playerStatus != null ? playerStatus.hashCode() : 0);
        result = 31 * result + (allianceName != null ? allianceName.hashCode() : 0);
        result = 31 * result + (playerLink != null ? playerLink.hashCode() : 0);
        result = 31 * result + (allianceLink != null ? allianceLink.hashCode() : 0);
        result = 31 * result + (playerRank != null ? playerRank.hashCode() : 0);
        result = 31 * result + (coordinates != null ? coordinates.hashCode() : 0);
        return result;
    }

    public String getPlanetName() {
        return planetName;
    }

    public String getMoonName() {
        return moonName;
    }

    public String getMoonSize() {
        return moonSize;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerStatus() {
        return playerStatus;
    }

    public String getAllianceName() {
        return allianceName;
    }

    public String getPlayerLink() {
        return playerLink;
    }

    public String getAllianceLink() {
        return allianceLink;
    }

    public String getPlayerRank() {
        return playerRank;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public PlanetPlayer(Element e){
        Elements el  = e.select("td");
        for(int i = 0; i<el.size(); i++)
            switch (i){
                case 0: coordinates = new Coordinates(el.get(i).text()); break;
                case 1: planetName = el.get(i).text(); break;
                case 2:
                    if(el.get(i).text().trim().isEmpty())
                        break;
                    String[] split = el.get(i).text().split("[\\\\s\\u00A0]+");
                    moonName = split[1];
                    moonSize = split[2].replace("(","").replace(")","").replace(" km","");
                    break;
                case 3:
                    if(el.get(i).select("a").isEmpty())
                        break;
                    Element link = el.get(i).select("a").get(0);
                    playerLink = link.attr("href");
                    playerName = link.text();
                    String[] playerStatusArr = el.get(i).text().split(" ");//[1].replace("(","").replace(")","");
                    if(playerStatusArr.length == 1)
                        break;
                    playerStatus = playerStatusArr[1].replace("(","").replace(")","");
                    break;
                case 4: playerRank = el.get(i).text(); break;
                case 5:
                    if(el.get(i).select("a").isEmpty())
                        break;
                    Element linkA = el.get(i).select("a").get(0);
                    allianceLink = linkA.attr("href");
                    allianceName = linkA.text().replace("[","").replace("]","");
                    break;
            }
    }
}
