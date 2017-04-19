package cz.cuni.mff.d3s.distrace.examples;

/**
 * Dependant tasks started by {@link StarterTask}.
 * This task should have same trace id when instrumented as the {@link StarterTask}
 */
public class DependantTask extends Task {

    DependantTask(String taskName) {
        super(taskName);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }
}