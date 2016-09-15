package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.utils.ClassCreator;

/**
 * Various helper methods
 */
public class Utils {

    public static String convertToJavaName(String name){
        return name.replaceAll("/", ".");
    }

    public static void forceLoad(byte[] classBytes, String className) {
        String classNameDots = Utils.convertToJavaName(className);
        ClassCreator.loadClassWithAllReferences(classBytes, classNameDots);
    }
}
