package utilities;

import org.openqa.selenium.By;
import utilities.fileio.FileOptions;
import utilities.webdriver.Driver;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 5/4/17.
 */
public class ProxyList {
    public static void main(String[] args) throws IOException {
        parallelTestIPS();
    }

    public final static String PROXY_LIST_DIR = FileOptions.cleanFilePath(FileOptions.DEFAULT_DIR+"/ogamebotapp/src/main/resources/proxylist/");
    public static void parallelTestIPS() throws IOException {
        Set<String> ips = new HashSet<>(), removeIps = new HashSet<>();

        String path = PROXY_LIST_DIR;
        FileOptions.getAllFiles(path).parallelStream()
                .forEach(a -> {
                    try {
                        List<String> files = FileOptions.readFileIntoListString(FileOptions.cleanFilePath(a.getAbsolutePath()))
                                .parallelStream().map(b->b.trim())
                                .filter(b->b.contains(":") && b.split("\\.").length == 4)
                                .collect(Collectors.toList());
                        if(!a.getAbsolutePath().toLowerCase().contains("failed"))
                            ips.addAll(files);
                        else
                            removeIps.addAll(files);
                    } catch (IOException e) { /*DO NOTHING*/ }
                });
        ips.removeAll(removeIps);

        String nanoTime = System.nanoTime()+"";
        new File(path+"/working"+nanoTime).mkdirs();
        new File(path+"/failed"+nanoTime).mkdirs();

        Set<String> failedIPs = new HashSet<>();
        HashMap<String,Long> workingIPs = new HashMap<>();
        ExecutorService service = Executors.newFixedThreadPool(100);
        ips.forEach(a->{
            service.submit(()->{
                Driver driver = new Driver();
                try {
                    driver.setProxy(a);
                    driver.setDriverName(Driver.PHANTOMJS);
                    utilities.Timer t = new utilities.Timer().start();
                    driver.getDriver().navigate().to("http://www.whatsmyip.org/");
                    String ip = driver.getDriver().findElements(By.id("ip")).get(0).getText();
                    t.stop();
                    System.out.println(a+"\tIP: "+ip+"\tTook: "+t.getTime());
                    workingIPs.put(a+","+ip,t.getNanoTime());
                    FileOptions.writeToFileOverWrite(path+"/working"+nanoTime+"/"+ UUID.randomUUID().toString()+".txt",a);
                }catch (Exception e){
                    System.out.println("IP FAILED: "+a);
                    failedIPs.add(a);
                    try {
                        FileOptions.writeToFileOverWrite(path+"/failed"+nanoTime+"/"+UUID.randomUUID().toString()+".txt",a);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                driver.getDriver().quit();
            });
        });

        try {
            System.out.println("attempt to shutdown executor");
            service.shutdown();
            service.awaitTermination(5, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        }
        finally {
            if (!service.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            service.shutdownNow();
            System.out.println("shutdown finished");
        }

        Set<String> working = new HashSet<>();
        working.addAll(workingIPs.keySet());
        working.addAll(FileOptions.readFileIntoListString(PROXY_LIST_DIR+"working.txt").stream().map(a->a.trim()).collect(Collectors.toList()));

        failedIPs.addAll(FileOptions.readFileIntoListString(PROXY_LIST_DIR+"failed.txt").stream().map(a->a.trim()).collect(Collectors.toList()));

        FileOptions.writeToFileAppend(PROXY_LIST_DIR+"working.txt",working.stream().reduce("",(u, s) -> u+"\n"+s));
        FileOptions.writeToFileAppend(PROXY_LIST_DIR+"failed.txt",failedIPs.stream().reduce("",(u, s) -> u+"\n"+s));

        FileOptions.deleteDirectory(path+"/working"+nanoTime);
        FileOptions.deleteDirectory(path+"/failed"+nanoTime);
    }

}
