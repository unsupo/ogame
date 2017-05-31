package ogame.pages;

import bot.Bot;
import org.jsoup.nodes.Document;

/**
 * Created by jarndt on 5/30/17.
 */
public class Messages implements OgamePage {
    public static final String MESSAGES = "Messages";

    @Override
    public String getPageName() {
        return MESSAGES;
    }

    @Override
    public String getXPathSelector() {
        return "//*[@id='message-wrapper']/a[1]";
    }

    @Override
    public String getCssSelector() {
        return "#message-wrapper > a.selected.comm_menu.messages.tooltip.js_hideTipOnMobile";
    }

    @Override
    public String uniqueXPath() {
        return "//*[@id=\"buttonz\"]/div[2]/div[1]/ul";
    }

    @Override
    public void parsePage(Bot b, Document document) {
        //TODO message parsing
    }
}
