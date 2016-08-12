package cz.cuni.mff.d3s.distrace.examples.interceptors;

import cz.cuni.mff.d3s.distrace.utils.CodeUtils;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

public abstract class SimpleInterceptor {

    @RuntimeType
    public static void print(@This Object that){
        CodeUtils.injectTraceContext(that);
        System.out.println("Method print was instrumented, property was defined and now has value: ");
    }
}
