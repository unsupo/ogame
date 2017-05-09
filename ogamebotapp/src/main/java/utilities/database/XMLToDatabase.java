package utilities.database;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import utilities.fileio.FileOptions;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 5/8/17.
 */
public class XMLToDatabase {
    public static void main(String[] args) throws JDOMException, IOException, SQLException, ClassNotFoundException {
        String path = FileOptions.cleanFilePath(FileOptions.RESOURCE_DIR+"ogame/xml_downloads/1/");
        new XMLToDatabase(Database.DATABASE,Database.USERNAME,Database.PASSWORD,"1")
                .parseAllFiles(Arrays.asList(
                        path+"server_1.xml",
                            path+"alliances_1.xml",
                            path+"players_1.xml",
                            path+"planets_1.xml"
                        ),
                        Arrays.asList("server","alliance","player","planet"));
    }

    String database, username, password, serverId;
    List<String> files;
    SAXBuilder builder = new SAXBuilder();

    private List<String> ignoreList = Arrays.asList("deftotf","rapidfire");
    private HashMap<String,String> replaceMap = new HashMap<>();

    public XMLToDatabase(String database, String username, String password, String serverId) {
        this.database = database;
        this.username = username;
        this.password = password;
        this.serverId = serverId;
        init();
    }

    private void init(){
        replaceMap.put("number","server_id");
        replaceMap.put("alliance","alliance_id");
        replaceMap.put("player","player_id");
    }

    public void parseAllFiles(List<String> files, List<String> tableNames) throws JDOMException, IOException, SQLException, ClassNotFoundException {
        if(files.size() != tableNames.size())
            throw new IllegalArgumentException("files list and table names list must be same size, files:"+files.size()+", tableNames: "+tableNames.size());
        String result = null;
        for (int i = 0; i < files.size(); i++) {
            replaceMap.put("id", tableNames.get(i)+"_id");
            if (files.get(i).contains("server"))
                result = insertResult(parseFileServer(files.get(i)), tableNames.get(i));
            else
                result = insertResultList(parseFile(files.get(i)), tableNames.get(i));
            try {
                new Database(database, username, password).executeQuery(result);
            }catch (Exception e){
                if(!e.getMessage().contains("duplicate key value violates unique constraint")
                        && !e.getMessage().contains("\"planet\" violates foreign key constraint \"planet_player_id_fkey\""))
                    throw e;
            }
        }
    }
    private List<String> parseFileServer(String file) throws JDOMException, IOException{
        String regex = "[A-Z]";
        Pattern p = Pattern.compile(regex);

        SAXBuilder builder = new SAXBuilder();
        Element v = builder.build(file).getRootElement();
        return v.getChildren().stream().map(a -> {
            String s = a.getName(), vv = a.getText();
            if(replaceMap.containsKey(s))
                s = replaceMap.get(s);
            if (ignoreList.contains(s.toLowerCase()))
                return s+splitString+vv;
            return s.replaceAll("([A-Z])", "_$1").toLowerCase()+splitString+"'"+vv+"'";
        }).collect(Collectors.toList());
    }
    private static String splitString = "SPLIT_BY_ME";
    private List<List<String>> parseFile(String file) throws JDOMException, IOException{
        String regex = "[A-Z]";
        Pattern p = Pattern.compile(regex);
        SAXBuilder builder = new SAXBuilder();
        Element v = builder.build(file).getRootElement();
        return v.getChildren().stream().map(a->{
            List<String> m = a.getAttributes().stream().map(b -> attributeMapper(b)).collect(Collectors.toList());
            String id = replaceMap.get("id");
            if(a.getChild("moon")!=null) {
                replaceMap.put("id","moon_id");
                replaceMap.put("name","moon_name");
                replaceMap.put("size","moon_size");
                m.addAll(a.getChild("moon").getAttributes().stream().map(b -> attributeMapper(b)).collect(Collectors.toList()));
            }
            replaceMap.remove("size");
            replaceMap.remove("name");
            replaceMap.put("id",id);
            return m;
        }).collect(Collectors.toList());
    }
    private String attributeMapper(Attribute b){
        String s = b.getName(), vv = b.getValue();
        if (replaceMap.containsKey(s))
            s = replaceMap.get(s);
        if (ignoreList.contains(s.toLowerCase()))
            return s + splitString + vv;
        return s.replaceAll("([A-Z])", "_$1").toLowerCase() + splitString + "'" + vv + "'";
    }

    private String insertResult(List<String> queryList, String tableName) throws SQLException, IOException, ClassNotFoundException {
        StringBuilder query = new StringBuilder("INSERT INTO "+tableName.toUpperCase()+"("),
                res = new StringBuilder(")\n\tVALUES(");
        for(String q : queryList){
            String[] vv = q.split(splitString);
            query.append(vv[0]+",");
            res.append(vv[1]+",");
        }
        query = new StringBuilder(query.substring(0,query.length()-1));
        res = new StringBuilder(res.substring(0,res.length()-1)).append(");");
        return query.append(res).toString();
    }

    private String insertResultList(List<List<String>> queryListList, String tableName) throws SQLException, IOException, ClassNotFoundException {
        StringBuilder result = new StringBuilder("");
        for(List<String> queryList : queryListList) {
            queryList.add("server_id"+splitString+serverId);
            result.append(insertResult(queryList, tableName)).append("\n");
        }
        return result.toString();
    }
}
