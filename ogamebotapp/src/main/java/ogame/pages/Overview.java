package ogame.pages;

import bot.Bot;
import ogame.objects.game.BuildTask;
import ogame.objects.game.planet.Planet;
import ogame.objects.game.planet.PlanetProperties;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import utilities.Timer;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
        //TODO set currently being built: shipyard
        List<String> v = Arrays.asList(
                "#overviewBottom > div:nth-child(1) > div.content > table",
                "#overviewBottom > div:nth-child(2) > div.content > table",
                "#overviewBottom > div:nth-child(3) > div.content > table"
        );
        Planet p = b.getCurrentPlanet();
        for (int i = 0; i < v.size(); i++) {
            Elements activeConstruction = d.select(v.get(i));
            boolean works = activeConstruction.select("tr").size() == 5;
            if (works) {
                BuildTask buildTask = new BuildTask();
                String name = activeConstruction.select("tr > th").text().trim();
                buildTask.setBuildable(p.getBuildable(name));

                int level = Integer.parseInt(activeConstruction.select("span.level").text().replace("Level ", "").trim());
                buildTask.setCountOrLevel(level);

                long time = Bot.parseTime(activeConstruction.select("td.desc.timer > span").text().trim());
                buildTask.setCompleteTime(LocalDateTime.now().plusSeconds(time));

                if(i == 1) {
                    b.setCurrentResearchBeingBuilt(buildTask);
                    b.getResearch().put(name,level);
                }else if(i == 0) {
                    p.setCurrentBuildingBeingBuild(buildTask);
                    p.addBuildable(buildTask.getBuildable());
                }
                //TODO shipyard
            }else{
                if(i == 0)
                    p.setCurrentBuildingBeingBuild(null);
                else if(i == 1)
                    b.setCurrentResearchBeingBuilt(null);
                //TODO shipyard
            }
        }
        try {
            String rank = d.select("#scoreContentField > a").text().trim();
            int points = Integer.parseInt(rank.replaceAll(" \\(.*\\)", ""));
            String[] split = rank.replaceAll(".*\\(Place ", "")
                    .replace(".", "")
                    .replace(")", "")
                    .split(" of ");
            int ownRank = Integer.parseInt(split[0].trim()),
                    totalRanks = Integer.parseInt(split[1]);
            b.setRank(ownRank);
            b.setTotalRanks(totalRanks);
            b.setPoints(points);
        }catch (Exception e){/*DO NOTHING*/}
        try {
            int honorPoints = Integer.parseInt(d.select("#honorContentField").text().trim());
            b.setHonorPoints(honorPoints);
        }catch (Exception e){/*DO NOTHING*/}
    }
}
