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
    public final String dbName = "";
    public HSQLDB db = null;
    private _HSQLDB() throws IOException, SQLException {
        db = new HSQLDB("ogniterDB");
        db.executeQuery(
                "CREATE TABLE IF NOT EXISTS PLAYER (" +
                        "player_name VARCHAR(50), " +
                        "alliance_name VARCHAR(50), " +
                        "player_link VARCHAR(250), " +
                        "alliance_link VARCHAR(250), " +
                        "player_rank INT," +
                        "player_status VARCHAR(10)," +
                        "PRIMARY KEY (player_name)" +
                        ");");

        db.executeQuery(
                "CREATE TABLE IF NOT EXISTS PLANET (" +
                        "player_name VARCHAR(50), " +
                        "planet_name VARCHAR(50), " +
                        "moon_name VARCHAR(50), " +
                        "moon_size VARCHAR(50), " +
                        "coordinates varchar(50)," +
                        "PRIMARY KEY (player_name,coordinates)" +
                        ");");

    }

    static _HSQLDB instance;

    public static _HSQLDB getInstance() throws IOException, SQLException {
        if(instance == null)
            instance = new _HSQLDB();
        return instance;
    }

    public static List<Map<String, Object>> executeQuery(String query) throws IOException, SQLException {
        return getInstance().db.executeQuery(query);
    }

}
