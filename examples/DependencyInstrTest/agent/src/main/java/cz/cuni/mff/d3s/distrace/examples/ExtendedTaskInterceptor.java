package cz.cuni.mff.d3s.distrace.examples;


import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class ExtendedTaskInterceptor implements Serializable{

    private String param;

    public ExtendedTaskInterceptor(String param){
        this.param = param;
    }
    public String instrument(@SuperCall(serializableProxy=true) Callable<String> value) throws Exception {
            return "Instrumented ("+param+"): " + value.call();
    }
}
