package cz.cuni.mff.d3s.distrace.examples;

/**
 * Created by kuba on 30/08/16.
 */
public class Task implements Runnable {

    private Callback callback;
    public Task(Callback callback){
        this.callback = callback;
    }
    @Override
    public void run() {
        callback.call();
    }
}
