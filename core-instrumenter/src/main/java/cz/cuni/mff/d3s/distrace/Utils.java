package cz.cuni.mff.d3s.distrace;

/**
 * Various helper methods
 */
public class Utils {

    public static String convertToJavaName(String name){
        return name.replaceAll("/", ".");
    }

}
