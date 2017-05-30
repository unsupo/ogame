package ogame.objects.game;

import java.time.LocalDateTime;

/**
 * Created by jarndt on 5/10/17.
 */
public class BuildTask {
    private Buildable buildable;
    private String line;
    private LocalDateTime startTime, completeTime;
    private int countOrLevel;
    private boolean constructed = false;

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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(LocalDateTime completeTime) {
        this.completeTime = completeTime;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public Buildable getBuildable() {
        return buildable;
    }

    public void setBuildable(Buildable buildable) {
        this.buildable = buildable;
    }

    public int getCountOrLevel() {
        return countOrLevel;
    }

    public void setCountOrLevel(int countOrLevel) {
        this.countOrLevel = countOrLevel;
    }

    public boolean isConstructed() {
        return constructed;
    }

    public void setConstructed(boolean constructed) {
        this.constructed = constructed;
    }
    public boolean isInProgress() {
        return startTime.isBefore(completeTime);
    }

    public boolean isComplete() {
        return LocalDateTime.now().isAfter(completeTime);
    }


    @Override
    public String toString() {
        return "BuildTask{" +
                "buildable=" + buildable +
                ", startTime=" + startTime +
                ", completeTime=" + completeTime +
                ", countOrLevel=" + countOrLevel +
                ", constructed=" + constructed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BuildTask buildTask = (BuildTask) o;

        if (countOrLevel != buildTask.countOrLevel) return false;
        if (constructed != buildTask.constructed) return false;
        if (buildable != null ? !buildable.equals(buildTask.buildable) : buildTask.buildable != null) return false;
        if (line != null ? !line.equals(buildTask.line) : buildTask.line != null) return false;
        if (startTime != null ? !startTime.equals(buildTask.startTime) : buildTask.startTime != null) return false;
        return completeTime != null ? completeTime.equals(buildTask.completeTime) : buildTask.completeTime == null;

    }

    @Override
    public int hashCode() {
        int result = buildable != null ? buildable.hashCode() : 0;
        result = 31 * result + (line != null ? line.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (completeTime != null ? completeTime.hashCode() : 0);
        result = 31 * result + countOrLevel;
        result = 31 * result + (constructed ? 1 : 0);
        return result;
    }

}
