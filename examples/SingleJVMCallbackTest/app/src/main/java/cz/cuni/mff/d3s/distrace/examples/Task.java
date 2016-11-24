package cz.cuni.mff.d3s.distrace.examples;


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
