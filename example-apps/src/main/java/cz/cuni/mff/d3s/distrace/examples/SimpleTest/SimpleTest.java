package cz.cuni.mff.d3s.distrace.examples.SimpleTest;

/**
 * Super Simple Example. The method print on Printer is expected to be instrumented
 * by adding new print statement.
 */
public class SimpleTest {

    public static void main(String[] args){
        new Printer().print();
    }
}
