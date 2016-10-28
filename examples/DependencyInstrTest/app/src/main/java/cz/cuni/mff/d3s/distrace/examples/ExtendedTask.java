package cz.cuni.mff.d3s.distrace.examples;


public class ExtendedTask{

    public static void aa(BaseTask b){

    }
    @Override
    public String toString() {
        System.out.println(new Dummy().toString());
        return "Extended task - super()'s value:  " + super.toString();
    }
}
