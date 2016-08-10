package cz.cuni.mff.d3s.distrace.utils;

import cz.cuni.mff.d3s.distrace.api.TraceContext;
import cz.cuni.mff.d3s.distrace.interceptors.FieldGetterSetter;
import cz.cuni.mff.d3s.distrace.interceptors.SetterInterceptor;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.FieldProxy;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.MessageFormat;


/**
 * Helper methods for code generation
 */
public class CodeUtils {

    public static DynamicType.Builder<?> defineField(DynamicType.Builder<?> builder, Class clazz, String name){
        return builder.defineField(name, clazz, Visibility.PRIVATE);
    }


}
