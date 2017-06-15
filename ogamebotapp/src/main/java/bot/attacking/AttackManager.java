package bot.attacking;

import ogame.objects.game.Coordinates;
import ogame.objects.game.data.Server;
import utilities.database.Database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by jarndt on 6/14/17.
 */
public class AttackManager {
    private Server server;
    private int ogameUserId, points, rank;
    private Coordinates mainPlanet;

    private transient Database d;
    private Database getDatabase() throws SQLException, IOException, ClassNotFoundException {
        if(d == null)
            d = Database.newDatabaseConnection();
        return d;
    }

    public AttackManager(Server server, int ogameUserId, int points, int rank, Coordinates coordinates) {
        this.server = server;
        this.ogameUserId = ogameUserId;
        this.points = points;
        this.rank = rank;
        mainPlanet = coordinates;
    }

    public List<Target> getEspionageTargets(){

        return null;
    }

    public List<Target> getAttackTargets(){

        return null;
    }

    public List<Target> getSafeAttackTargets(){

        return null;
    }

    public List<Target> getRecyclerTargets(){

        return null;
    }
}
