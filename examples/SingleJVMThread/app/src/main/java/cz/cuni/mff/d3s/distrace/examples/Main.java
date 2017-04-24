package cz.cuni.mff.d3s.distrace.examples;

/**
 * This test shows the propagation of trace contexts between different tasks.
 */
public class Main {

    public static void main(String[] args) {
        StarterTask A = new StarterTask("A");
        StarterTask B = new StarterTask("B");

        A.start();
        B.start();

        try {
            A.join();
            B.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Finished, check http://localhost:9411 for span visualizations!");
        System.exit(0);
    }
}
