package ogame.pages;

/**
 * Created by jarndt on 9/26/16.
 */

import org.jsoup.Jsoup;
import utilities.selenium.UIMethods;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Static class to read messages
 */
public class Message {
    public static final String MESSAGES = "Messages";


    public static final String  FLEETS          = "Fleets",//"tabs-nfFleets",
                                COMMUNICATION   = "Communication",//"tabs-nfCommunication",
                                ECONOMY         = "Economy",//"tabs-nfEconomy",
                                UNIVERSE        = "Universe",//"tabs-nfUniverse",
                                OGAME           = "OGame",//"tabs-nfSystem",
                                FAVOURITES      = "Favourites",//"tabs-nfFavorites";

                                FLEETS_ESPIONAGE        = "Espionage",
                                FLEETS_COMBAT_REPORT    = "Combat Reports",
                                FLEETS_EXPEDITIONS      = "Expeditions",
                                FLEETS_UNIONS_TRANSPORT = "Unions/Transport",
                                FLEETS_OTHER            = "Other",

                                COMMUNICATION_MESSAGES               = "Messages",
                                COMMUNICATION_INFORMATION            = "Information",
                                COMMUNICATION_SHARED_COMBAT_REPORTS  = "Shared Combat Reports",
                                COMMUNICATION_ESPIONAGE_REPORTS      = "Shared Espionage Reports",
                                COMMUNICATION_EXPEDITIONS            = "Expeditions";


    private static HashMap<String, List<String>> tabSubTabMap = new HashMap<>();

    static{
        tabSubTabMap.put(FLEETS,
                Arrays.asList(FLEETS_ESPIONAGE,FLEETS_COMBAT_REPORT,FLEETS_EXPEDITIONS,FLEETS_UNIONS_TRANSPORT,FLEETS_OTHER));
        tabSubTabMap.put(COMMUNICATION,
                Arrays.asList(COMMUNICATION_MESSAGES,COMMUNICATION_INFORMATION,COMMUNICATION_SHARED_COMBAT_REPORTS
                        ,COMMUNICATION_ESPIONAGE_REPORTS,COMMUNICATION_EXPEDITIONS));
        tabSubTabMap.put(ECONOMY,Arrays.asList());
        tabSubTabMap.put(UNIVERSE,Arrays.asList());
        tabSubTabMap.put(OGAME,Arrays.asList());
        tabSubTabMap.put(FAVOURITES,Arrays.asList());
    }

    public Message(){   }
    public static Message instance;
    public static Message getInstance(){
        if(instance == null)
            instance = new Message();
        return instance;
    }

    public static boolean isInMessagePage(){
        return UIMethods.doesPageContainText("News feed");
    }
    public static int getMessageCount(){
        return Integer.parseInt(UIMethods.getTextFromAttributeAndValue("class","new_msg_count").trim());
    }
    public static void clickOnMessages() {
        UIMethods.clickOnAttributeAndValue("class","messages");
    }

    public static List<String> getTabsWithUnreadMessages(){
        return Jsoup.parse(UIMethods.getWebDriver().getPageSource())
                .select("li.list_item").stream().filter(e->!e.select("span.new_msg_count").isEmpty())
                .map(e->e.select("span.icon_caption").text().trim())
                .collect(Collectors.toList());
    }

    public static List<String> getSubTabsWithUnreadMessages(){
        return Jsoup.parse(UIMethods.getWebDriver().getPageSource()).select("li.list_item > a").stream()
                .filter(e->!e.select("span").isEmpty() && e.select("span").text().contains("("))
                .map(e->e.text().split(" ")[0].trim())
                .collect(Collectors.toList());
    }

    public static void parseAllMessages() {
        if(!isInMessagePage())
            clickOnMessages();

        parseFleetMessages();
        parseCommunicationMessages();
        parseEconomyMessages();
        parseUniverseMessages();
        parseOgameMessages();
        parseFavouritesMessages();
    }

    public static void clickOnMessageTab(String tabName){
        if(!isTabActive(tabName)) //click on tab if it's not active
            UIMethods.clickOnText(tabName);

    }public static void clickOnMessageSubTab(String subTabName){
        String parent = getParentTab(subTabName); //get parent tab
        clickOnMessageTab(parent); //click on parent tab if it's not open yet
        if(!isSubTabActive(subTabName)) //click on tab if it's not active
            UIMethods.clickOnText(subTabName);
    }

    public static String getParentTab(String subTabName) {
        return tabSubTabMap.entrySet().stream()
                .filter(a->a.getValue().contains(subTabName))
                .map(a->a.getKey()).collect(Collectors.toList()).get(0);
    }

    public static boolean isTabActive(String tabName) {
        return tabName.equals(getActiveTab());
    }public static boolean isSubTabActive(String tabName) {
        return tabName.equals(getActiveSubTab());
    }

    public static String getActiveTab(){
        return Jsoup.parse(UIMethods.getWebDriver().getPageSource()).select("ul.tabs_btn > li.ui-tabs-active").text().trim();
    }public static String getActiveSubTab(){
        String[] tabs = Jsoup.parse(UIMethods.getWebDriver().getPageSource()).select("li.ui-tabs-active").text().split(" ");
        return tabs.length > 1 ? tabs[1].trim() : tabs[0].trim();
    }

    public static void parseFavouritesMessages() {
        clickOnMessageTab(MESSAGES);
    }

    public static void parseOgameMessages() {
        clickOnMessageTab(OGAME);

    }

    public static void parseUniverseMessages() {
        clickOnMessageTab(UNIVERSE);

    }

    public static void parseEconomyMessages() {
        clickOnMessageTab(ECONOMY);

    }

    public static void parseCommunicationMessages() {
        clickOnMessageTab(COMMUNICATION);

    }

    public static void parseFleetMessages() {
        clickOnMessageTab(MESSAGES);
        parseEspionageMessage();
        parseCombatReportMessages();
        parseExpeditionsMessages();
        parseUnionTransportMessages();
        parseOtherMessages();
    }

    public static void parseOtherMessages() {
        clickOnMessageSubTab(FLEETS_OTHER);
    }

    public static void parseUnionTransportMessages() {
        clickOnMessageSubTab(FLEETS_UNIONS_TRANSPORT);

    }

    public static void parseExpeditionsMessages() {
        clickOnMessageSubTab(FLEETS_EXPEDITIONS);

    }

    public static void parseCombatReportMessages() {
        clickOnMessageSubTab(FLEETS_COMBAT_REPORT);

    }

    public static void parseEspionageMessage() {
        clickOnMessageSubTab(FLEETS_ESPIONAGE);

    }
}
