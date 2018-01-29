# Distrace
Monitoring Tool for Distributed Java Applications

## Basic introduction
This main goal of this project is to monitor cluster applications written in JVM-based languages and provide information
in the context of calls among different computation nodes.


## Structure of the project
The whole project consist 4 sub-projects:

* Core agent - native agent written in C++11
* Agent Attacher - small Java program allowing to attach native agent to already running application
* Instrumentor - Java program which is forked by native agent and instruments the classes sent from the native agent. 
Usually when user needs to add new capabilities to the instrumentation library, the change has to be done in this library. There 
is no need to change the native agent.
* Example applications - Java project consisting of applications used for testing the platform.

The Instrumentor project is aimed to be extensible library on which the developer should built it's own instrumentation
library for particular platform. It hides all the internals of the low level instrumentation
 and the developer just needs to implement Tranformers and Interceptors in order to instrument classes in the desired platform.

## How to build and Develop
Distrace is using Gradle build system. 

To build the project, run
```
./gradlew clean build
```
This commands build all sub-projects including the native agent.

The project structure is created in a way that whole Distrace project can be open in IntelliJ or other IDE which supports
importing projects based on Gradle. The core-agent = native agent sub-project can be opened using CLion or any IDE which
supports CMake.

## How to start
To monitor an application using this platform, the native agent has to be configured as the application's argument
 prior it's start using the following java option:

```
java -agentpath:PATH_TO_AGENT_LIB_FILE -jar javaProgram
```

We can also specify arguments to native agent like this:
```
java -agentpath:PATH_TO_AGENT_LIB_FILE=param1=value1;param2=value2 -jar javaProgram
```

There are several arguments which can be set, the most important ones:

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
`connection_str=ipc`.  Distrace automatically creates the underlying communication mechanism. Also, in this case the
 Instrumentor is automatically started by the native agent when the monitored application is started.
* TCP - network transport via TCP. 
To use TCP, `connection_str` argument has to be set to as `connection_str=IP:port`, where IP and port
specifies a node where the Instrumentor JVM is running. In this case is the native agent expects the Instrumentor to be
already running on the specified node.

For more information about the communication types please visit [http://nanomsg.org](http://nanomsg.org)

### Visualizing collected traces
We use Zipkin Server and Zipkin UI to represent collected traces, please see [https://github.com/openzipkin/zipkin](https://github.com/openzipkin/zipkin)

The Zipkin UI is by default started on the following address - [http://localhost:9411](http://localhost:9411).

## How to start examples
1) Clone this repo.
2) Run: `./docker/run-test.sh EXAMPLE_NAME`
Requirements: Docker and Docker compose. The project is tested on JDK 8 and the correct JDK is already used within the docker image.

The ./run-test.sh script is used to start examples within this docker image. The
only argument to this script is example name.  This script is is actually using docker-compose to start 
to docker containers - one with Distrace and the example and another docker container for Zipkin UI service
where we can see visual output.

In case of testing on windows, please use ./docker/run-test.cmd batch script.

Available examples so far:

* DependencyInstrumentation- simple example demonstrating instrumentation of dependant classes. This example does not have any output to the UI.
* H2OSumMRTask  - bigger example instrumenting and monitoring H2O's MR Tasks.
* SingleJVMCallback - monitoring communication using callbacks.
* SingleJVMThread - monitoring communication between different threads within one app .

The traces are by default sent directly to Zipkin UI available on host machine on http://localhost:9411

## Developing your own instrumentation library

In order to develop custom instrumentation library based on Distrace you need to

1) Build Distrace native agent for your custom platform.

2) Create a empty java project and include distrace-instrumenter as a compile dependency.

3) Code it!
-  In order to be able to use custom interceptors and span exporters, the interceptors have to implement Interceptor interface and
   have to be specified as implementation for Interceptor service in order to be able to load them using our
   ServiceLoader mechanism.
     There are 2 ways how to do it. First is to create META-INF/services entries manually or better way
   is to use AutoService library. This library must be added as a compile dependency into your project and all
   Interceptors have to be annotated with an AutoService annotation. AutoService library will generate all entries in META-INF/services automatically
   for us.
   
-  Create main method in which you start the InstrumentorServer. Please see one of the examples how it's done.
      
-  You can include your application classes as dependency too and use them to build more complex transformers 
   and interceptors. This will also have a positive performance impact as the classes won't need to be send to the instrumentor at runtime from your application. 

5) Package your instrumentor as one fat jar with distrace-instrumenter and all other dependencies.

6) Run your application as
java -agentpath:$AGENT_ARGS -jar app.jar


## Examples with H2O
In examples with H2O, the version can be configured by passing -Ph2oVersion to gradle build command, such as:

First, start Zipkin in a docker service:
```
./docker/start-zipkin.sh
```
Once we have Zipking running, start Distrace in docker:
./docker/bin/run-shell.sh # This command downloads the docker file with all the dependencies
./gradlew build -x check -Ph2oVersion=3.16.0.4 # Build distrace and the examples for desired H2O
./bin/run-test.sh # start demo of your choice. The h2o demos will use the desired version

```
