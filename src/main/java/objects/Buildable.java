package objects;

import utilities.Utility;

import java.io.IOException;

/**
 * Created by jarndt on 9/19/16.
 */
public class Buildable {
    private String name;

    public String getWebName() {
        return webName;
    }

    private String webName;
    private int id;

    @Override
    public String toString() {
        return "Buildable{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", requires='" + requires + '\'' +
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

    public String getRequires() {
        return requires;
    }

    private String requires;

    public Buildable(String line) {
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
}
