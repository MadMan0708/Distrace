package com.distrace;


import com.sun.tools.attach.VirtualMachine;

class Attacher {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Wrong number of arguments! " +
                    "1) PID of process to which attach the Agent" +
                    "2) Path to the Agent Library");
            System.exit(0);
        }
        String pid = args[0];
        String pathToAgentLib = args[1];
        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgentPath(pathToAgentLib);
            vm.detach();
            System.out.println("Attached to target JVM and loaded Java Agent successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}