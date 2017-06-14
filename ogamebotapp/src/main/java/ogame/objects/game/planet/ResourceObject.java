package ogame.objects.game.planet;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;

/**
 * Created by jarndt on 6/14/17.
 */
public class ResourceObject {
    public static final String DARK_MATTER = "darkmatter", ENERGY = "energy", DEUETERIUM = "deuterium", CRYSTAL = "crystal", METAL = "metal";

    private String resourceName, availableClassName, storageCapacityClassName, currentProductionClassName,denCapacityClassName,
            purchasedClassName, foundClassName, consumptionClassName, className;
    private long storageCapacity, denCapacity, available, purchased, found, consumption, max, actual;
    private double currentProduction, production;

    public ResourceObject(String resourceName, JSONObject jo){
        this.resourceName = resourceName;
        className = jo.getString("class");

        JSONObject ressies = jo.getJSONObject("resources");
        actual = ressies.getLong("actual");
        if(ressies.has("max"))
            max = ressies.getLong("max");
        if(ressies.has("production"))
            production = ressies.getDouble("production");

        Elements tooltip = Jsoup.parse(jo.getString("tooltip")).select("tr");

        for(Element e : tooltip)
            if(e.select("th").text().contains("Available")){
                available = Long.parseLong(e.select("span").text().trim().replace(".",""));
                availableClassName = e.select("span").attr("class");
            }else if(e.select("th").text().contains("Storage capacity")){
                storageCapacity = Long.parseLong(e.select("span").text().trim().replace(".",""));
                storageCapacityClassName = e.select("span").attr("class");
            }else if(e.select("th").text().contains("Current production")){
                currentProduction = Long.parseLong(
                        e.select("span").text().trim().replace(".","").replace("+","")
                )/3600.;
                currentProductionClassName = e.select("span").attr("class");
            }else if(e.select("th").text().contains("Den Capacity")){
                denCapacity = Long.parseLong(e.select("span").text().trim().replace(".",""));
                denCapacityClassName = e.select("span").attr("class");
            }else if(e.select("th").text().contains("Consumption")){
                consumption = Long.parseLong(e.select("span").text().trim().replace(".",""));
                consumptionClassName = e.select("span").attr("class");
            }else if(e.select("th").text().contains("Purchased")){
                purchased = Long.parseLong(e.select("span").text().trim().replace(".",""));
                purchasedClassName = e.select("span").attr("class");
            }else if(e.select("th").text().contains("Found")){
                found = Long.parseLong(e.select("span").text().trim().replace(".",""));
                foundClassName = e.select("span").attr("class");
            }
    }

    public ResourceObject(String resourceName, String availableClassName, String storageCapacityClassName, String currentProductionClassName, String denCapacityClassName, String className, long storageCapacity, long denCapacity, long available, long max, long actual, double currentProduction, double production) {
        this.availableClassName = availableClassName;
        this.resourceName = resourceName;
        this.storageCapacityClassName = storageCapacityClassName;
        this.currentProductionClassName = currentProductionClassName;
        this.denCapacityClassName = denCapacityClassName;
        this.className = className;
        this.storageCapacity = storageCapacity;
        this.denCapacity = denCapacity;
        this.available = available;
        this.max = max;
        this.actual = actual;
        this.currentProduction = currentProduction;
        this.production = production;
    }

    public ResourceObject(String resourceName, String availableClassName, String purchasedClassName, String foundClassName, String className, long actual) {
        this.resourceName = resourceName;
        this.availableClassName = availableClassName;
        this.purchasedClassName = purchasedClassName;
        this.foundClassName = foundClassName;
        this.className = className;
        this.actual = actual;
    }

    public ResourceObject(String resourceName, String availableClassName, String currentProductionClassName, String consumptionClassName, String className, long available, long consumption, long actual, double currentProduction) {
        this.resourceName = resourceName;
        this.availableClassName = availableClassName;
        this.currentProductionClassName = currentProductionClassName;
        this.consumptionClassName = consumptionClassName;
        this.className = className;
        this.available = available;
        this.consumption = consumption;
        this.actual = actual;
        this.currentProduction = currentProduction;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getAvailableClassName() {
        return availableClassName;
    }

    public void setAvailableClassName(String availableClassName) {
        this.availableClassName = availableClassName;
    }

    public String getStorageCapacityClassName() {
        return storageCapacityClassName;
    }

    public void setStorageCapacityClassName(String storageCapacityClassName) {
        this.storageCapacityClassName = storageCapacityClassName;
    }

    public String getCurrentProductionClassName() {
        return currentProductionClassName;
    }

    public void setCurrentProductionClassName(String currentProductionClassName) {
        this.currentProductionClassName = currentProductionClassName;
    }

    public String getDenCapacityClassName() {
        return denCapacityClassName;
    }

    public void setDenCapacityClassName(String denCapacityClassName) {
        this.denCapacityClassName = denCapacityClassName;
    }

    public String getPurchasedClassName() {
        return purchasedClassName;
    }

    public void setPurchasedClassName(String purchasedClassName) {
        this.purchasedClassName = purchasedClassName;
    }

    public String getFoundClassName() {
        return foundClassName;
    }

    public void setFoundClassName(String foundClassName) {
        this.foundClassName = foundClassName;
    }

    public String getConsumptionClassName() {
        return consumptionClassName;
    }

    public void setConsumptionClassName(String consumptionClassName) {
        this.consumptionClassName = consumptionClassName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public long getStorageCapacity() {
        return storageCapacity;
    }

    public void setStorageCapacity(long storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public long getDenCapacity() {
        return denCapacity;
    }

    public void setDenCapacity(long denCapacity) {
        this.denCapacity = denCapacity;
    }

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public long getPurchased() {
        return purchased;
    }

    public void setPurchased(long purchased) {
        this.purchased = purchased;
    }

    public long getFound() {
        return found;
    }

    public void setFound(long found) {
        this.found = found;
    }

    public long getConsumption() {
        return consumption;
    }

    public void setConsumption(long consumption) {
        this.consumption = consumption;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getActual() {
        return actual;
    }

    public void setActual(long actual) {
        this.actual = actual;
    }

    public double getCurrentProduction() {
        return currentProduction;
    }

    public void setCurrentProduction(double currentProduction) {
        this.currentProduction = currentProduction;
    }

    public double getProduction() {
        return production;
    }

    public void setProduction(double production) {
        this.production = production;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceObject that = (ResourceObject) o;

        if (storageCapacity != that.storageCapacity) return false;
        if (denCapacity != that.denCapacity) return false;
        if (available != that.available) return false;
        if (purchased != that.purchased) return false;
        if (found != that.found) return false;
        if (consumption != that.consumption) return false;
        if (max != that.max) return false;
        if (actual != that.actual) return false;
        if (Double.compare(that.currentProduction, currentProduction) != 0) return false;
        if (Double.compare(that.production, production) != 0) return false;
        if (resourceName != null ? !resourceName.equals(that.resourceName) : that.resourceName != null) return false;
        if (availableClassName != null ? !availableClassName.equals(that.availableClassName) : that.availableClassName != null)
            return false;
        if (storageCapacityClassName != null ? !storageCapacityClassName.equals(that.storageCapacityClassName) : that.storageCapacityClassName != null)
            return false;
        if (currentProductionClassName != null ? !currentProductionClassName.equals(that.currentProductionClassName) : that.currentProductionClassName != null)
            return false;
        if (denCapacityClassName != null ? !denCapacityClassName.equals(that.denCapacityClassName) : that.denCapacityClassName != null)
            return false;
        if (purchasedClassName != null ? !purchasedClassName.equals(that.purchasedClassName) : that.purchasedClassName != null)
            return false;
        if (foundClassName != null ? !foundClassName.equals(that.foundClassName) : that.foundClassName != null)
            return false;
        if (consumptionClassName != null ? !consumptionClassName.equals(that.consumptionClassName) : that.consumptionClassName != null)
            return false;
        return className != null ? className.equals(that.className) : that.className == null;
    }

    @Override
    public int hashCode() {
        int result;
        result = resourceName != null ? resourceName.hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return "ResourceObject{" +
                "resourceName='" + resourceName + '\'' +
                ", availableClassName='" + availableClassName + '\'' +
                ", storageCapacityClassName='" + storageCapacityClassName + '\'' +
                ", currentProductionClassName='" + currentProductionClassName + '\'' +
                ", denCapacityClassName='" + denCapacityClassName + '\'' +
                ", purchasedClassName='" + purchasedClassName + '\'' +
                ", foundClassName='" + foundClassName + '\'' +
                ", consumptionClassName='" + consumptionClassName + '\'' +
                ", className='" + className + '\'' +
                ", storageCapacity=" + storageCapacity +
                ", denCapacity=" + denCapacity +
                ", available=" + available +
                ", purchased=" + purchased +
                ", found=" + found +
                ", consumption=" + consumption +
                ", max=" + max +
                ", actual=" + actual +
                ", currentProduction=" + currentProduction +
                ", production=" + production +
                '}';
    }
}
