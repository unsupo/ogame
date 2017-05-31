package ogame.pages;

import bot.Bot;
import org.jsoup.nodes.Document;

/**
 * Created by jarndt on 5/30/17.
 */
public interface OgamePage {
    public String getPageName();
    public String getXPathSelector();
    public String getCssSelector();
    public String uniqueXPath();
    public void parsePage(Bot b, Document document);
}
