package ogame.pages;

import bot.Bot;
import org.jsoup.nodes.Document;

/**
 * Created by jarndt on 5/30/17.
 */
public class Fleet implements OgamePage{
    public static final String FLEET = "FleetInfo";

    @Override
    public String getPageName() {
        return FLEET;
    }
    @Override
    public String getXPathSelector() {
        return "//*[@id='menuTable']/li[8]/a/span";
    }

    @Override
    public String getCssSelector() {
        return "#menuTable > li:nth-child(8) > a > span";
    }

    @Override
    public String uniqueXPath() {
        return "//*[@id='inhalt']/div[2]";
    }

    @Override
    public void parsePage(Bot b, Document document) {
        //TODO
    }
}
