# ogame

src.main.java.Runner is the main class
In there i have a few examples of how to run the program.

The following is an example of logging into ogame.  where universe is like this example: s129-en.ogame.gameforge.com
Initialize.login("<universe>","<username>","<password>")
Once you're logged in you can then click on a new page or link for instance
This will open the research page
new Overview().clickOnResearch();

This will research combustion drive with dark matter
new Research().clickOnCombustionDrive().clickOnStartWithDM();

This will look how long the current Building/Facility/Research will take and sleep that long
Thread.sleep(Utility.getInProgressTime());


The following is an example of how to get all the players and there planets from the ogniter database which are inactive

for(Map<String, Object> a : _HSQLDB.executeQuery("select * from player p JOIN planet t ON p.player_name = t.player_name where player_status in ('I','i')"))
    System.out.println(a);
_HSQLDB.getInstance().db.stopDBServer();
System.exit(0);

The following example query will get all inactive players who are in galaxy 5
        for(Map<String, Object> a : _HSQLDB.executeQuery(
                "select * from player p JOIN planet t ON p.player_name = t.player_name " +
                        "where player_status in ('I','i') and " +
                            "regexp_substring(coordinates,'[0-9]+')='5'"))
            System.out.println(a);


The following is an example of how to insert all data into the hsql database to be read with a query example like above.

OgniterGalaxyParser.parseEntireUniverse(398); //unierse 398 is quantum

