package ogame.objects.game;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by jarndt on 5/10/17.
 */
public class BuildTask implements Comparable<BuildTask>{
    public static void main(String[] args) {
        List<BuildTask> buildTasks = new ArrayList<>(Arrays.asList(
                new BuildTask().setBuildPriority(100).setCompleteTime(LocalDateTime.now().minusSeconds(1)),
                new BuildTask().setQueuedTime(LocalDateTime.now().plusSeconds(100)),
                new BuildTask().setQueuedTime(LocalDateTime.now().plusSeconds(10)),
                new BuildTask().setBuildPriority(0),
                new BuildTask().setBuildPriority(1000)
        ));
        buildTasks.forEach(System.out::println);
        System.out.println("\n\n\n");
        Collections.sort(buildTasks);
        buildTasks.forEach(System.out::println);
    }

    private Buildable buildable;
    private String line, botPlanetID;
    private LocalDateTime queuedTime, startTime, completeTime;
    private int countOrLevel, buildPriority;
    private boolean done = false;

    public BuildTask(Buildable buildable, LocalDateTime completeTime) {
        this.buildable = buildable;
        this.startTime = LocalDateTime.now();
        this.completeTime = completeTime;
    }
    public BuildTask(Buildable buildable, LocalDateTime completeTime, int countOrLevel) {
        this.buildable = buildable;
        this.startTime = LocalDateTime.now();
        this.completeTime = completeTime;
        this.countOrLevel = countOrLevel;
    }
    public BuildTask(Buildable buildable, int countOrLevel, String nextProfileBuild){
        this.buildable = buildable;
        this.countOrLevel = countOrLevel;
        this.line = nextProfileBuild;
    }

    public BuildTask() {    }

    public String getBotPlanetID() {
        return botPlanetID;
    }

    public BuildTask setBotPlanetID(String botPlanetID) {
        this.botPlanetID = botPlanetID;
        return this;
    }

    public LocalDateTime getQueuedTime() {
        return queuedTime;
    }

    public BuildTask setQueuedTime(LocalDateTime queuedTime) {
        this.queuedTime = queuedTime;
        return this;
    }

    public int getBuildPriority() {
        return buildPriority;
    }

    public BuildTask setBuildPriority(int buildPriority) {
        this.buildPriority = buildPriority;
        return this;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public BuildTask setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public BuildTask setCompleteTime(LocalDateTime completeTime) {
        this.completeTime = completeTime;
        return this;
    }

    public String getLine() {
        return line;
    }

    public BuildTask setLine(String line) {
        this.line = line;
        return this;
    }

    public Buildable getBuildable() {
        return buildable;
    }

    public BuildTask setBuildable(Buildable buildable) {
        this.buildable = buildable;
        return this;
    }

    public int getCountOrLevel() {
        return countOrLevel;
    }

    public BuildTask setCountOrLevel(int countOrLevel) {
        this.countOrLevel = countOrLevel;
        return this;
    }

    public boolean isDone() {
        return done;
    }

    public BuildTask setDone(boolean done) {
        this.done = done;
        return this;
    }
    public boolean isInProgress() {
        if(startTime == null || completeTime == null)
            return false;
        return startTime.isBefore(completeTime);
    }

    public boolean isComplete() {
        if(completeTime == null)
            return false;
        boolean b = LocalDateTime.now().isAfter(completeTime);
        if(b) done = true;
        return b;
    }

    @Override
    public String toString() {
        return "BuildTask{" +
                "buildable=" + buildable +
                ", line='" + line + '\'' +
                ", botPlanetID='" + botPlanetID + '\'' +
                ", queuedTime=" + queuedTime +
                ", startTime=" + startTime +
                ", completeTime=" + completeTime +
                ", countOrLevel=" + countOrLevel +
                ", buildPriority=" + buildPriority +
                ", done=" + done +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BuildTask buildTask = (BuildTask) o;

        if (countOrLevel != buildTask.countOrLevel) return false;
        if (buildPriority != buildTask.buildPriority) return false;
        if (done != buildTask.done) return false;
        if (buildable != null ? !buildable.equals(buildTask.buildable) : buildTask.buildable != null) return false;
        if (line != null ? !line.equals(buildTask.line) : buildTask.line != null) return false;
        if (botPlanetID != null ? !botPlanetID.equals(buildTask.botPlanetID) : buildTask.botPlanetID != null)
            return false;
        if (queuedTime != null ? !queuedTime.equals(buildTask.queuedTime) : buildTask.queuedTime != null) return false;
        if (startTime != null ? !startTime.equals(buildTask.startTime) : buildTask.startTime != null) return false;
        return completeTime != null ? completeTime.equals(buildTask.completeTime) : buildTask.completeTime == null;
    }

    @Override
    public int hashCode() {
        int result = buildable != null ? buildable.hashCode() : 0;
        result = 31 * result + (line != null ? line.hashCode() : 0);
        result = 31 * result + (botPlanetID != null ? botPlanetID.hashCode() : 0);
        result = 31 * result + (queuedTime != null ? queuedTime.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (completeTime != null ? completeTime.hashCode() : 0);
        result = 31 * result + countOrLevel;
        result = 31 * result + buildPriority;
        result = 31 * result + (done ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(BuildTask o) {
        int score = 0;
        if(isComplete()||isDone())
            return Integer.MAX_VALUE;
        if(queuedTime != null)
            score = LocalDateTime.now().compareTo(queuedTime);
        score+=new Integer(buildPriority).compareTo(Integer.MAX_VALUE);
        return score;
    }
}
