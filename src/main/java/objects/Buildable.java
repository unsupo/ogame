package objects;

import ogame.utility.Initialize;
import utilities.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarndt on 9/19/16.
 */
public class Buildable {
    private String name;

    public String getWebName() {
        return webName;
    }

    private String webName, line;
    private int id;

    public void setLevelNeeded(int levelNeeded) {
        this.levelNeeded = levelNeeded;
    }

    private int levelNeeded;

    public int getLevelNeeded() {
        return levelNeeded;
    }

    @Override
    public String toString() {
        return "Buildable{" +
                "name='" + name + '\'' +
                ", webName='" + webName + '\'' +
                ", id=" + id +
                ", levelNeeded=" + levelNeeded +
                ", requires='" + getRequires() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Buildable building = (Buildable) o;

        if (id != building.id) return false;
        if (name != null ? !name.equals(building.name) : building.name != null) return false;
        return requires != null ? requires.equals(building.requires) : building.requires == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (requires != null ? requires.hashCode() : 0);
        return result;
    }

    public String getName() {

        return name;
    }

    public int getId() {
        return id;
    }

    public String getRequiresString() {
        return requires;
    }

    public List<Buildable> getRequires(){
        String[] split = requires.split("\\/");
        List<Buildable> buildables = new ArrayList<>();
        for(String s : split){
            if(s == null)
                continue;
            if(s.trim().isEmpty())
                continue;
            String[] subSplit = s.split("\\.");
            Buildable b = null;
            b = (Buildable) Initialize.getBuildableByID(Integer.parseInt(subSplit[0])).clone();
            b.setLevelNeeded(Integer.parseInt(subSplit[1]));
            buildables.add(b);
        }
        return buildables;
    }

    private String requires;

    public Buildable(String line) {
        this.line = line;
        String[] obj = line.split(",");
        id = Integer.parseInt(obj[0]);
        name = obj[1].contains("/")?getObjName(obj[1]):obj[1];
        webName = obj[2];
        requires = obj.length <= 3 ? "" : obj[3];
    }

    private String getObjName(String s) {
        String[] split = s.split("\\/");
        try {
            return Ship.getShipByID(Integer.parseInt(split[1]),Utility.RESOURCE_DIR+split[0]).getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Buildable clone(){
        return new Buildable(this.line);
    }
}
