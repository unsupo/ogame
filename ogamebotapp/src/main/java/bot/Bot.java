package bot;

import ogame.objects.Planet;
import utilities.webdriver.DriverController;

import java.util.UUID;

/**
 * Created by jarndt on 5/10/17.
 */
public class Bot {
    String username, password, universe, name;
    DriverController driverController;

    public Bot(String username, String password, String universe, String name) {
        this.username = username;
        this.password = password;
        this.universe = universe;
        this.name = name;

        init();
    }

    public Bot(String username, String password, String universe) {
        this.username = username;
        this.password = password;
        this.universe = universe;
        this.name = UUID.randomUUID().toString();

        init();
    }

    private void init() {
    }
}
