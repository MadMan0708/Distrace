# Distrace
Monitoring Tool for Distributed Java Applications

## Basic introduction
This main goal of this project is to monitor cluster applications written in JVM-based languages and provide information
in context of calls among different computation nodes.

This project consist of 2 parts - native agent written in C++11 and Instrumentor written in Java.

## Structure of the project
The whole project consist 4 sub-projects at the moment:

* Core agent - native agent written in C++11
* Agent Attacher - small Java program allowing to attach native agent to already running application
* Instrumentor - Java program which is forked by native agent and instruments the classes sent from the native agent. 
Usually when user needs to add new capabilities to the instrumentation library, the change has to be done in this library. There 
is no need to change the native agent.
* Example applications - Java project consisting of applications used for testing the platform.

The Instrumentor Java project act as basic project on which the developer should built it's own instrumentation
library for particular platform. It hides all the internals and the developer just needs to implement Tranformers
and Interceptors in order to instrument classes in the desired platform.

## How to build and Develop
Distrace is using Gradle build system. 

To build the project, run
```
./gradlew clean build
```
This commands build all sub-projects including the native agent.

The project structure is created in a way that whole Distrace project can be open in IntelliJ or other IDE which supports
importing a project based on Gradle. The core-agent = native agent sub-project can be opened using any CLion or anu IDE which
supports CMake.

## How to start
To monitor an application using this platform, the native agent has to be added to the application prior it's start
using this java options: 

```
java -agentpath:PATH_TO_AGENT_LIB_FILE -jar javaProgram
```

We can also specify arguments to native agent like this:
```
java -agentpath:PATH_TO_AGENT_LIB_FILE=param1=value1;param2=value2 -jar javaProgram
```

There are 5 arguments at the moment which can be set:

* log_dir - optional with directory from where the application has been started as default value. It allows us to specify directory
 where logs are generated for both Instrumentor and native agent.
* log_level - optional with "error" as default value.  It allows us to specify logging level for both Instrumentor and native agent. The value can be one
of these: trace, debug, info, warn, error, fatal, off.
* connection_str - optional with "ipc" as default value. This argument is used to specify type of communication used between Instrumentor and native agent.
* instrumentor_jar - mandatory. This argument points to Instrumentor JAR. It is needed since Instrumentor is started from
native agent.
* instrumentor_main_class - mandatory. This argument has to contain class name from where the Instrumentor is started.

### Setting communication type
Currently available types of communication types are:

* IPC - transport between processes on a single machine. To use IPC, `connection_str` argument has to be set to as
`connection_str=ipc`. 
Distrace automatically creates the underlying communication mechanism. In this case the Instrumentor is automatically
started by the native agent when the monitored application is started.
* TCP - network transport via TCP. 
To use TCP, `connection_str` argument has to be set to as `connection_str=IP:port`, where IP and port
specifies node where the Instrumentor JVM is running. In this case is the native agent expects the Instrumentor to be
already running on the specified node.

For more information about the communication types please visit [http://nanomsg.org](http://nanomsg.org)


## How to start examples
There are several scripts which makes starting example programs and attaching agent to it simpler - they set the arguments
and prepare the environment so the test scenarios can be started just using one call in shell.

To start the example with the agent attached prior the application start run:
```
./bin/run_with_agent.sh EXAMPLE_NAME
```

To start the example and attach the agent at runtime:

```
./bin/run_without_agent.sh EXAMPLE_NAME # starts the application
./bin/attach_agent_to_example.sh EXAMPLE_NAME # attach the agent to the application
```

Available examples so far:

* SimpleTest - this examples only creates new instance of class and instrumentor does simple instrumentation. It adds 
additional print statement to the method.