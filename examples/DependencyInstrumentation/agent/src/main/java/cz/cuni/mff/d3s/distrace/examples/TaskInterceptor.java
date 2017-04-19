package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.instrumentation.Interceptor;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.util.concurrent.Callable;

import com.google.auto.service.AutoService;

/**
 * Example interceptor
 */
@AutoService(Interceptor.class)
public class TaskInterceptor implements Interceptor {

    private String prefix;

    public TaskInterceptor(String prefix) {
        this.prefix = prefix;
    }

    public String instrument(@SuperCall(serializableProxy = true) Callable<String> value) throws Exception {
        return prefix + " (" + value.call() + ")";
    }
}
