package cz.cuni.mff.d3s.distrace.examples;

/**
 * This class is used to create callbacks
 */
public class CallbackCreator {

    private static Dummy d = new Dummy();
    public static  Callback createCallback(final String callbackMsg){
        return new Callback() {
            @Override
            public void call() {
                System.out.println("Message from the callback: " + callbackMsg);
            }
        };
    }
}
