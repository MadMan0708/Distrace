# Distrace
Monitoring Tool for Distributed Java Applications


To build the core library and custom instrumentation libraries ( so far just H2O)

run 

```
./gradlew clean build
```

To start the example with the agent attached prior the application start run:

```
./bin/run_with_agent.sh EXAMPLE_NAME
```

To start the example and attach the agent at runtime:

```
./bin/run_without_agent.sh EXAMPLE_NAME # starts the application
./bin/attach_agent_to_example.sh EXAMPLE_NAME # attach
```

So far only available examples are: InfiniteLoop