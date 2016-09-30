package objects.messages;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by jarndt on 9/30/16.
 */
public interface IMessage {
    public void writeToDatabase(int universeID) throws IOException, SQLException;
}
