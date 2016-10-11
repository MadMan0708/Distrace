package cz.cuni.mff.d3s.distrace.examples;

import net.bytebuddy.asm.Advice;


public class BaseTaskAdvice {
    @Advice.OnMethodExit
    public static String instrument(@Advice.Return String value) {
        return "Instrumented by Base" + value;
    }
}
