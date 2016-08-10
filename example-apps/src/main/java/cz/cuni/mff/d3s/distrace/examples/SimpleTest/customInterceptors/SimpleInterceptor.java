package cz.cuni.mff.d3s.distrace.examples.SimpleTest.customInterceptors;

import cz.cuni.mff.d3s.distrace.interceptors.FieldGetterSetter;
import net.bytebuddy.implementation.bind.annotation.FieldProxy;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

public class SimpleInterceptor {
    @RuntimeType
    public static void print(@FieldProxy("traceContext") FieldGetterSetter accessor){
        //accessor.setValue("ahoj");
        System.out.println("Method print was instrumented, property was defined and now has value: " );//+ accessor.getValue());
    }
}
