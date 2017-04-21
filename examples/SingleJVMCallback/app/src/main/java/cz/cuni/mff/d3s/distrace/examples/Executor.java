package cz.cuni.mff.d3s.distrace.examples;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple task executor
 */
public class Executor {
    private ExecutorService executorService;

    public Executor(){
        executorService = Executors.newCachedThreadPool();
    }

    public void submitTask(Runnable task){
        executorService.submit(task);
    }

    public void stop(){
        executorService.shutdown();
    }
}
