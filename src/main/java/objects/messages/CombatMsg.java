package objects.messages;

import objects.Coordinates;
import ogame.utility.Resource;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utilities.database._HSQLDB;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 9/30/16.
 */
public class CombatMsg implements IMessage{
    LocalDateTime msgDate;
    Coordinates attackedPlanetCoordinates;
    String attacker, defender;
    long attackerLosses, defenderLosses, actuallyRepaired, debrisField;
    Resource lootedResources;
    int lootPercent;

    public CombatMsg(LocalDateTime msgDate, Element e) {
        this.msgDate = msgDate;
        attackedPlanetCoordinates = new Coordinates(e.select("span.msg_title").select("a").text().trim());

        Elements combatLeftSide = e.select("div.combatLeftSide > span");
        String[] attackerInfo = combatLeftSide.get(0).text().split(": ");
        attacker = attackerInfo[1].replace("(","").replace(")","");
        attackerLosses = EspionageMsg.parseNumber(attackerInfo[2]);
        String[] res = combatLeftSide.get(1).attr("title").split("<br\\/>");
        long metal, crystal, dueterium;
        if(res.length >= 3) {
            metal = EspionageMsg.parseNumber(res[1]);
            crystal = EspionageMsg.parseNumber(res[2]);
            dueterium = EspionageMsg.parseNumber(res[3]);
        }else{
            metal = crystal = dueterium = 0;
        }
        lootedResources = new Resource(metal,crystal,dueterium);
        lootPercent = EspionageMsg.parseNumber(combatLeftSide.get(1)).intValue();
        debrisField = EspionageMsg.parseNumber(combatLeftSide.get(2));

        Elements combatRightSide = e.select("div.combatRightSide > span");
        String[] defenderInfo = combatRightSide.get(0).text().split(": ");
        defender = defenderInfo[1].replace("(","").replace(")","");
        defenderLosses = EspionageMsg.parseNumber(defenderInfo[2]);
        actuallyRepaired = EspionageMsg.parseNumber(combatRightSide.get(1));
    }

    @Override
    public void writeToDatabase(int universeID) throws IOException, SQLException {
        List<String> names = Arrays.asList(("coords,msgDate,attacker," +
                "defender,lootedMetal,lootedCrystal,lootedDeuterium,lootPercent," +
                        "attackerLosses,defenderLosses,actuallyRepaired,debrisField,universe_id").split(","));
        String query = "INSERT INTO COMBAT_REPORTS({NAMES}) VALUES('";
        query = query.replace("{NAMES}",names.stream().collect(Collectors.joining(",")));
        query+=getAttackedPlanetCoordinates().getStringValue()+"','";
        query+=msgDate.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()+"','";
        query+=attacker+"','"+defender+"','"+lootedResources.metal+"','"+lootedResources.crystal+"','"+lootedResources.deuterium+"','";
        query+=lootPercent+"','"+attackerLosses+"','"+defenderLosses+"','"+actuallyRepaired+"','"+debrisField+"','"+universeID+"');";
        try {
            _HSQLDB.executeQuery(query);
        }catch (Exception e){
            if (!e.getMessage().contains("unique constraint")) {
                System.err.println("FAILED QUERY: " + query);
                throw e;
            }
        }
    }

    public LocalDateTime getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(LocalDateTime msgDate) {
        this.msgDate = msgDate;
    }

    public Coordinates getAttackedPlanetCoordinates() {
        return attackedPlanetCoordinates;
    }

    public void setAttackedPlanetCoordinates(Coordinates attackedPlanetCoordinates) {
        this.attackedPlanetCoordinates = attackedPlanetCoordinates;
    }

    public String getAttacker() {
        return attacker;
    }

    public void setAttacker(String attacker) {
        this.attacker = attacker;
    }

    public String getDefender() {
        return defender;
    }

    public void setDefender(String defender) {
        this.defender = defender;
    }

    public long getAttackerLosses() {
        return attackerLosses;
    }

    public void setAttackerLosses(long attackerLosses) {
        this.attackerLosses = attackerLosses;
    }

    public long getDefenderLosses() {
        return defenderLosses;
    }

    public void setDefenderLosses(long defenderLosses) {
        this.defenderLosses = defenderLosses;
    }

    public long getActuallyRepaired() {
        return actuallyRepaired;
    }

    public void setActuallyRepaired(long actuallyRepaired) {
        this.actuallyRepaired = actuallyRepaired;
    }

    public long getDebrisField() {
        return debrisField;
    }

    public void setDebrisField(long debrisField) {
        this.debrisField = debrisField;
    }

    public Resource getLootedResources() {
        return lootedResources;
    }

    public void setLootedResources(Resource lootedResources) {
        this.lootedResources = lootedResources;
    }

    public int getLootPercent() {
        return lootPercent;
    }

    public void setLootPercent(int lootPercent) {
        this.lootPercent = lootPercent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CombatMsg combatMsg = (CombatMsg) o;

        if (attackerLosses != combatMsg.attackerLosses) return false;
        if (defenderLosses != combatMsg.defenderLosses) return false;
        if (actuallyRepaired != combatMsg.actuallyRepaired) return false;
        if (debrisField != combatMsg.debrisField) return false;
        if (lootPercent != combatMsg.lootPercent) return false;
        if (msgDate != null ? !msgDate.equals(combatMsg.msgDate) : combatMsg.msgDate != null) return false;
        if (attackedPlanetCoordinates != null ? !attackedPlanetCoordinates.equals(combatMsg.attackedPlanetCoordinates) : combatMsg.attackedPlanetCoordinates != null)
            return false;
        if (attacker != null ? !attacker.equals(combatMsg.attacker) : combatMsg.attacker != null) return false;
        if (defender != null ? !defender.equals(combatMsg.defender) : combatMsg.defender != null) return false;
        return lootedResources != null ? lootedResources.equals(combatMsg.lootedResources) : combatMsg.lootedResources == null;

    }

    @Override
    public int hashCode() {
        int result = msgDate != null ? msgDate.hashCode() : 0;
        result = 31 * result + (attackedPlanetCoordinates != null ? attackedPlanetCoordinates.hashCode() : 0);
        result = 31 * result + (attacker != null ? attacker.hashCode() : 0);
        result = 31 * result + (defender != null ? defender.hashCode() : 0);
        result = 31 * result + (int) (attackerLosses ^ (attackerLosses >>> 32));
        result = 31 * result + (int) (defenderLosses ^ (defenderLosses >>> 32));
        result = 31 * result + (int) (actuallyRepaired ^ (actuallyRepaired >>> 32));
        result = 31 * result + (int) (debrisField ^ (debrisField >>> 32));
        result = 31 * result + (lootedResources != null ? lootedResources.hashCode() : 0);
        result = 31 * result + lootPercent;
        return result;
    }

    @Override
    public String toString() {
        return "CombatMsg{" +
                "msgDate=" + msgDate +
                ", attackedPlanetCoordinates=" + attackedPlanetCoordinates +
                ", attacker='" + attacker + '\'' +
                ", defender='" + defender + '\'' +
                ", attackerLosses=" + attackerLosses +
                ", defenderLosses=" + defenderLosses +
                ", actuallyRepaired=" + actuallyRepaired +
                ", debrisField=" + debrisField +
                ", lootedResources=" + lootedResources +
                ", lootPercent=" + lootPercent +
                '}';
    }
}
