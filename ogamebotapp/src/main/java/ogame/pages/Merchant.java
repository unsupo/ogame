package ogame.pages;

import bot.Bot;
import org.jsoup.nodes.Document;

/**
 * Created by jarndt on 5/30/17.
 */
public class Merchant implements OgamePage{
    public static final String MERCHANT = "Merchant";

    @Override
    public String getPageName() {
        return MERCHANT;
    }

    @Override
    public String getXPathSelector() {
        return "//*[@id='menuTable']/li[4]/a/span";
    }

    @Override
    public String getCssSelector() {
        return "#menuTable > li:nth-child(4) > a > span";
    }

    @Override
    public String uniqueCssSelector() {
        return getCssSelector()+".selected";
    }

    @Override
    public void parsePage(Bot b, Document document) {
        //NOTHING TO PARSE
    }
}
