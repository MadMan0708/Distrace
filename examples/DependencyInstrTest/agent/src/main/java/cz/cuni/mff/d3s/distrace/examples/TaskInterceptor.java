package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Interceptor;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.util.concurrent.Callable;


public class TaskInterceptor implements Interceptor {

    String prefix;

    public TaskInterceptor(String prefix){
        this.prefix = prefix;
    }

    public String instrument(@SuperCall(serializableProxy=true) Callable<String> value) throws Exception{
        return prefix + " (" + value.call() + ")";
    }
}
