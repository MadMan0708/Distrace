#!/usr/bin/env bash

java -jar $1 # start the application

# Get list of PIDs to which the application Should attach
# and attach agent on the given set of PIDs