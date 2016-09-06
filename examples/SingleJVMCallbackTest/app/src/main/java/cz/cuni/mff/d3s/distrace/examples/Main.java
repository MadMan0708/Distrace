package cz.cuni.mff.d3s.distrace.examples;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main example class demonstrating how to instrument callbacks
 */
public class Main {

    public static void main(String[] args){
        ExecutorService executorService = Executors.newCachedThreadPool();

        Callback callbackA = CallbackCreator.createCallback("Callback A");
        //Callback callbackB = CallbackCreator.createCallback("Callback B");

        //Task taskA = new Task(callbackA);
        //Task taskB = new Task(callbackB);

        //executorService.execute(taskA);
        //executorService.execute(taskB);


    }
}
