package cz.cuni.mff.d3s.distrace.examples;

import hex.tree.gbm.GBM;
import hex.tree.gbm.GBMModel;
import water.H2O;
import water.H2OApp;
import water.H2ONode;
import water.Key;
import water.fvec.*;
import water.parser.ParseSetup;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

/**
 * This application starts h2o instance which connects to the rest of the cluster
 * and submits an MR task which sums the number
 */
public class MainWithTask {
    public static void main(String[] args) throws IOException {
        // Start h2o node
        H2OApp.main(args);
        // Wait for rest of the cloud, for 10 seconds max
        H2O.waitForCloudSize(1, 20000);
        System.out.println("Loading test data prostate.csv");
        File prostate = new File("data/prostate.csv").getAbsoluteFile();

        System.out.println("Creating frame based on the " + prostate);
        String name = prostate.toURI().toString();
        String baseName = name.substring(name.lastIndexOf('/') + 1);
        Frame frame = water.util.FrameUtils.parseFrame(Key.make(ParseSetup.createHexName(baseName)), prostate.toURI());
        System.out.println("Frame created!");
        printFrameInfo(frame);
        // Start Sum MR task n times

        startTask(frame, 1);

        System.out.println("Finished, check http://localhost:9411 for span visualizations!");
        // Shutdown the cluster once we have the result
        H2O.orderlyShutdown(1000);
        H2O.exit(0);
    }

    private static void startTask(Frame frame, int howManyTimes) {
        for (int i = 0; i < howManyTimes; i++) {
            GBMModel.GBMParameters gbmParams = new GBMModel.GBMParameters();
            gbmParams._train = frame._key;
            gbmParams._response_column = "CAPSULE";
            gbmParams._ntrees = 10;
            GBMModel gbmModel = new GBM(gbmParams).trainModel().get();
            System.out.println("Train Model " + gbmModel);
        }
    }

    private static void printFrameInfo(Frame fr) {
        System.out.println("Number of chunks on frame: " + fr.anyVec().nChunks());
        System.out.println("Frame distributed on nodes:");
        HashSet<H2ONode> uniqueNodes = new HashSet<>();
        for (int i = 0; i < fr.anyVec().nChunks(); i++) {
            H2ONode node = fr.anyVec().chunkKey(i).home_node();
            if (!uniqueNodes.contains(node)) {
                System.out.println(node);
                uniqueNodes.add(node);
            }
        }
        System.out.println();
    }
}
