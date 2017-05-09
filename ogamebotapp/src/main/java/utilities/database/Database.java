package utilities.database;

import utilities.fileio.FileOptions;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jarndt on 5/8/17.
 */
public class Database {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        Database d = new Database("localhost:5432/ogame","ogame_user","ogame");

        d.executeQuery("delete from server");
//        d.executeQuery("select * from server")
//                .forEach(System.out::println);
    }

    public static final String DATABASE = "localhost:5432/ogame", USERNAME = "ogame_user", PASSWORD = "ogame";

    public static boolean checkForPostgres(String server, String username, String password){ /*127.0.0.1:5432/testdb*/
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://"+server, username, password);
            if (connection != null)
                return true;
        } catch (Exception e) { /* */ }
        return false;
    }

    private static String SQL_SCRIPT_DIR = FileOptions.cleanFilePath(FileOptions.RESOURCE_DIR+"database_config/");
    private Connection connection;

    public Database(String server, String username, String password) throws SQLException, ClassNotFoundException, IOException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(
            "jdbc:postgresql://"+server, username, password);
        executeQuery(FileOptions.readFileIntoString(SQL_SCRIPT_DIR+"create_tables.sql"));
    }

    public List<Map<String,Object>> executeQuery(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = null;
        try{
            rs = stmt.executeQuery(query);
        }catch(SQLException sql){
            if(sql.getMessage().contains("Table already exists")
                    || sql.getMessage().contains("No results were returned by the query")){
                return null;
            }else if(sql.getMessage().contains("Unexpected token: POSITION in statement")){
                rs = stmt.executeQuery(query.toUpperCase().replace("POSITION", "\"POSITION\""));
            }else{
                throw sql;
            }
        }
        List<Map<String,Object>> results = new ArrayList<Map<String, Object>>();
        while(rs.next()){
            Map<String, Object> subMap = new HashMap<String, Object>();
            ResultSetMetaData rsmd = rs.getMetaData();
            for(int i = 1; i<=rsmd.getColumnCount(); i++){
                subMap.put(rsmd.getColumnLabel(i), rs.getObject(i));
            }
            results.add(subMap);
        }
        return results;
    }

}
