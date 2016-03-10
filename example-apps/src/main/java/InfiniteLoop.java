package com.distrace.examples;

/**
 * Created by kuba on 10/03/16.
 */
public class Main {
    public static void main(String[] args){
        try{
            randomSleep();}
        catch (Exception e){
        }
    }

    public static void randomSleep() throws InterruptedException{
        // randomly sleeps between 500ms and 1200s
        long randomSleepDuration = (long) (500 + Math.random() * 700);
        System.out.printf("Sleeping for %d ms ..\n", randomSleepDuration);
        Thread.sleep(randomSleepDuration);
    }
}
