package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.utils.ClassCreator;

/**
 * Various helper methods
 */
public class Utils {

    public static String convertToJavaName(String name){
        return name.replaceAll("/", ".");
    }

    public static boolean loaded(String className){
        String classNameDots = Utils.convertToJavaName(className);
        return ClassCreator.contains(classNameDots);
    }

    public static boolean forceLoad(byte[] classBytes, String className) {
        String classNameDots = Utils.convertToJavaName(className);
        return ClassCreator.loadClassWithAllReferences(classBytes, classNameDots);
    }
}
