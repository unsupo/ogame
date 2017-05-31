package ogame.pages;

import bot.Bot;
import org.jsoup.nodes.Document;

/**
 * Created by jarndt on 5/30/17.
 */
public class Shop implements OgamePage{
    public static final String SHOP = "Shop";

    @Override
    public String getPageName() {
        return SHOP;
    }
    @Override
    public String getXPathSelector() {
        return "//*[@id='menuTable']/li[12]/a/span";
    }

    @Override
    public String getCssSelector() {
        return "#menuTable > li:nth-child(12) > a > span";
    }
    @Override
    public String uniqueXPath() {
        return "//*[@id='buttonz']/div[2]/button[1]";
    }

    @Override
    public void parsePage(Bot b, Document document) {
        //TODO
    }
}
