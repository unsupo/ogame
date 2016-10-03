package objects.messages;

import objects.Coordinates;
import ogame.utility.Initialize;
import org.jsoup.nodes.Element;
import utilities.database._HSQLDB;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Created by jarndt on 10/2/16.
 */
public class ContactLostMsg implements  IMessage {
    Coordinates lostCoordinates;
    LocalDateTime msgDate;
    public ContactLostMsg(LocalDateTime msgDate, Element e) {
        lostCoordinates = new Coordinates(e.select("a.txt_link").text().trim());
        this.msgDate = msgDate;
    }

    @Override
    public void writeToDatabase(int universeID) throws IOException, SQLException {
        String query = "INSERT INTO DONT_ATTACK_LIST(coords,universe_id,lostFleet) " +
                "VALUES('"+lostCoordinates.getStringValue()+"',"+Initialize.getUniverseID()+",1)";
        try {
            _HSQLDB.executeQuery(query);
        }catch (Exception e){
            if (!e.getMessage().contains("unique constraint")) {
                System.err.println("FAILED QUERY: " + query);
                throw e;
            }
        }
    }
}
