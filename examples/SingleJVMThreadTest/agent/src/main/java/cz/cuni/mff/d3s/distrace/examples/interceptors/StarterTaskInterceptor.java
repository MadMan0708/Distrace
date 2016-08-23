package cz.cuni.mff.d3s.distrace.examples.interceptors;

import cz.cuni.mff.d3s.distrace.utils.CodeUtils;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * Interceptor of starter task
 */
public class StarterTaskInterceptor {

    public static void start(@This Thread thread){
        CodeUtils.injectTraceContextOn(thread);
        System.out.println("Method start starter task" + thread.getId() + " was called");
    }

    public static void run(){
        System.out.println("Method run on starter task" + Thread.currentThread().getId() + " was called. Current trace id is "+ CodeUtils.getTraceContext().getTraceId());

    }
}
