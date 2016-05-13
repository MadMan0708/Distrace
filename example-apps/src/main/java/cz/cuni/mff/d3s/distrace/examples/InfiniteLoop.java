package cz.cuni.mff.d3s.distrace.examples;

/**
 *
 */
public class InfiniteLoop {

    public static void main(String[] args){
        new Thread().run();
        while(true) {
            try {
                randomSleep();
                new Test().print();
            } catch (Exception ignored) {
            }
        }

    }

    public static void randomSleep() throws InterruptedException{
            // randomly sleeps between 500ms and 1200s
            long randomSleepDuration = (long) (500 + Math.random() * 700);
            System.out.println("Sleeping for "+randomSleepDuration+" ms");
            Thread.sleep(randomSleepDuration);
    }
}
