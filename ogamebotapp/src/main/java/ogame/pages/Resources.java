package ogame.pages;

import bot.Bot;
import ogame.objects.game.Buildable;
import ogame.objects.game.Resource;
import ogame.objects.game.planet.Planet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by jarndt on 5/8/17.
 */
public class Resources implements OgamePage{
    public static final String ID = "ref";
    public static final String RESOURCES = "Resources";
    public static final String
            METAL_MINE              = "Metal Mine",
            CRYSTAL_MINE            = "Crystal Mine",
            DUETERIUM_SYNTHESIZER   = "Deuterium Synthesizer",
            SOLAR_PLANET            = "Solar Plant",
            FUSION_REACTOR          = "Fusion Reactor",
            METAL_STORAGE           = "Metal Storage",
            CRYSTAL_STORAGE         = "Crystal Storage",
            DUETERIUM_TANK          = "Dueterium Tank";

    public static String[] names = {
            METAL_MINE, CRYSTAL_MINE, DUETERIUM_SYNTHESIZER,
            SOLAR_PLANET, FUSION_REACTOR,
            METAL_STORAGE, CRYSTAL_STORAGE,DUETERIUM_TANK
    };

    static {
        baseCosts = Resource.convertCosts(new long[] {
                60,15,0,10,
                48,24,0,10,
                225,75,0,20,
                75, 30, 0,0,
                900, 360, 180,0,
                1000, 0, 0,0,
                1000,500, 0,0,
                1000, 1000, 0,0
        });
    }

    public static Resource[] baseCosts;

    @Override
    public String getPageName() {
        return RESOURCES;
    }

    @Override
    public String getXPathSelector() {
        return "//*[@id='menuTable']/li[2]/a/span";
    }

    @Override
    public String getCssSelector() {
        return "#menuTable > li:nth-child(2) > a > span";
    }

    @Override
    public String uniqueXPath() {
        return "//*[@id='slot01']/a";
    }

    @Override
    public void parsePage(Bot b, Document document) {
        Elements v = document.select("#buttonz > div.content").select("div.buildingimg");
        Planet p = b.getCurrentPlanet();
        for(Element e : v) {
            String name = e.select("a > span > span > span").text().trim();
            Integer level =Integer.parseInt(e.select("span.level").get(0).ownText().trim());
            Buildable bb = Buildable.getBuildableByName(name).setCurrentLevel(level);
            p.addBuildable(bb);
        }

        //TODO Buildings being built

        //TODO parse other planet's resources
    }
}
