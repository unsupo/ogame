package utilities.database;

import utilities.webdriver.DriverController;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by jarndt on 5/8/17.
 */
public class DatabaseCommons {

    public static void registerDriver(DriverController driverController) throws SQLException, IOException, ClassNotFoundException {
        new Database(Database.DATABASE,Database.USERNAME,Database.PASSWORD);
    }



}
