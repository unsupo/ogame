package utilities.database;

import database.HSQLDB;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by jarndt on 9/21/16.
 */
public class _HSQLDB {
    private static String defaultName = "ogniterDB";
    private static String dbName = "";
    private HSQLDB db = null;
    private _HSQLDB() throws IOException, SQLException {
        db = new HSQLDB(dbName);
//        db.executeQuery( //Deprecated
//                "CREATE TABLE IF NOT EXISTS PLAYER (" +
//                        "player_name VARCHAR(50), " +
//                        "alliance_name VARCHAR(50), " +
//                        "player_link VARCHAR(250), " +
//                        "alliance_link VARCHAR(250), " +
//                        "alliance_homepage VARCHAR(250), " +
//                        "alliance_tag VARCHAR(250), " +
//                        "player_rank INT," +
//                        "player_status VARCHAR(10)," +
//                        "PRIMARY KEY (player_name)" +
//                        ");");
//
//        db.executeQuery(
//                "CREATE TABLE IF NOT EXISTS PLANETS (" +
//                        "player_name VARCHAR(50), " +
//                        "planet_name VARCHAR(50), " +
//                        "moon_name VARCHAR(50), " +
//                        "moon_size VARCHAR(50), " +
//                        "coordinates varchar(50)," +
//                        "PRIMARY KEY (player_name,coordinates)" +
//                ");");


        db.executeQuery(
                "CREATE TABLE IF NOT EXISTS SERVER(" +
                        "name VARCHAR(50), " +
                        "number INT," +
                        "language VARCHAR(2)," +
                        "timezone VARCHAR(100)," +
                        "timezoneOffset VARCHAR(20)," +
                        "domain VARCHAR(250)," +
                        "version VARCHAR(10)," +
                        "speed INT," +
                        "speedFleet INT," +
                        "galaxies INT," +
                        "systems INT, " +
                        "acs INT, " +
                        "rapidFire INT, " +//0 for no 1 for yes
                        "defToTF INT, " +//0 for no 1 for yes
                        "debrisFactor DECIMAL, " +
                        "debrisFactorDef DECIMAL, " +
                        "repairFactor DECIMAL, " +
                        "newbieProtectionLimit INT, " +
                        "newbieProtectionHigh INT, " +
                        "topScore BIGINT, " +
                        "bonusFields INT, " +
                        "donutGalaxy INT, " +//0 for no 1 for yes
                        "donutSystem INT, " +//0 for no 1 for yes
                        "wfEnabled INT, " +//0 for no 1 for yes
                        "wfMinimumRessLost INT, " +
                        "wfMinimumLossPercentage INT," +
                        "wfBasicPercentageRepairable INT, " +
                        "globalDeuteriumSaveFactor INT, " +
                        "PRIMARY KEY (domain)" +
                ");");

        db.executeQuery(
                "CREATE TABLE IF NOT EXISTS HIGHSCORE_PLAYER( " +
                        "position INT, " +
                        "id INT, " +
                        "score BIGINT, " +
                        "universe_id INT, " +
                        "PRIMARY KEY (id,universe_id)" +
                ");");

        db.executeQuery(
                "CREATE TABLE IF NOT EXISTS HIGHSCORE_ALLIANCE( " +
                        "position INT, " +
                        "id INT, " +
                        "score BIGINT, " +
                        "universe_id INT, " +
                        "PRIMARY KEY (id,universe_id)" +
                ");");

        db.executeQuery(
                "CREATE TABLE IF NOT EXISTS PLAYERS( " +
                        "id INT, " +
                        "name VARCHAR(100), " +
                        "status VARCHAR(3), " +
                        "alliance INT, " +
                        "universe_id INT, " +
                        "PRIMARY KEY (id,universe_id)" +
                ");");

        db.executeQuery(
                "CREATE TABLE IF NOT EXISTS ALLIANCES( " +
                        "id INT, " +
                        "name VARCHAR(100), " +
                        "tag VARCHAR(10), " +
                        "homepage VARCHAR(250), " +
                        "logo VARCHAR(250), " +
                        "open INT, " +
                        "player_id INT, " +
                        "universe_id INT, " +
                        "PRIMARY KEY (id,universe_id)" +
                ");");

        db.executeQuery(
                "CREATE TABLE IF NOT EXISTS PLANETS( " +
                        "id BIGINT, " +
                        "player INT, " +
                        "name VARCHAR(100), " +
                        "coords VARCHAR(10), " +
                        "moon_id INT, " +
                        "moon_name VARCHAR(100), " +
                        "moon_size INT, " +
                        "universe_id INT, " +
                        "PRIMARY KEY (id,universe_id)" +
                ");");

        db.executeQuery(
                "CREATE TABLE IF NOT EXISTS ESPIONAGE_REPORTS(" +
                        "coords VARCHAR(20), " +
                        "msgDate BIGINT, " +
                        "activityType VARCHAR(5), " +
                        "activityString VARCHAR(100), " +
                        "metal BIGINT, " +
                        "crystal BIGINT, " +
                        "deuterium BIGINT, " +
                        "lootPercent INT, " +
                        "counterEspionagePercent INT, " +
                        "fleets BIGINT, " +
                        "defence BIGINT, " +
                        "universe_id INT, " +
                        "PRIMARY KEY (coords, msgDate, universe_id)" +
                ");");

        db.executeQuery(
                "CREATE TABLE IF NOT EXISTS COMBAT_REPORTS(" +
                        "coords VARCHAR(20), " +
                        "msgDate BIGINT, " +
                        "attacker VARCHAR(100), " +
                        "defender VARCHAR(100), " +
                        "lootedMetal BIGINT, " +
                        "lootedCrystal BIGINT, " +
                        "lootedDeuterium BIGINT, " +
                        "lootPercent INT, " +
                        "attackerLosses BIGINT, " +
                        "defenderLosses BIGINT, " +
                        "actuallyRepaired BIGINT, " +
                        "debrisField BIGINT, " +
                        "universe_id INT, " +
                        "PRIMARY KEY (coords, msgDate,universe_id)" +
                ");");

        db.executeQuery(
                "CREATE TABLE IF NOT EXISTS DONT_ATTACK_LIST(" +
                        "coords VARCHAR(20), " +
                        "universe_id INT, " +
                        "fleets BIGINT DEFAULT -1, " +
                        "defence BIGINT DEFAULT -1, " +
                        "espionageTech INT, " +//espionage tech level used last
                        "probeCount INT, " +//number of probes used last
                        "lostFleet INT, " + //1 if message for coords is Contact with the attacking fleet has been lost. 0 otherwise
                        "PRIMARY KEY (coords,universe_id)" +
                ")");

        db.executeQuery(
                "CREATE TABLE IF NOT EXISTS ATTACK_HISTORY(" +
                        "coords VARCHAR(20), " +
                        "universe_id INT, " +
                        "attack_count INT, " +
                        "last_attack_date BIGINT, " +
                        "PRIMARY KEY (coords,universe_id)" +
                ")");
    }

    static _HSQLDB instance;

    public static _HSQLDB getInstance() throws IOException, SQLException {
        if(instance == null)
            instance = new _HSQLDB();
        return instance;
    }

    public static void setDbName(int dbNameNumber){
        dbName=defaultName+dbNameNumber;
    }
    public static void setDbName(String dbName){
        dbName=defaultName+"_"+dbName;
    }

    public static List<Map<String, Object>> executeQuery(String query) throws IOException, SQLException {
//        try {
            return getInstance().db.executeQuery(query);
//        }catch (SQLTransientConnectionException e){
//            executeQuery(query); //DOC states that the this execption could succeed if tried again:  https://docs.oracle.com/javase/7/docs/api/java/sql/SQLTransientConnectionException.html
//        }
//        return null;
    }

}
