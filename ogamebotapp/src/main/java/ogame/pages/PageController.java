package ogame.pages;

import bot.Bot;
import com.google.gson.Gson;
import ogame.objects.game.Buildable;
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
import java.util.stream.Collectors;

/**
 * Created by jarndt on 5/30/17.
 */
public class PageController {
    Bot b;
    HashMap<String,OgamePage> ogamePages = new HashMap<>();

    public PageController(Bot b){this.b = b; init();}

    private void init(){
        ogamePages.put(Overview.OVERVIEW.toLowerCase(),new Overview());
        ogamePages.put(Resources.RESOURCES.toLowerCase(),new Resources());
        ogamePages.put(Facilities.FACILITIES.toLowerCase(),new Facilities());
        ogamePages.put(Merchant.MERCHANT.toLowerCase(),new Merchant());
        ogamePages.put(Research.RESEARCH.toLowerCase(),new Research());
        ogamePages.put(Shipyard.SHIPYARD.toLowerCase(),new Shipyard());
        ogamePages.put(Defense.DEFENSE.toLowerCase(),new Defense());
        ogamePages.put(Fleet.FLEET.toLowerCase(),new Fleet());
        ogamePages.put(Galaxy.GALAXY.toLowerCase(),new Galaxy());
        ogamePages.put(Alliance.ALLIANCE.toLowerCase(),new Alliance());
        ogamePages.put(RecruitOfficers.RECRUIT_OFFICERS.toLowerCase(),new RecruitOfficers());
        ogamePages.put(Shipyard.SHIPYARD.toLowerCase(),new Shipyard());
    }

    public boolean goToPage(String pageName) throws IOException {
        //TODO utilize tooltip on each planet (right side) to navigate to pages this allows direct navigation to different planets
        OgamePage page = ogamePages.get(pageName.toLowerCase());
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
            if(!b.getPlanets().containsKey(coords.getStringValue()))
                b.getPlanets().put(coords.getStringValue(),
                        new PlanetBuilder()
                                .setId(planet.id())
                                .setClassName(planet.className())
                                .setPlanetImageURL(planet.select("img").attr("src"))
                                .setLink(planet.select("a").attr("href"))
                                .setCoordinates(coords)
                                .setPlanetName(planetName)
                                .setPlanetSize(parsePlanetProperties(planet,coords))
                                .build()
                );
            else{
                Planet p = b.getPlanets().get(coords.getStringValue());
                p.setId(planet.id());
                p.setId(planet.className());
                p.setPlanetImageURL(planet.select("img").attr("src"));
                p.setCoordinates(coords);
                p.setPlanetName(planetName);
                p.setPlanetSize(parsePlanetProperties(planet,coords));
            }
        }

        if(b.getPlanets().size() == 1)
            b.setCurrentPlanetCoordinates(new Coordinates(new ArrayList<>(b.getPlanets().keySet()).get(0),b.getLogin().getUser().getUniverse()));
        else {
            System.out.println("Not implemented yet");
            System.exit(-10);
        }
        Planet currentPlanet = b.getCurrentPlanet();

        //parse resources
        List<Long> values = new ArrayList<>();
        String[] storage = new String[3];
        int i = 0;
        for(String v : Arrays.asList("metal","crystal","deuterium","energy","darkmatter")) {
            Elements vv = d.select("#resources_" + v);
            values.add(Long.parseLong(vv.text().trim().replace(".", "")));
            if(i<3)
                storage[i] = vv.attr("class").trim();

            i++;
        }
        currentPlanet.setMetalStorageString(storage[0]);
        currentPlanet.setCrystalStorageString(storage[1]);
        currentPlanet.setDueteriumStorageString(storage[2]);

        currentPlanet.setResources(new Resource(values.get(0),values.get(1),values.get(2),values.get(3)));

        resourcesFromToolTip(b,d);

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

        try{
            boolean validated = d.select("#advice-bar > a").attr("title").trim().isEmpty();
            b.getLogin().getUser().setVerified(validated);
        }catch (Exception e){/*DO NOTHING*/}
    }

    private PlanetProperties parsePlanetProperties(Element planet, Coordinates planetName) {
        List<String> v = Arrays.asList(planet.select("a").attr("title")
                .replaceAll("<a.*", "")
                .split("<\\/?br?\\/?>")
        ).stream().filter(a -> a != null && !a.isEmpty()).collect(Collectors.toList());

        PlanetProperties planetProperties = new PlanetProperties();
        boolean parse = false;
        int j = 0;
        for (int i = 0; i < v.size(); i++) {
            if(v.get(i).contains(planetName.getStringValue())) {
                parse = true;
                continue;
            }if(!parse) continue;
            if(j>1) return planetProperties;
            if(j == 0){
                String[] split = v.get(i).split("km \\(");
                planetProperties.setSize(Integer.parseInt(split[0].replace(".","")));
                String[] sizeSplit = split[1].replace(")","").split("/");
                planetProperties.setTotalFields(Integer.parseInt(sizeSplit[1]));
                planetProperties.setUsedFields(Integer.parseInt(sizeSplit[0]));
            }else if(j == 1){
                String[] split = v.get(i).replace("°C","").split(" to ");
                planetProperties.setMaxTemp(Integer.parseInt(split[1]));
                planetProperties.setMinTemp(Integer.parseInt(split[0]));
            }
            j++;
        }

        return planetProperties;
    }

    private void resourcesFromToolTip(Bot b, Document d) {
        List<Element> v = d.getElementsByTag("script").stream()
                .filter(a -> !a.hasAttr("src") && a.toString().contains("resourceTooltip"))
                .collect(Collectors.toList());
        List<String> functions = new ArrayList<>(Arrays.asList(v.get(0).toString().split("function")))
                .stream().filter(a->a.contains("resourceTooltip")).collect(Collectors.toList());

        List<String> resources = new ArrayList<>(Arrays.asList(functions.get(0).split("[{|}]")))
                .stream().filter(a -> a.contains("actualFormat")).collect(Collectors.toList());

        List<String> order = Arrays.asList("metal","crystal","deuterium","energy","darkmatter");
        for (int i = 0; i < resources.size(); i++){
//            System.out.print(order.get(i)+": ");
            String[] res = resources.get(i).split(":");
            String actual = res[2].split(",")[0];
            String max = "", production = "";
            if(res.length > 3) {
                max = res[3].split(",")[0];
                production = res[4];
            }
            Planet p = b.getCurrentPlanet();
            if(order.get(i).equals("metal")){
                p.setMetalStorage(Long.parseLong(max));
                p.getResources(true).setMetal(Long.parseLong(actual));
                p.setMetalProduction(Double.parseDouble(production));
            }
            if(order.get(i).equals("crystal")){
                p.setCrystalStorage(Long.parseLong(max));
                p.getResources(true).setCrystal(Long.parseLong(actual));
                p.setCrystalProduction(Double.parseDouble(production));
            }
            if(order.get(i).equals("deuterium")){
                p.setDueteriumStorage(Long.parseLong(max));
                p.getResources(true).setDeuterium(Long.parseLong(actual));
                p.setDueteriumProduction(Double.parseDouble(production));
            }
        }
    }

    private void writeBotDataToFile() throws IOException {
        FileOptions.writeToFileOverWrite(
                b.getBotDataDirectory()+"/"+b.getName()+"_data.json",
                new Gson().toJson(b)
        );
    }

    public OgamePage getPage(String pageName) {
        return ogamePages.get(pageName.toLowerCase());
    }

    public void goToPageOnPlanet(Coordinates planetCoordinates, String page) {
        //TODO learn how to switch between planets
        //pageName.toLowerCase()
    }

    public static void parseGenericBuildings(Document document, Bot b){
        Elements v = document.select("#buttonz > div.content").select("div.buildingimg");
        Planet p = b.getCurrentPlanet();
        for(Element e : v) {
            String name = e.select("a > span > span > span").text().trim();
            Integer level =Integer.parseInt(e.select("span.level").get(0).ownText().trim());
            Buildable bb = Buildable.getBuildableByName(name).setCurrentLevel(level);
            bb.setCssSelector(e.cssSelector());
            bb.setQuickBuildLink(e.select("a").attr("onclick").trim());
            bb.setRef(e.select("a").attr("ref").trim());
            p.addBuildable(bb);
        }
    }
}
