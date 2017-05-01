package application;

import org.springframework.web.bind.annotation.*;


/**
 * Created by jarndt on 4/13/17.
 */
@RestController
public class ApplicationController {
    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name",required = false, defaultValue = "World") String name){
        return "greeting";
    }
}
