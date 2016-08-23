package cz.cuni.mff.d3s.distrace.examples;

/**
 * Dependant tasks started by Starter task.
 *
 * This task should have same trace id as the started task
 */
public class DependantTask extends Task{

    public DependantTask(String taskName) {
        super(taskName);
    }

}