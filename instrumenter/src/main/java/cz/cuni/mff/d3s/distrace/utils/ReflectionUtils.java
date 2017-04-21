package cz.cuni.mff.d3s.distrace.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Helper class providing methods to ease work with reflections
 */
public class ReflectionUtils {

    /**
     * Find method on provided class
     * @param cl class on which to look for a method
     * @param methodName method name
     * @param parameterTypes parameter types
     * @return found method
     */
    public static Method getMethod(Class<?> cl, String methodName, Class<?>... parameterTypes){
        try {
            return cl.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException ignore) {
            throw new RuntimeException(String.format("Method \"%s\" needs to be available!", methodName));
        }
    }

    /**
     * Get object value
     * @param o object on which to obtain field value
     * @param fieldName field name
     * @return value
     */
    public static Object getFieldValue(Object o, String fieldName){
        try {
            Field f = o.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(String.format("Field \"%s\" needs to be available!", fieldName));
        }
    }

}
