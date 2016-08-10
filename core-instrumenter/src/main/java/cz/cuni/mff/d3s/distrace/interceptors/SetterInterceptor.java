package cz.cuni.mff.d3s.distrace.interceptors;

import net.bytebuddy.implementation.bind.annotation.FieldProxy;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;


public class SetterInterceptor {
    @RuntimeType
    public Object intercept(@FieldProxy("stringVal") FieldGetterSetter accessor) {
        Object value = accessor.getValue();
        System.out.println("Invoked method with: " + value);
        return value;
    }
}