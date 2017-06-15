package ogame.pages;

import bot.Bot;
import org.jsoup.nodes.Document;

/**
 * Created by jarndt on 5/30/17.
 */
public class RecruitOfficers implements OgamePage{
    public static final String RECRUIT_OFFICERS = "Recruit Officers";

    @Override
    public String getPageName() {
        return RECRUIT_OFFICERS;
    }
    @Override
    public String getXPathSelector() {
        return "//*[@id='menuTable']/li[11]/a/span";
    }

    @Override
    public String getCssSelector() {
        return "#menuTable > li:nth-child(11) > a > span";
    }
    @Override
    public String uniqueCssSelector() {
        return getCssSelector()+".selected";
    }

    @Override
    public void parsePage(Bot b, Document document) {
        //TODO
    }
}
