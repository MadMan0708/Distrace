#!/usr/bin/env bash

# We attach the agent library prior the start of the application we want to monitor
java -javaagent:../agent-core/Agent.jar -jar SimpleApp.jar