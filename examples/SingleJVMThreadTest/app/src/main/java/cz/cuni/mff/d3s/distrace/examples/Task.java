package cz.cuni.mff.d3s.distrace.examples;

/**
 * Abstract Task
 */
public class Task extends Thread {

    public String taskName;
    public Task(String taskName) {
        this.taskName = taskName;
    }

    public void run() {
        System.out.println("Task: " +taskName + ", run method called, " + "trace id = " + Thread.currentThread().getId() + " was called");
    }
}
