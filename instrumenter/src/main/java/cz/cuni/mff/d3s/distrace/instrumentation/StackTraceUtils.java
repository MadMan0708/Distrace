package cz.cuni.mff.d3s.distrace.instrumentation;

/**
 * Various utilities methods to work with stack traces
 */
public class StackTraceUtils {

    /**
     * Check whether the given stack trace contains the line with the specified method
     *
     * @param stacktrace stack trace to check
     * @param methodName method name to check
     */
    public static boolean containsMethodCall(StackTraceElement[] stacktrace, String methodName) {
        return numMethodCalls(stacktrace, methodName) > 0;
    }

    /**
     * Check whether the current stack trace contains the line with the specified method
     *
     * @param methodName method name to check
     */
    public static boolean containsMethodCall(String methodName) {
        return containsMethodCall(Thread.currentThread().getStackTrace(), methodName);
    }

    /**
     * Get the number of method calls from the given stack trace
     *
     * @param stacktrace stack trace to check
     * @param methodName method name to check
     */
    public static int numMethodCalls(StackTraceElement[] stacktrace, String methodName) {
        int counter = 0;
        for (StackTraceElement e : stacktrace) {
            if (e.toString().contains(methodName)) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Get the number of method calls from the current stack trace
     *
     * @param methodName method name to check
     */
    public static int numMethodCalls(String methodName) {
        return numMethodCalls(Thread.currentThread().getStackTrace(), methodName);
    }
}
