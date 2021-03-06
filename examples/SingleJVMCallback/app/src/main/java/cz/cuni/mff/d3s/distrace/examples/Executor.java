package cz.cuni.mff.d3s.distrace.examples;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Simple task executor
 */
public class Executor {
    private ExecutorService executorService;

    public Executor() {
        executorService = Executors.newCachedThreadPool();
    }

    public void submitTask(Runnable task) {
        executorService.submit(task);
    }

    public void stop() {
        try {
            executorService.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
