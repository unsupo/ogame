package ogame.objects;

/**
 * Created by jarndt on 5/10/17.
 */
public class PlanetProperties {
    private int totalFields, usedFields, size, minTemp, maxTemp;

    public PlanetProperties(int totalFields, int usedFields, int size, int minTemp, int maxTemp) {
        this.totalFields = totalFields;
        this.usedFields = usedFields;
        this.size = size;
        this.minTemp = minTemp;
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
}
