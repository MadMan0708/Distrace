package com.distrace.examples;

/**
 *
 */
public class InfiniteLoop {
    public static void main(String[] args){
        while(true) {
            try {
                randomSleep();
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
