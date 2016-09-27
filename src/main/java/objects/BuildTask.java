package objects;

import ogame.utility.Initialize;
import ogame.utility.QueueManager;
import utilities.filesystem.FileOptions;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 9/26/16.
 */
public class BuildTask{
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

    public BuildTask() {    }

    @Override
    public String toString() {
        return "BuildTask{" +
                "buildable=" + buildable +
                ", line='" + line + '\'' +
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

    public BuildTask(Buildable buildable, int countOrLevel, String nextProfileBuild){
        this.buildable = buildable;
        this.countOrLevel = countOrLevel;
        this.line = nextProfileBuild;
    }

    public static BuildTask getNextBuildTask() throws IOException {
        String nextProfileBuild = QueueManager.getProfileFileContents().stream()
                .filter(a->a.startsWith("build:"))
                .collect(Collectors.toList()).get(0);

        String[] buildAndQuantity = nextProfileBuild.split(":")[1].trim().split(",");
        Buildable build = Initialize.getBuildableByNameIgnoreCase(buildAndQuantity[0].trim());

        int quantity = 0;
        if(buildAndQuantity.length > 1)
            quantity = Integer.parseInt(buildAndQuantity[1].trim());

        return new BuildTask(build,quantity,nextProfileBuild);
    }

    public static void markBuildTaskAsCompleted(BuildTask buildTask) throws IOException {
        File f = QueueManager.getFileContents().entrySet().stream().filter(a->a.getValue().contains(buildTask.getLine()))
                .map(a->a.getKey()).collect(Collectors.toList()).get(0);

        List<String> v = FileOptions.readFileIntoListString(f.getAbsolutePath());
        int index = v.indexOf(buildTask.getLine());
        v.remove(index);
        v.add(index,"#DONE "+buildTask.getLine());
        FileOptions.writeToFileOverWrite(f.getAbsolutePath(),v.stream().collect(Collectors.joining(", ")));
    }

    public boolean isInProgress() {
        return startTime.isBefore(completeTime);
    }
}