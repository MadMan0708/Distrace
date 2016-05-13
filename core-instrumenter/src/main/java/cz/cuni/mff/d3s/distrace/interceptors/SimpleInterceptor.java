package cz.cuni.mff.d3s.distrace.interceptors;

import net.bytebuddy.asm.Advice;

/**
 * Created by kuba on 01/04/16.
 */
public class SimpleInterceptor {
    public static void print(){
        System.out.println("Method print was instrumented ");
    }
}
