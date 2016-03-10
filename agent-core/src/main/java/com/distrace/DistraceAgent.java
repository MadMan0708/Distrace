package com.distrace;

import com.distrace.transformers.CustomTransformer;
import com.sun.tools.attach.VirtualMachine;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

class DistraceAgent {
    private static Instrumentation instrumentation;
    public static void premain(String args, Instrumentation inst) {
        System.out.println("Attached to JVM prior its start");
        // registers the transformer
        instrumentation = inst;
        inst.addTransformer(new CustomTransformer());
    }

    public static void main(String[] args){
        if(args.length!=1){
            System.out.println("Wrong number of arguments! PID of process to which attach the agent must be specified");
            System.exit(0);
        }

        DistraceAgent.initialize(args[0]);
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
        System.out.println("Dynamically attaching to running JVM");
        instrumentation = inst;
        instrumentation.addTransformer(new CustomTransformer());
    }

    /**
     * Programmatic hook to dynamically load javaagent at runtime.
     */
    public static void initialize(String pid) {
        if (instrumentation == null) {
            DistraceAgent.loadAgent(pid);
        }
    }

    /**
     * Returns the PID of process to which we should attach the agent
     * @return
     */
    public static String getPID(){
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        return nameOfRunningVM.substring(0, p);
    }

    private static final String jarFilePath = "/Users/kuba/IdeaProjects/Agent/out/artifacts/Agent_jar/Agent.jar";

    public static void loadAgent(String pid) {

        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(jarFilePath, "");
            vm.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}