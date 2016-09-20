package utilities;

/**
 * Created by jarndt on 9/19/16.
 */

public class OSProperties {

    public static final String 	SEPERATOR 		= System.getProperty("file.separator"),
            PATH 			= System.getProperty("user.dir")+SEPERATOR;

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static void main(String[] args){
        System.out.println(PATH);
    }

    public static String getOS(){
        if (isWindows())
            return "Windows";
        else if (isMac())
            return "Mac";
        else if (isUnix())
            return "Linux";
        else if (isSolaris())
            return "Solaris";
        return null;
    }

    public static boolean isWindows() {

        return (OS.indexOf("win") >= 0);

    }

    public static boolean isMac() {

        return (OS.indexOf("mac") >= 0);

    }

    public static boolean isUnix() {

        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );

    }

    public static boolean isSolaris() {

        return (OS.indexOf("sunos") >= 0);

    }

}