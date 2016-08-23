package cz.cuni.mff.d3s.distrace.examples;

/**
 * Task starting dependant task
 */
public class StarterTask extends Task {

    public StarterTask(String taskName) {
        super(taskName);
    }

    @Override
    public void run() {
        super.run();
        // start dependant task
        new DependantTask(super.taskName+"-Dependant").start();
    }
}


