package cz.cuni.mff.d3s.distrace.examples;

/**
 * Main example class on which we show how to instrument callbacks
 */
public class Main {

    public static void main(String[] args) {
        Executor executor = new Executor();
        Callback callbackA = CallbackCreator.createCallback("Callback A");
        Callback callbackB = CallbackCreator.createCallback("Callback B");

        // artificial delay before we process the call back
        try {
            Thread.sleep(1111);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Task taskA = new Task(callbackA);
        Task taskB = new Task(callbackB);

        executor.submitTask(taskA);
        executor.submitTask(taskB);

        executor.stop();
        System.exit(0);
    }
}
