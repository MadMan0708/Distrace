package cz.cuni.mff.d3s.distrace.examples;

/**
 * This class is used to create callbacks
 */
public class CallbackCreator {

    private static Dummy a = new Dummy();
    public static  Callback createCallback(final String callbackMsg){
        return new Callback() {

            @Override
            public void call() {
                System.out.println(a.doSomething());
                System.out.println("Message from the callback: " + callbackMsg);
            }
        };
    }
}
