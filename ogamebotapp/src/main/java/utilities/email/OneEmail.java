package utilities.email;

import org.openqa.selenium.By;
import utilities.PasswordEncryptDecrypt;
import utilities.fileio.FileOptions;
import utilities.webdriver.Driver;
import utilities.webdriver.JavaScriptFunctions;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by jarndt on 5/5/17.
 */
public class OneEmail {
    public static void main(String[] args) throws IOException {
        parallelCreateAccounts(1000);
        cleanUpEmailDirectory();
    }
    public static final String EMAILS_DIR =
            FileOptions.cleanFilePath(FileOptions.DEFAULT_DIR+"/ogamebotapp/src/main/resources/emails/");

    public static void cleanUpEmailDirectory() throws IOException {
        final String path = FileOptions.cleanFilePath(EMAILS_DIR+"working/");
        Set<String> emails = new HashSet<>();
        emails.addAll(FileOptions.getAllFilesEndsWith(path,".txt").stream().map(a -> {
            try {
                return FileOptions.readFileIntoString(a.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(a->a!=null).collect(Collectors.toList()));

        String emailFile = "email_list.txt";
        FileOptions.writeToFileOverWrite(EMAILS_DIR+emailFile,emails.stream().reduce("",(a,b)->a+"\n"+b));

        FileOptions.deleteDirectory(path);
//        FileOptions.getAllFilesEndsWith(path,".txt").stream().filter(a->!a.getName().equals(emailFile))
//                .forEach(a->a.delete());
    }

    public static void parallelCreateAccounts(int numAccounts){
        final String path = FileOptions.cleanFilePath(EMAILS_DIR+"working/");
        new File(path).mkdirs();
        List<Callable> names = IntStream.range(0, numAccounts).boxed().map(i -> (Callable) () -> {
            try {
                String name = randomString(10);
                new OneEmail().createNewOneEmail(name, name);
                FileOptions.writeToFileOverWrite(path + UUID.randomUUID().toString() + ".txt", name+"@michaelgutin.one");
                return name;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
        FileOptions.runConcurrentProcess(names);
    }

    private static List<String> characters =
            IntStream.range('a', 'z' + 1).boxed().map(i -> "" + ((char) i.intValue())).collect(Collectors.toList());
    static {
        IntStream.range(0,10).forEach(i->characters.add(i+""));
    }
    private static Random random = new Random();
    public static String randomString(int length){
        StringBuilder builder = new StringBuilder("");
        for(int i = 0; i<length; i++)
            builder.append(characters.get(random.nextInt(characters.size())));
        return builder.toString();
    }


    String oneUserName = "Wz5qIq65vmbVV6x+8bX0zN+p50n7NvwK", onePassword = "+/xb61x36biew0c7oW6Mlg==";

    public void createNewOneEmail(String username, String password) throws GeneralSecurityException, IOException {
        final String    oneUser = PasswordEncryptDecrypt.decrypt(oneUserName),
                        onePass = PasswordEncryptDecrypt.decrypt(onePassword);
        Driver driver = new Driver();
        try {
            driver.setDriverName(Driver.PHANTOMJS);
            driver.getDriver().navigate().to("https://login.one.com/");

            JavaScriptFunctions.fillFormByXpath(driver, "/html/body/div[3]/div[2]/div[2]/div[1]/div/form/input[2]", oneUser);
            JavaScriptFunctions.fillFormByXpath(driver, "/html/body/div[3]/div[2]/div[2]/div[1]/div/form/input[5]", onePass);

            driver.getDriver().findElements(By.xpath("/html/body/div[3]/div[2]/div[2]/div[1]/div/form/div[3]/button")).get(0).click();

            driver.clickWait(By.xpath("//*[@id=\"frontpageMailLink\"]"), 1l, TimeUnit.MINUTES);
            driver.clickWait(By.xpath("//*[@id=\"content\"]/div[2]/fieldset[1]/a"), 1l, TimeUnit.MINUTES);

            driver.waitForElement(By.xpath("//*[@id='name']"), 1l, TimeUnit.MINUTES);

            JavaScriptFunctions.fillFormByXpath(driver, "//*[@id='name']", username, 1);
            JavaScriptFunctions.fillFormByXpath(driver, "//*[@id='mailPassword1']", password);
            JavaScriptFunctions.fillFormByXpath(driver, "//*[@id='mailPassword2']", password);

            driver.getDriver().findElements(By.xpath("//*[@id=\"mailAccountForm\"]/div[2]/input[2]")).get(0).click();
        }catch (Exception e){
            driver.getDriver().quit();
            throw e;
        }
        driver.getDriver().quit();
    }
}
