package application;

import org.springframework.web.bind.annotation.*;


/**
 * Created by jarndt on 4/13/17.
 */
@RestController
public class ApplicationController {
//    public static void main(String[] args) {
//        DriverController driverController = new DriverController();
//        driverController.setDriverName(DriverController.PHANTOMJS);
//        driverController.getDriver().navigate().to("http://google.com");
//        byte[] bytesArr = ((TakesScreenshot) driverController.getDriver()).getScreenshotAs(OutputType.BYTES);
//        System.out.println();
//    }

//    DriverController driverController = new DriverController();
//    List<String> webpages = Arrays.asList(
//            "http://google.com",
//            "http://stackoverflow.com",
//            "https://www.w3schools.com/angular/ng_ng-src.asp"
//    );
//    public ApplicationController(){
//        driverController.setDriverName(DriverController.PHANTOMJS);
//        new Thread(()->{
//            int i = 0;
//            Thread t;
//            while (true) {
//                driverController.getDriver().navigate().to(webpages.get((i++) % webpages.size()));
////                t = new Thread(()->{
////                    driverController.getDriver().navigate().to(webpages.get((i++) % webpages.size()));
////                }).start();
//                try {
//                    Thread.sleep(5000);
////                    t.interrupt();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name",required = false, defaultValue = "World") String name){
        return "greeting";
    }
//    @ResponseBody
//    @RequestMapping(value = "/getImage", method = RequestMethod.GET)//, produces = MediaType.IMAGE_PNG_VALUE)
//    public String getScreenShot(/*@RequestParam(value = "driverName") String driverName*/) throws IOException {
//        byte[] bytesArr = ((TakesScreenshot) driverController.getDriver()).getScreenshotAs(OutputType.BYTES);
//        String b64 = Base64.encodeBase64String(bytesArr);
//
////        URL url = new URL(driverController.getDriver().getCurrentUrl());
////        String baseUrl = url.getProtocol() + "://" + url.getHost();
////        String s = driverController.getDriver().getPageSource().replaceAll("src=\"\\/","src=\""+baseUrl+"/");
//        return b64;
//    }
}
