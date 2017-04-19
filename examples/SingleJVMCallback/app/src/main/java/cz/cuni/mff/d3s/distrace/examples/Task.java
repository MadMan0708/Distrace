package cz.cuni.mff.d3s.distrace.examples;


public class Task implements Runnable {

    private Callback callback;

    public Task(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        // do some work and then call the callback
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        callback.call();
    }
}
