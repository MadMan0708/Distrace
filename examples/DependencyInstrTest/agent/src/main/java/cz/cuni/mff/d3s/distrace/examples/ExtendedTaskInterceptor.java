package cz.cuni.mff.d3s.distrace.examples;


import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.Super;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.util.concurrent.Callable;

public class ExtendedTaskInterceptor {
    
    @Advice.OnMethodExit
    public static String instrument(@SuperCall(serializableProxy=true) Callable<String> value) {
        try {
            return "Instrumented by Extended " + value.call();
        } catch (Exception e) {
            return "a";
        }
    }
}
