package cz.cuni.mff.d3s.distrace.instrumentation;

/**
 * Various utilities methods to work with stack traces
 */
public class StackTraceUtils {

    public static boolean containsMethodCall(StackTraceElement[] stacktrace, String methodName){
        return numMethodCalls(stacktrace, methodName) > 0;
    }

    public static boolean containsMethodCall(String methodName){
        return containsMethodCall(Thread.currentThread().getStackTrace(), methodName);
    }

    public static int numMethodCalls(StackTraceElement[] stacktrace, String methodName){
        int counter = 0;
        for(StackTraceElement e : stacktrace){
            if(e.toString().contains(methodName)){
                counter++;
            }
        }
        return counter;
    }
    public static int numMethodCalls(String methodName){
        return numMethodCalls(Thread.currentThread().getStackTrace(), methodName);
    }
}
