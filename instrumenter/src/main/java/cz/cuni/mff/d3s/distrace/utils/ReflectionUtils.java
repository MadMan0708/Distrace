package cz.cuni.mff.d3s.distrace.utils;

import java.lang.reflect.Method;

/**
 * Helper class providing methods to ease work with reflections
 */
public class ReflectionUtils {
    public static Method findMethod(Class<?> cl, String methodName, Class<?>... parameterTypes){
        try {
            return cl.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException ignore) {
            throw new RuntimeException(String.format("Method \"%s\" needs to be available!", methodName));
        }
    }

}
