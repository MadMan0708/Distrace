package cz.cuni.mff.d3s.distrace.utils;

import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;


/**
 * Helper methods for code generation
 */
public class CodeUtils {

    public static DynamicType.Builder<?> defineField(DynamicType.Builder<?> builder, Class clazz, String name){
        return builder.defineField(name, clazz, Visibility.PRIVATE);
    }


}
