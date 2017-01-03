package cz.cuni.mff.d3s.distrace.examples;

import water.H2O;
import water.H2OApp;
import water.H2ONode;
import water.fvec.Frame;
import water.fvec.Vec;

import java.util.HashSet;

/**
 * This application starts h2o instance which connects to the rest of the cluster
 * and submit a MR task
 */
public class MainWithTask {
    public static void main(String[] args){
        // Start h2o node
        H2OApp.main(args);
        // Wait for rest of the cloud, for 10 seconds max
        H2O.waitForCloudSize(2, 10000);
        // Create frame with numbers we want to count
        Vec numVec = Vec.makeSeq(3, 10000000);
        Frame frame = new Frame(numVec);
        printFrameInfo(frame);

        // Start Sum MR task 3 times
        for(int i = 0; i<3; i++){
            SumMRTask mrTask = new SumMRTask().doAll(frame);
            System.out.println("Computed sum is " + mrTask.getSum());
        }
        
        // Shutdown the cluster once we have the result
        H2O.shutdown(0);
    }

    private static void printFrameInfo(Frame fr){
        System.out.println("Number of chunks on frame: " + fr.anyVec().nChunks());
        System.out.println("Frame distributed on nodes:");
        HashSet<H2ONode> uniqueNodes = new HashSet<>();
        for(int i = 0; i<fr.anyVec().nChunks(); i++){
            H2ONode node = fr.anyVec().chunkKey(i).home_node();
            if(!uniqueNodes.contains(node)){
                System.out.println(node);
                uniqueNodes.add(node);
            }
        }
        System.out.println();
    }
}
