package utilities.database;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by jarndt on 5/8/17.
 */
public class Database {
    public static void main(String[] args) {
        System.out.println(checkForPostgres("localhost:5432/test","ogame","ogame"));
    }

    public static boolean checkForPostgres(String server, String username, String password){ /*127.0.0.1:5432/testdb*/
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://"+server, username, password);
            if (connection != null)
                return true;
        } catch (Exception e) { /* */ }
        return false;
    }



}
