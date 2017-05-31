package ogame.pages;

import bot.Bot;
import com.google.gson.Gson;
import ogame.objects.game.Coordinates;
import ogame.objects.game.Resource;
import ogame.objects.game.planet.Planet;
import ogame.objects.game.planet.PlanetBuilder;
import ogame.objects.game.planet.PlanetProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import utilities.fileio.FileOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by jarndt on 5/30/17.
 */
public class PageController {
    Bot b;
    HashMap<String,OgamePage> ogamePages = new HashMap<>();

    public PageController(Bot b){this.b = b; init();}

    private void init(){
        ogamePages.put(Overview.OVERVIEW,new Overview());
        ogamePages.put(Resources.RESOURCES,new Resources());
        ogamePages.put(Facilities.FACILITIES,new Facilities());
        ogamePages.put(Merchant.MERCHANT,new Merchant());
        ogamePages.put(Research.RESEARCH,new Research());
        ogamePages.put(Shipyard.SHIPYARD,new Shipyard());
        ogamePages.put(Defense.DEFENSE,new Defense());
        ogamePages.put(Fleet.FLEET,new Fleet());
        ogamePages.put(Galaxy.GALAXY,new Galaxy());
        ogamePages.put(Alliance.ALLIANCE,new Alliance());
        ogamePages.put(RecruitOfficers.RECRUIT_OFFICERS,new RecruitOfficers());
        ogamePages.put(Shipyard.SHIPYARD,new Shipyard());
    }

    public boolean goToPage(String pageName) throws IOException {
        //TODO utilize tooltip on each planet (right side) to navigate to pages this allows direct navigation to different planets
        OgamePage page = ogamePages.get(pageName);
        b.getDriverController().clickWait(By.xpath(page.getXPathSelector()),1L,TimeUnit.MINUTES);
        boolean r = b.getDriverController().waitForElement(By.xpath(page.uniqueXPath()), 1L, TimeUnit.MINUTES);
        parsePage(page);
        return r;
    }
    private void parsePage(OgamePage page) throws IOException {
        b.getDriverController().waitForElement(By.xpath("//*[@id='metal_box']/div"),1L, TimeUnit.MINUTES);
        Document d = Jsoup.parse(b.getDriverController().getDriver().getPageSource());
        FileOptions.runConcurrentProcess((Callable)()->{
            parseAllPageContent(d);

            page.parsePage(b,d);

            FileOptions.runConcurrentProcessNonBlocking((Callable)()->{writeBotDataToFile(); return null;});

            return null;
        });
    }

    private void parseAllPageContent(Document d) throws IOException {
        for(Element planet : d.select("#planetList > div")){
            Coordinates coords = new Coordinates(planet.select("span.planet-koords").text())
                    .setUniverse(b.getLogin().getUser().getUniverse());
            String planetName = planet.select("span.planet-name").text().trim();
            b.getPlanets().put(coords,
                    new PlanetBuilder()
                            .setId(planet.id())
                            .setClassName(planet.className())
                            .setPlanetImageURL(planet.select("img").attr("src"))
                            .setLink(planet.select("a").attr("href"))
                            .setCoordinates(coords)
                            .setPlanetName(planetName)
                            .build()
            );
        }

        if(b.getPlanets().size() == 1)
            b.setCurrentPlanetCoordinates(new ArrayList<>(b.getPlanets().keySet()).get(0));
        else {
            System.out.println("Not implemented yet");
            System.exit(-10);
        }
        Planet currentPlanet = b.getCurrentPlanet();

        //parse resources
        List<Long> values = new ArrayList<>();
        for(String v : Arrays.asList("metal","crystal","deuterium","energy","darkmatter"))
            values.add(Long.parseLong(d.select("#resources_"+v).text().trim().replace(".","")));
        currentPlanet.setResources(new Resource(values.get(0),values.get(1),values.get(2),values.get(3)));
        b.setDarkMatter(values.get(4));

        //parse message count
        Elements v = d.select("#message-wrapper > a.comm_menu.messages.tooltip.js_hideTipOnMobile > span");
        int message = 0;
        if(v!=null && v.text()!=null && !v.text().isEmpty())
            try{ message = Integer.parseInt(v.text().trim());}
            catch (Exception e){ /*DO NOTHING*/}
        b.setUnreadMessages(message);

        //parse current page name
        b.setCurrentPage(d.select("#menuTable > li > a.selected").select("span").text().trim());
    }

    private void writeBotDataToFile() throws IOException {
        FileOptions.writeToFileOverWrite(
                b.getBotDataDirectory()+"/"+b.getName()+"_data.json",
                new Gson().toJson(this)
        );
    }
}
