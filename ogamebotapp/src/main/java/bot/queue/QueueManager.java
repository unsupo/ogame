package bot.queue;

import ogame.objects.game.BuildTask;
import ogame.objects.game.Buildable;
import ogame.objects.game.planet.Planet;
import ogame.simulators.BuildingSimulator;
import utilities.database.Database;
import utilities.fileio.FileOptions;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 5/31/17.
 */
public class QueueManager {
//    public static int MAX_METAL = 22, MAX_CRYSTAL = 18, MAX_DEUTERIUM = 18, MAX_ROBOTICS = 10;

    private String planetId;
    private transient Planet planet;

    private transient List<BuildTask> queue = new ArrayList<>();

    public QueueManager(Planet planet) {
        //TODO auto build;
        this.planet = planet;
        this.planetId = planet.getBotPlanetID();
    }

    private transient Database d;
    private Database getDatabase() throws SQLException, IOException, ClassNotFoundException {
        if(d == null)
            d = Database.newDatabaseConnection();
        return d;
    }

    public String getPlanetId() {
        return planetId;
    }

    public void setPlanetId(String planetId) {
        this.planetId = planetId;
    }

    public List<BuildTask> getQueue(int id) throws SQLException, IOException, ClassNotFoundException {
        List<Map<String, Object>> vv = getDatabase().executeQuery(
                "select p.id p_id,b.id b_id,* from bot_profile b, profile p \n" +
                        "\twhere b.profile_id = p.id \n" +
                        "    \t\tand done = 'N' \n" +
                        "    \t\tand bot_id = '"+id+"'\n" +
                        "            and (p.buildable_id,p.build_level) \n" +
                        "            \tnot in \n" +
                        "                \t(select buildable_id, build_level from planet_queue);"
        );
        List<Map<String, Object>> v = getDatabase().executeQuery(
                "select * from planet_queue where done = 'N' and bot_planets_id = " + planetId+"" +
                        " order by build_priority desc, build_timestamp;"
        );
        List<BuildTask> profile = new ArrayList<>(), queueList = new ArrayList<>();
        if(vv != null && vv.size() > 0 && vv.get(0) != null && vv.get(0).size() > 0)
            profile = vv.stream()
                    .map(a-> new BuildTask(a).setBuildPriority((int)a.get("build_priority")+(int)(a.get("priority"))))
                    .collect(Collectors.toList());


        if(v != null && v.size() > 0 && v.get(0) != null && v.get(0).size() > 0)
            queueList = v.stream().map(a -> new BuildTask(a)).collect(Collectors.toList());

        List<BuildTask> diff = new ArrayList<>(profile);
        diff.removeAll(queueList);
        insertBuildTasks(diff);

        Set<BuildTask> allQueue = new HashSet<>();
        allQueue.addAll(queueList);
        allQueue.addAll(profile);
        queue = new ArrayList<>();
        queue.addAll(allQueue);
        if(queue.isEmpty())
            simulateQueue();

        return queue;
    }

    private void insertBuildTasks(List<BuildTask> diff) throws SQLException, IOException, ClassNotFoundException {
        StringBuilder builder = new StringBuilder("");
        for(BuildTask b : diff)
            builder.append("insert into planet_queue(bot_planets_id,buildable_id,build_level,build_priority) " +
                    "   values("+planetId+","+b.getBuildable().getId()+","+b.getCountOrLevel()+","+b.getBuildPriority()+");");
        if(!builder.toString().isEmpty())
            getDatabase().executeQuery(builder.toString());
    }

    private void simulateQueue(){
        try {
            List<Buildable> v = new BuildingSimulator(planet.getAllBuildables()).simulate();
            Collections.reverse(v);
            List<BuildTask> buildTasks = new ArrayList<>();
            for (int i = 0; i < v.size(); i++)
                buildTasks.add(new BuildTask().setBuildable(v.get(i)).setBuildPriority(i));
            setQueue(buildTasks);
            FileOptions.runConcurrentProcessNonBlocking((Callable)()->{insertBuildTasks(buildTasks); return null;});
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setQueue(List<BuildTask> queue) {
        this.queue = queue;
    }

    @Override
    public String toString() {
        return "QueueManager{" +
                "planetId='" + planetId + '\'' +
                ", queue=" + queue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueueManager that = (QueueManager) o;

        if (planetId != null ? !planetId.equals(that.planetId) : that.planetId != null) return false;
        return queue != null ? queue.equals(that.queue) : that.queue == null;
    }

    @Override
    public int hashCode() {
        int result = planetId != null ? planetId.hashCode() : 0;
        result = 31 * result + (queue != null ? queue.hashCode() : 0);
        return result;
    }
}
