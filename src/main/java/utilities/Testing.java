package utilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriver;
import utilities.selenium.UIMethods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarndt on 8/3/16.
 */
public class Testing {
    public static void main(String[] args) throws IOException {
        WebDriver driver = UIMethods.getWebDriver();
        driver.navigate().to("http://ogame.org");
        UIMethods.clickOnAttributeAndValue("id","loginBtn");
        UIMethods.selectFromDropDown("id","serverLogin","s117-en.ogame.gameforge.com");
        UIMethods.typeOnAttributeAndValue("id","usernameLogin","username");
        UIMethods.typeOnAttributeAndValue("id","passwordLogin","password");
        UIMethods.clickOnAttributeAndValue("id","loginSubmit");
    }


    public static void test() throws IOException {

        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpGet httpget = new HttpGet("https://en.ogame.gameforge.com/main/login");

        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();

        System.out.println("Login form get: " + response.getStatusLine());
        if (entity != null) {
            entity.consumeContent();
        }
        System.out.println("Initial set of cookies:");
        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
        if (cookies.isEmpty()) {
            System.out.println("None");
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                System.out.println("- " + cookies.get(i).toString());
            }
        }

        HttpPost httpost = new HttpPost("https://en.ogame.gameforge.com/main/login");

        List <NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("login", "unsupo"));
        nvps.add(new BasicNameValuePair("pass", "supersmash"));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        response = httpclient.execute(httpost);
        entity = response.getEntity();
        System.out.println("Double check we've got right page " + EntityUtils.toString(entity));

        System.out.println("Login form get: " + response.getStatusLine());
        if (entity != null) {
            entity.consumeContent();
        }

        System.out.println("Post logon cookies:");
        cookies = httpclient.getCookieStore().getCookies();
        if (cookies.isEmpty()) {
            System.out.println("None");
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                System.out.println("- " + cookies.get(i).toString());
            }
        }

        httpclient.getConnectionManager().shutdown();
    }

    /**
     * To login
     * Select universe with (this gets all the appropriate values document.getElementById("serverLogin")):
     *  document.getElementById("serverLogin").value='s129-en.ogame.gameforge.com'
     *
     * This sets the login:
     *  document.getElementById("usernameLogin").value='hi'
     *
     * This sets the password:
     *  document.getElementById("passwordLogin").value='hi'
     *
     * This clicks the login button
     *  document.getElementById("loginSubmit").click()
     */

    /**
     * To sign up
     * Javascript to select universe, just change Betelgeuse to whatever:
     *  select_uni('s128-en.ogame.gameforge.com', 'Betelgeuse','exodus-server-old');
     *
     * This is all the allowed univserses:
     *  document.getElementsByClassName("uni_span margin-uni-selection")
     *
     * This sets the username:
     *  document.getElementById("username").value = 'hi'
     *
     * This sets the password:
     *  document.getElementById("password").value = 'hi'
     *
     * This sets the email:
     *  document.getElementById("email").value = 'hi'
     *
     * This checks you agree to terms and conditions:
     *  document.getElementById("agb").checked = true
     *
     * This submits the registration:
     *  document.getElementById("regSubmit").click()
     *
     *  //finally need to get the results of the sign up
     */
}
