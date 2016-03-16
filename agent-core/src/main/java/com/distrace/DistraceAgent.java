package com.distrace;

import com.distrace.transformers.CustomTransformer;
import com.distrace.utils.Log;
import com.sun.tools.attach.VirtualMachine;

import java.lang.instrument.Instrumentation;
import java.util.Enumeration;
import java.util.Properties;

class DistraceAgent {

    private static Instrumentation instrumentation;

    /**
     * JVM Hook to load agent when specified using -javaagent:PathToAgentJar
     * @param args
     * @param inst
     */
    public static void premain(String args, Instrumentation inst) {
        Log.info("Attached to JVM prior its start");
        registerTransformers(inst);
    }

    /**
     * JVM hook to dynamically load javaagent at runtime.
     *
     * The agent class may have an agentmain method for use when the agent is
     * started after VM startup.
     *
     * @param args
     * @param inst
     * @throws Exception
     */
    public static void agentmain(String args, Instrumentation inst) throws Exception {
        Log.info("Dynamically attaching to running JVM");
        registerTransformers(inst);
    }

    public static void main(String[] args){
        if(args.length!=2){
            Log.info("Wrong number of arguments! " +
                    "1) PID of process to which attach the agent" +
                    "2) Path to the Agent JAR");
            System.exit(0);
        }
        DistraceAgent.initialize(args[0], args[1]);
    }


    public static void registerTransformers(Instrumentation inst){
        instrumentation = inst;
        instrumentation.addTransformer(new CustomTransformer());
    }
    /**
     * Programmatic hook to dynamically load javaagent at runtime.
     */
    public static void initialize(String pid, String pathToAgentJar) {
        if (instrumentation == null) {
            DistraceAgent.loadAgent(pid, pathToAgentJar);
        }
    }

    public static void loadAgent(String pid, String pathToAgentJar) {
        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(pathToAgentJar, "");
            vm.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}