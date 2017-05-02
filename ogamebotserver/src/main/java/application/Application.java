package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
        SpringApplication.run(Application.class,args);
    }
}
