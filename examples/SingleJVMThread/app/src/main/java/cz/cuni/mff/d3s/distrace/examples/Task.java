package cz.cuni.mff.d3s.distrace.examples;

/**
 * Abstract Task
 */
public abstract class Task extends Thread {

    String taskName;

    Task(String taskName) {
        super(taskName);
        this.taskName = taskName;
    }
}
