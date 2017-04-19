package cz.cuni.mff.d3s.distrace.examples;

/**
 * Task starting {@link DependantTask}
 */
public class StarterTask extends Task {

    public StarterTask(String taskName) {
        super(taskName);
    }

    @Override
    public void run() {
        // start dependant task
        DependantTask dependantTask = new DependantTask(super.taskName + "-Dependant");
        try {
            Thread.sleep(500);
            dependantTask.start();
            dependantTask.join();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }
}


