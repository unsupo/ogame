package utilities.data;

/**
 * Created by jarndt on 5/8/17.
 */
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import utilities.database.HSQLDBCommons;
import utilities.fileio.FileOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 9/27/16.
 */
public class XMLAPIDownloader {
    public static void main(String[] args) throws IOException, SQLException {
        FileOptions.runConcurrentProcess(Arrays.asList(universeNumbers).stream().map(a->(Callable)()->{
            downloadAllFiles(a+"");
            return null;
        }).collect(Collectors.toList()));

//        _HSQLDB.executeQuery("DROP TABLE alliances;");
//        parseAllFiles();
//        parseFiles(141);
        //"select * from server where number = 117" //server details
        //"select * from players where universe_id = 117 and status in ('I','i')" //inactive players
//        for(Map<String, Object> v : _HSQLDB.executeQuery("select * from highscore_player a join players p on p.universe_id = a.universe_id and p.id = a.id join planets t on t.universe_id = a.universe_id and t.player = p.id where universe_id = 117 and status in ('I','i') and score < 10000 order by position"))
//            System.out.println(v);
//        for(Map<String, Object> v : _HSQLDB.executeQuery("select * from planets"))
//            System.out.println(v);
//        System.out.println(_HSQLDB.executeQuery("INSERT INTO PLANETS(id,player,name,coords,moon_id,moon_name,moon_size,universe_id) " +
//                "VALUES ('1620749','thanks urza (Moon)','8246','1126672','103949','Void','1:208:15',1);"));
//        for(Coordinates c : Utility.getAllInactiveTargets(new Coordinates(5,414,12),117))
//            System.out.println(c);

        System.exit(0);
    }

    final static public String regReplace = "{UNI}";
    public static final String
            SERVER_DATA_FILE        = "server",
            UNIVERSE_FILE           = "planets",
            PLAYERS_FILE            = "players",
            ALLIANCES_FILE          = "alliances",
            HIGHSCORE_PLAYER_FILE   = "highscore_player",
            HIGHSCORE_ALLIANCE_FILE = "highscore_alliance";

    final static public String
            SERVER_DATA          = "https://"+regReplace+"/api/serverData.xml",
            UNIVERSE             = "https://"+regReplace+"/api/universe.xml",
            PLAYERS              = "https://"+regReplace+"/api/players.xml",
            ALLIANCES            = "https://"+regReplace+"/api/alliances.xml",
            HIGHSCORE_PLAYER     = "https://"+regReplace+"/api/highscore.xml?category=1&type=1",
            HIGHSCORE_ALLIANCE   = "https://"+regReplace+"/api/highscore.xml?category=2&type=1";


    final public static HashMap<String,String> FILE_DATA = new HashMap<>();
    public static String DIR = FileOptions.cleanFilePath(FileOptions.RESOURCE_DIR + "ogame/xml_downloads/");

    static{
        FILE_DATA.put(SERVER_DATA_FILE,SERVER_DATA);
        FILE_DATA.put(UNIVERSE_FILE,UNIVERSE);
        FILE_DATA.put(PLAYERS_FILE,PLAYERS);
        FILE_DATA.put(ALLIANCES_FILE,ALLIANCES);
        FILE_DATA.put(HIGHSCORE_PLAYER_FILE,HIGHSCORE_PLAYER);
        FILE_DATA.put(HIGHSCORE_ALLIANCE_FILE,HIGHSCORE_ALLIANCE);
    }
    public static boolean readLastUpdate(String dir, String file){
        new File(dir).mkdirs();
        try {
            String lastUpdate = FileOptions.readFileIntoString(dir+file).trim();
            LocalDateTime dateTime = LocalDateTime.from(FileOptions.FORMAT.parse(lastUpdate));

            if(dateTime.plusDays(1).isAfter(LocalDateTime.now()))
                return true;

        } catch (IOException e) {        }
        return false;
    }

    public static void downloadAllFiles(final String universeNumber){
        final String dir = DIR+universeNumber;
        new File(dir).mkdirs();
        FileOptions.runConcurrentProcess(FILE_DATA.entrySet().stream().map(a->(Callable)()->{
            try {
                URL website = new URL(a.getValue().replace(regReplace, "s"+universeNumber+"-en.ogame.gameforge.com"));
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream(dir+"/"+a.getKey()+"_"+universeNumber+".xml");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList()));
        writeDateToFile(dir,universeNumber);
    }

    private static void writeDateToFile(String dir, String universeNumber) {
        try {
            FileOptions.writeToFileOverWrite(dir+"/last_update_"+universeNumber,LocalDateTime.now().format(FileOptions.FORMAT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void parseAllFiles() {
        Arrays.asList(universeNumbers).stream().forEach(a->{
            final String dir = DIR+a;
            if(!readLastUpdate(dir,"/last_update_"+a)) {
                downloadAllFiles(a + "");
                parseFileIntoDatabase(a + "");
            }
            writeDateToFile(dir, a+"");
        });
    }public static void parseFiles(int universeNumber) {
        if(!Arrays.asList(universeNumber).contains(universeNumber))
            throw new IllegalArgumentException(universeNumber+" is not a valid universeNumber");

        final String dir = DIR+universeNumber;
        new File(dir).mkdirs();
        if(!readLastUpdate(dir,"/last_update_"+universeNumber)) {
            downloadAllFiles(universeNumber + "");
            parseFileIntoDatabase(universeNumber + "");
        }

        writeDateToFile(dir, universeNumber+"");
    }
    public static Integer[] universeNumbers = {
            1,
            117, //QUANTUM
            118,
            120,
            122,
            123,
            128,//BETELGEUSE
            129,//CYGNUS
            130,//DEIMOS
            131,//ERIDANUS
            132,//FIDIS
            133,//GANIMED
            134,
            135,
            136,
            137,
            138,
            139,
            140,
            141
    };
    //    private static List<String> unique = Arrays.asList(UNIVERSE,); //for debugging
    public static void parseFileIntoDatabase(final String universeNumber) {
        FileOptions.runConcurrentProcess(FILE_DATA.entrySet().stream().map(a->(Callable)()->{
            try {
                new XMLFileParser(a,universeNumber).parse();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList()));

//        FILE_DATA.entrySet().stream().filter(a->!a.getKey().equals(ALLIANCES_FILE)).forEach(a-> { //TODO Alliance file
//            try {
//                new XMLFileParser(a,universeNumber).parse();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
    }

    private static class XMLFileParser{
        Element xmlFile;
        String type, universeNumber, name;

        public XMLFileParser(Map.Entry<String, String> type,String universeNumber){
            SAXBuilder builder = new SAXBuilder();
            try {
                xmlFile = builder.build(DIR+type.getKey()+"_"+universeNumber+".xml").getRootElement();
                this.type = type.getValue();
                this.universeNumber = universeNumber;
                this.name = type.getKey();
            } catch (JDOMException | IOException e) {
                e.printStackTrace();
            }
        }

        public void parse() throws IOException, SQLException {
            switch (type){
                case SERVER_DATA:
                    parseUniverseFiles(universeNumber); break;
                case HIGHSCORE_PLAYER:
                case HIGHSCORE_ALLIANCE:
                case PLAYERS:
                case UNIVERSE:
                case ALLIANCES:
                    parseAttributeFiles(universeNumber); break;
            }
        }

        private void parseAttributeFiles(String universeNumber) throws IOException, SQLException {
            String regReplaceNames = "{NAMES}", regReplaceValues = "{VALUES}";

            xmlFile.getChildren().parallelStream().forEach( e-> {
                String query = "INSERT INTO "+name+"(" + regReplaceNames + ") VALUES (" + regReplaceValues + ");";

                String names = "", values = "", updateQuery;
                updateQuery = "UPDATE "+name+" SET ";
                String extra = "", id = null;
                if(e.getChildren().size() != 0)
                    for(Element el : e.getChildren())
                        for(Attribute a : el.getAttributes()) {
                            names += "," + el.getName()+"_"+a.getName();
                            values += "," + "'" + a.getValue() + "'";

                            extra += ", "+el.getName()+"_"+a.getName()+" = '"+a.getValue()+"'";
                        }

                for(Attribute a : e.getAttributes()) {
                    names += "," + a.getName();
                    values += "," + "'" + a.getValue() + "'";

                    extra += ", "+a.getName()+" = '"+a.getValue()+"'";
                    if(a.getName().equals("id"))
                        id =a.getValue();
                }
                names+=",universe_id";
                values+=","+universeNumber;

                names = names.substring(1);
                values = values.substring(1);
                query = query.replace(regReplaceNames, names).replace(regReplaceValues, values);
                try {
                    HSQLDBCommons.executeQuery(query);
                } catch (Exception ex) {
                    if (ex.getMessage().contains("unique constraint")) {
                        query=updateQuery;
                        extra = extra.substring(1);
                        query+=extra+" where id = "+id+" and universe_id = "+universeNumber;
                        try {
                            HSQLDBCommons.executeQuery(query);
                        }catch (Exception exc){
                            exc.printStackTrace();
                        }
                    }else{
                        System.err.println("FAILED QUERY: "+query);
                        ex.printStackTrace();
                    }
                }
            });
            System.out.println("DONE PARSING: "+name+" universe: "+universeNumber);
        }

        private void parseUniverseFiles(String universeNumber) throws IOException, SQLException {
            String regReplaceNames = "{NAMES}", regReplaceValues = "{VALUES}";
            String query = "INSERT INTO "+name+"(" + regReplaceNames + ") VALUES (" + regReplaceValues + ");";

            String names = "", values = "", domain = "", extra = "";
            for (Element e : xmlFile.getChildren()) {
                names += "," + e.getName();
                values += "," +"'"+ e.getText()+"'";
                extra += ", "+e.getName()+" = '"+e.getText()+"'";

                if(e.getName().equals("domain"))
                    domain= e.getValue();
            }
            names = names.substring(1);
            values = values.substring(1);
            query = query.replace(regReplaceNames, names).replace(regReplaceValues, values);
            try {
                HSQLDBCommons.executeQuery(query);
            } catch (Exception e) {
                if (e.getMessage().contains("unique constraint")) {
                    query = "UPDATE "+name+" SET ";
                    extra = extra.substring(1);
                    query+=extra+" where domain = '"+domain+"'";
                    HSQLDBCommons.executeQuery(query);
                } else{
                    System.err.println("FAILED QUERY: "+query);
                    throw e;
                }
            }
            System.out.println("DONE PARSING: "+name+" universe: "+universeNumber);
        }
    }
}