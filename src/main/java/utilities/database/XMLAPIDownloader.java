package utilities.database;

import utilities.Utility;
import utilities.filesystem.FileOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

import static ogame.utility.Initialize.f;

/**
 * Created by jarndt on 9/27/16.
 */
public class XMLAPIDownloader {
    final static public String regReplace = "{UNI}";
    public static final String
            SERVER_DATA_FILE        = "server_data",
            UNIVERSE_FILE           = "universe_data",
            PLAYERS_FILE            = "players_data",
            HIGHSCORE_PLAYER_FILE   = "highscore_player_data",
            HIGHSCORE_ALLIANCE_FILE = "highscore_alliance_data";

    final static public String
                    SERVER_DATA          = "https://"+regReplace+"/api/serverData.xml",
                    UNIVERSE             = "https://"+regReplace+"/api/universe.xml",
                    PLAYERS              = "https://"+regReplace+"/api/players.xml",
                    HIGHSCORE_PLAYER     = "https://"+regReplace+"/api/highscore.xml?category=1&type=1",
                    HIGHSCORE_ALLIANCE   = "https://"+regReplace+"/api/highscore.xml?category=2&type=1";

    final public static HashMap<String,String> FILE_DATA = new HashMap<>();

    static{
        FILE_DATA.put(SERVER_DATA_FILE,SERVER_DATA);
        FILE_DATA.put(UNIVERSE_FILE,UNIVERSE);
        FILE_DATA.put(PLAYERS_FILE,PLAYERS);
        FILE_DATA.put(HIGHSCORE_PLAYER_FILE,HIGHSCORE_PLAYER);
        FILE_DATA.put(HIGHSCORE_ALLIANCE_FILE,HIGHSCORE_ALLIANCE);
    }

    public static void downloadAllFiles(final String universeNumber){
        final String dir = Utility.RESOURCE_DIR + "xml_downloads";
        new File(dir).mkdirs();
        try {
            String lastUpdate = FileOptions.readFileIntoString(dir+"/last_update_"+universeNumber).trim();
            LocalDateTime dateTime = LocalDateTime.from(f.parse(lastUpdate));

            if(dateTime.plusDays(1).isAfter(LocalDateTime.now()))
                return;

        } catch (IOException e) {
        }
        FILE_DATA.entrySet().parallelStream().forEach(a-> {
            try {
                URL website = new URL(a.getValue().replace(regReplace, "s"+universeNumber+"-en.ogame.gameforge.com"));
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream(dir+"/"+a.getKey()+"_"+universeNumber+".xml");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            FileOptions.writeToFileOverWrite(dir+"/last_update_"+universeNumber,LocalDateTime.now().format(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Integer[] universeNumbers = {
                1,
                118,
                120,
                122,
                123,
                128,
                129,
                130,
                131,
                132,
                133,
                134,
                135,
                136,
                137,
                138,
                139,
                140,
                141
        };

        Arrays.asList(universeNumbers).parallelStream().forEach(a->{
            downloadAllFiles(a+"");
        });
    }
}
