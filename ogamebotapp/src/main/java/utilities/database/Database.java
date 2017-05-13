package utilities.database;

import org.postgresql.util.PSQLException;
import utilities.fileio.FileOptions;
import utilities.fileio.JarUtility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.bouncycastle.crypto.tls.ConnectionEnd.server;

/**
 * Created by jarndt on 5/8/17.
 */
public class Database {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        Database d = new Database("localhost:9999/ogame","ogame_user","ogame");
//        d.executeQuery("drop database if exists jarndt");
//        d.executeQuery("select pg_terminate_backend(pid) from pg_stat_activity where datname='test';")
//            .forEach(System.out::println);
//        d.executeQuery("SELECT pid FROM pg_stat_activity where pid <> pg_backend_pid();")
//                .forEach(System.out::println);
//        Database d = new Database("localhost:5432/ogame","ogame_user","ogame");

//        d.executeQuery("DROP TABLE SERVER CASCADE;\n" +
//                "DROP TABLE ALLIANCE CASCADE;\n" +
//                "DROP TABLE PLAYER CASCADE;\n" +
//                "DROP TABLE PLANET CASCADE;\n" +
//                "DROP TABLE ALLIANCE_HIGHSCORE CASCADE;\n" +
//                "DROP TABLE PLAYER_HIGHSCORE CASCADE;");

//        d.executeQuery("delete from server");
        d.executeQuery("select * from server")
                .forEach(System.out::println);
    }

    public static final String DATABASE = "localhost:9999/ogame", USERNAME = "ogame_user", PASSWORD = "ogame";

    public static boolean checkForPostgres(String server, String username, String password){ /*127.0.0.1:5432/testdb*/
        try {
//            DriverManager.getConnection()
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://"+server, username, password);
            if (connection != null)
                return true;
        } catch (Exception e) { /* */ }
        return false;
    }

    private static String SQL_SCRIPT_DIR = FileOptions.cleanFilePath(JarUtility.getResourceDir()+"/database_config/");
    private Connection connection;
    private String server,username,password;

    public Database(String server, String username, String password) throws SQLException, ClassNotFoundException, IOException {
        this.server = server; this.username = username; this.password = password;
        getConnection();
        executeQuery(FileOptions.readFileIntoString(SQL_SCRIPT_DIR+"create_tables.sql"));
    }
    int attempt = 0;
    private Connection getConnection() throws SQLException {
        if(connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
                try {
                    connection = DriverManager.getConnection(
                            "jdbc:postgresql://" + server, username, password);
                } catch (PSQLException e) {
                    if (e.getMessage().contains("Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections."))
                        startDatabase();
                    if (e.getMessage().equals("FATAL: database \"ogame\" does not exist"))
                        init();
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            if(attempt++ > 10) return null;
            return getConnection();
        }
        return connection;
    }

    private void init() throws ClassNotFoundException, SQLException, IOException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:9999/postgres", "postgres", "postgres");
        try {
            conn.prepareCall("CREATE USER OGAME_USER WITH PASSWORD 'ogame';")
                    .executeUpdate();
        }catch (PSQLException e){
            if(!e.getMessage().equals("ERROR: role \"ogame_user\" already exists"))
                throw e;
        }
        conn.prepareCall("CREATE DATABASE ogame OWNER = OGAME_USER; ")
            .executeUpdate();
    }

    public List<Map<String,Object>> executeQuery(String query) throws SQLException {
        Statement stmt = getConnection().createStatement();
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

    public static void startDatabase() throws IOException {
        String databaseDir = FileOptions.cleanFilePath(JarUtility.getResourceDir() + "/databases/");
        File postgresDir = new File(databaseDir+"/postgres");
        if(!(postgresDir.exists() && postgresDir.isDirectory())) {
            String zipFile = "postgres.zip";
            String windowsCommand = "powershell.exe -nologo -noprofile -command \"& { Add-Type -A 'System.IO.Compression.FileSystem'; [IO.Compression.ZipFile]::ExtractToDirectory('"+zipFile+"', '.'); }\"";
            String macCommand = "unzip "+zipFile;
            String command = macCommand;
            if(FileOptions.OS.substring(0,3).equals(JarUtility.WINDOWS))
                command = windowsCommand;
            ExecutorService service = FileOptions.runSystemProcess(command, databaseDir);

        }
        if(!checkForPostgres(DATABASE,USERNAME,PASSWORD)) {
            FileOptions.runSystemProcess("pg_ctl -D ../postgres -l server.log start",
                    FileOptions.cleanFilePath(databaseDir + "/postgres/bin/"));
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
