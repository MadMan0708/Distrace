package cz.cuni.mff.d3s.distrace.examples;


public class ExtendedTask{

    @Override
    public String toString() {
        return "Extended task - super()'s value:  " + super.toString();
    }
}
