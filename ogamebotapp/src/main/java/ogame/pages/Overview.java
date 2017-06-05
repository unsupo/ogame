package ogame.pages;

import bot.Bot;
import ogame.objects.game.planet.PlanetProperties;
import org.jsoup.nodes.Document;

/**
 * Created by jarndt on 5/30/17.
 */
public class Overview implements OgamePage {
    public static final String OVERVIEW = "Overview";

    @Override
    public String getPageName() {
        return OVERVIEW;
    }

    @Override
    public String getXPathSelector() {
        return "//*[@id='menuTable']/li[1]/a/span";
    }

    @Override
    public String getCssSelector() {
        return "#menuTable > li:nth-child(1) > a > span";
    }

    @Override
    public String uniqueXPath() {
        return "//*[@id='detailWrapper']";
    }

    @Override
    public void parsePage(Bot b, Document d) {
        //TODO set currently being built: buildings, research, shipyard

        //TODO parse fleet movement

        //TODO get unread messages
    }
}
