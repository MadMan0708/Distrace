package cz.cuni.mff.d3s.distrace.examples;


import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.util.concurrent.Callable;


public class BaseTaskInterceptor {

    public static String instrument(@SuperCall(serializableProxy=true) Callable<String> value) throws Exception{
        return "Instrumented by Base: (" + value.call() + ")";
    }
}
