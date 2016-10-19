package cz.cuni.mff.d3s.distrace.examples;


public class ExtendedTask extends BaseTask{
    private String str = "a";
    @Override
    public String toString() {
        return "Extended task - super()'s value:  " + super.toString();
    }
}
