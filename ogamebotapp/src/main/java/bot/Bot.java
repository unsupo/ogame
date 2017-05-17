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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUniverse() {
        return universe;
    }

    public void setUniverse(String universe) {
        this.universe = universe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DriverController getDriverController() {
        return driverController;
    }

    public void setDriverController(DriverController driverController) {
        this.driverController = driverController;
    }


    @Override
    public String toString() {
        return "Bot{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", universe='" + universe + '\'' +
                ", name='" + name + '\'' +
                ", driverController=" + driverController +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bot bot = (Bot) o;

        if (username != null ? !username.equals(bot.username) : bot.username != null) return false;
        if (password != null ? !password.equals(bot.password) : bot.password != null) return false;
        if (universe != null ? !universe.equals(bot.universe) : bot.universe != null) return false;
        if (name != null ? !name.equals(bot.name) : bot.name != null) return false;
        return driverController != null ? driverController.equals(bot.driverController) : bot.driverController == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (universe != null ? universe.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (driverController != null ? driverController.hashCode() : 0);
        return result;
    }
}
