package utilities.http;

/**
 * Created by jarndt on 8/30/16.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class HttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception {
        String v = "https://s141-en.ogame.gameforge.com/game/index.php?page=messages&amp;messageId=2097353&amp;tabid=21&amp;ajax=1";

//        new HttpClient().sendGet();

    }

    public HttpResponse getResponse() {
        return response;
    }

    private HttpResponse response;
    private String url;
    private URI uri;
    private org.apache.http.client.HttpClient httpclient = HttpClients.createDefault();
    private List<NameValuePair> params = new ArrayList<NameValuePair>();


    public HttpClient(String url) {
        this.url = url;
    }
    public HttpClient(URI uri) {
        this.uri = uri;
    }

    public HttpClient addParamter(String name, String value){
        params.add(new BasicNameValuePair(name, value));
        return this;
    }

    // HTTP GET request
    public String sendGet(String...body) throws Exception {
        String b = "";
        if(body != null && body.length == 1)
            b = body[0];
        HttpGetWithEntity request = url == null ? new HttpGetWithEntity(uri) : new HttpGetWithEntity(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);
        StringEntity se = new StringEntity(b);
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
        request.setEntity(se);

        // Request parameters and other properties.
        for(NameValuePair key : params)
            request.addHeader(key.getName(),key.getValue());

        response = httpclient.execute(request);

        logger.debug("Sending GET request to URL: "+url+"\n"+request);
        logger.debug("Response code: "+response.getStatusLine().getStatusCode());


        return getResult(response.getEntity());
    }

    // HTTP POST request
    public String sendPost(String...body) throws Exception {
        String b = "";
        if(body != null && body.length == 1)
            b = body[0];
        HttpPost httppost = url == null ? new HttpPost(uri) : new HttpPost(url);

        // Request parameters and other properties.
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        StringEntity se = new StringEntity(b);
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
        httppost.setEntity(se);

        //Execute and get the response.
        response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        logger.debug("Sending POST request to URL: "+(url==null?uri:url));
        logger.debug("Response code: "+response.getStatusLine().getStatusCode());
        logger.debug("Response: "+response);

        return getResult(entity);
    }



    private String getResult(HttpEntity entity) throws IOException {
        StringBuffer result = new StringBuffer();
        if (entity != null) {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(entity.getContent()));
            InputStream instream = entity.getContent();
            try {
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                    result.append(System.getProperty("line.separator"));
                }
            } finally {
                instream.close();
            }
        }
        logger.debug(result.toString());
        return result.toString();
    }

}

class HttpGetWithEntity extends HttpPost {

    public final static String METHOD_NAME = "GET";

    public HttpGetWithEntity(URI url) {
        super(url);
    }

    public HttpGetWithEntity(String url) {
        super(url);
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}