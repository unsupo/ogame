package objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utilities.selenium.UIMethods;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jarndt on 9/25/16.
 */
public class PlanetProperties {
    private int totalFields, remainingFields, size, minTemp, maxTemp;

    @Override
    public String toString() {
        return "PlanetProperties{" +
                "totalFields=" + totalFields +
                ", remainingFields=" + remainingFields +
                ", size=" + size +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanetProperties that = (PlanetProperties) o;

        if (totalFields != that.totalFields) return false;
        if (remainingFields != that.remainingFields) return false;
        return size == that.size;

    }

    @Override
    public int hashCode() {
        int result = totalFields;
        result = 31 * result + remainingFields;
        result = 31 * result + size;
        return result;
    }

    public int getTotalFields() {

        return totalFields;
    }

    public void setTotalFields(int totalFields) {
        this.totalFields = totalFields;
    }

    public int getRemainingFields() {
        return remainingFields;
    }

    public void setRemainingFields(int remainingFields) {
        this.remainingFields = remainingFields;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public PlanetProperties(){
        Element details = Jsoup.parse(UIMethods.getWebDriver().getPageSource()).select("#planetDetails").get(0);
        String tempSpan = details.select("#temperatureContentField").get(0).text().trim();//.split(" to ");
        Matcher r = Pattern.compile("[0-9-]+").matcher(tempSpan);
        minTemp = Integer.parseInt(r.group(0));
        maxTemp = Integer.parseInt(r.group(1));

        Element e = details.select("#diameterContentField").get(0);
        size = Integer.parseInt(e.text().split("km")[0].trim());
        Elements el  =  e.select("span");
        totalFields = Integer.parseInt(el.get(0).text().trim());
        totalFields = Integer.parseInt(el.get(1).text().trim());
    }
}
