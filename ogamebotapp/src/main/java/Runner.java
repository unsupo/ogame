import utilities.JarUtility;
import utilities.PasswordEncryptDecrypt;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jarndt on 5/2/17.
 */
public class Runner {
    public static void main(String[] args) throws IOException, GeneralSecurityException, URISyntaxException {
        parseCommandLineArgs(args);
        JarUtility.extractFiles();
    }

    private static void parseCommandLineArgs(String[] args) throws IOException, GeneralSecurityException {
        if(args!=null && args.length != 0){
            List<String> ecryptSwitches = new ArrayList<>(Arrays.asList("-encrypt","-e"));
            List<String> defaultDriver = new ArrayList<>(Arrays.asList("-defaultDriver","-dd"));
            List<String> exportPath = new ArrayList<>(Arrays.asList("-exportPath","-ep"));
            List<String> webDriverPath = new ArrayList<>(Arrays.asList("-webDriverPath","-wdp"));
            for(int i = 0; i<args.length; i++){
                if(ecryptSwitches.contains(args[i])){
                    try{
                        PasswordEncryptDecrypt.encryptReader();
                    }catch (IndexOutOfBoundsException e){
                        System.err.println("No value to encrypt");
                    }
                    System.exit(0);
                }
                try {
                    if(defaultDriver.contains(args[i]))
                        JarUtility.setDefaultDrivers(Arrays.asList(args[i + 1].split(",")));
                    if(exportPath.contains(args[i]))
                        JarUtility.setExportPath(args[i + 1]);
                    if(webDriverPath.contains(args[i]))
                        JarUtility.setWebDriverPath(args[i + 1]);
                }catch(IndexOutOfBoundsException e){
                    System.err.println("No given switch option for: "+args[i]);
                }
            }
        }
    }

}
