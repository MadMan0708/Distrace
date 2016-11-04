package cz.cuni.mff.d3s.distrace.examples;

import water.H2O;
import water.H2OApp;
import water.H2ONode;
import water.fvec.Chunk;
import water.fvec.Frame;
import water.fvec.Vec;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This application starts h2o instance which connects to the rest of the cluster
 * and submit a MR task
 */
public class MainWithTask {
    public static void main(String[] args){
        // Start h2o node
        H2OApp.main(args);
        // Wait for rest of the cloud
        H2O.waitForCloudSize(2,10000);

        // Create frame with numbers we want to count
        Vec numVec = Vec.makeSeq(1,100000);
        Frame frame = new Frame(numVec);
        System.out.println("Number of chunks on frame: " + frame.anyVec().nChunks());
        System.out.println("Frame distributed on nodes:");
        HashSet<H2ONode> uniqueNodes = new HashSet<>();
        for(int i = 0; i<frame.anyVec().nChunks(); i++){
            H2ONode node = frame.anyVec().chunkKey(i).home_node();
            if(!uniqueNodes.contains(node)){
                System.out.println(node);
                uniqueNodes.add(node);
            }
        }
        System.out.println();

        // Start Sum MR task
        SumMRTask mrTask = new SumMRTask().doAll(frame);
        long sum = mrTask.getResult().getFinalSum();
        System.out.println("Computed sum is " + sum);

        // Shutdown the cluster once we have the result
        H2O.shutdown(0);
    }
}
