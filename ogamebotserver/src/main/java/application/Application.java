package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import utilities.FileOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 4/13/17.
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) throws IOException, InterruptedException {
        init();
        SpringApplication.run(Application.class,args);
    }

    public static final String STATIC_DIR = FileOptions.cleanFilePath(FileOptions.DEFAULT_DIR+"/ogamebotserver/src/main/resources/static");

    public static void init() throws IOException, InterruptedException {
        File bowerComponents = new File(FileOptions.cleanFilePath(STATIC_DIR+"/bower_components"));
        if(!(bowerComponents.exists() && bowerComponents.isDirectory())) {
            FileOptions.runProcess("./bower.sh", STATIC_DIR);
            System.out.println("Bower Components installed.  Please rerun Application");
            System.exit();
        }
    }
}
