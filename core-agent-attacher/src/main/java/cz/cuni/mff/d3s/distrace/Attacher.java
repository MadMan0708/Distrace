package cz.cuni.mff.d3s.distrace;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.IOException;

/**
 * This class is used to attach native agent to already running JVM. It requires two arguments:
 * 1) Process ID of the running JVM
 * 2) Path to the native agent library
 *
 * If these two arguments are not set or are not valid, attaching is terminated.
 */
class Attacher {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Wrong number of arguments! " +
                    "1) PID of process to which attach the Agent" +
                    "2) Path to the Agent Library");
            System.exit(-1);
        }
        String pid = args[0];
        String pathToAgentLib = args[1];
        File f = new File(pathToAgentLib);
        if(!f.exists() || f.isDirectory()) {
            System.out.println("The path "+pathToAgentLib+" does not exist!");
            System.exit(-2);
        }
        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgentPath(pathToAgentLib);
            vm.detach();
            System.out.println("Attached to target JVM and loaded Java Agent successfully");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AgentInitializationException e) {
            e.printStackTrace();
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (AgentLoadException e) {
            e.printStackTrace();
        }

    }
}