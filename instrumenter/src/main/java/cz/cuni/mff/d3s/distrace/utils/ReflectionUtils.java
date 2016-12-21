package cz.cuni.mff.d3s.distrace.utils;

import java.lang.reflect.Method;

/**
 * Created by kuba on 19/12/2016.
 */
public class ReflectionUtils {
    public static Method findMethod(Class cl, String methodName, Class<?>... parameterTypes){
        try {
            return cl.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException ignore) {
            throw new RuntimeException("Method should be available!");
        }
    }
}
