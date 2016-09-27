package objects;

import utilities.selenium.UIMethods;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jarndt on 9/25/16.
 */
public class PlanetProperties {
    private int totalFields, usedFields, size, minTemp, maxTemp;

    @Override
    public String toString() {
        return "PlanetProperties{" +
                "totalFields=" + totalFields +
                ", usedFields=" + usedFields +
                ", size=" + size +
                ", minTemp=" + minTemp +
                ", maxTemp=" + maxTemp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanetProperties that = (PlanetProperties) o;

        if (totalFields != that.totalFields) return false;
        if (usedFields != that.usedFields) return false;
        if (size != that.size) return false;
        if (minTemp != that.minTemp) return false;
        return maxTemp == that.maxTemp;

    }

    @Override
    public int hashCode() {
        int result = totalFields;
        result = 31 * result + usedFields;
        result = 31 * result + size;
        result = 31 * result + minTemp;
        result = 31 * result + maxTemp;
        return result;
    }

    public int getMinTemp() {

        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getTotalFields() {

        return totalFields;
    }

    public void setTotalFields(int totalFields) {
        this.totalFields = totalFields;
    }

    public int getUsedFields() {
        return usedFields;
    }

    public void setUsedFields(int usedFields) {
        this.usedFields = usedFields;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public PlanetProperties(int totalFields, int usedFields, int size, int minTemp, int maxTemp) {
        this.totalFields = totalFields;
        this.usedFields = usedFields;
        this.size = size;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    public PlanetProperties(){
    }

    public PlanetProperties parseProperties(){
        String tempSpan = UIMethods.getTextFromAttributeAndValue("id","temperatureContentField");//.split(" to ");
        Matcher r = Pattern.compile("[0-9-]+").matcher(tempSpan);
        if(r.find())
            minTemp = Integer.parseInt(r.group());
        if(r.find())
            maxTemp = Integer.parseInt(r.group());

        String[] e = UIMethods.getTextFromAttributeAndValue("id","diameterContentField").split("km");
        size = Integer.parseInt(e[0].replace(".","").trim());
        String[] el = e[1].replace("(", "").replace(")", "").split("\\/");
        usedFields = Integer.parseInt(el[0].trim());
        totalFields = Integer.parseInt(el[1].trim());
        return this;
    }
}
