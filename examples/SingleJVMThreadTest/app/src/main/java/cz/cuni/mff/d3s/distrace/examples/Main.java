package cz.cuni.mff.d3s.distrace.examples;

/**
 * This test shows the propagation of trace contexts between different tasks.
 */
public class Main {

    public static void main(String[] args){
        new StarterTask("A").start();
        new StarterTask("B").start();
    }
}
