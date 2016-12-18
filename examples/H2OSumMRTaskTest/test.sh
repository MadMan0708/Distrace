#!/usr/bin/env bash

TOPDIR=$(cd `dirname $0`/../..;pwd)
EXAMPLE_NAME=$(basename `pwd`)
source $TOPDIR/bin/prepare-env.sh

# First start h2o nodes with agents attached to them
java -agentpath:$AGENT_ARGS -jar $APP_JAR_PATH -name kuba_cluster &

java -agentpath:$AGENT_ARGS -jar $APP_JAR_PATH -name kuba_cluster &

# Start h2o node which starts the MRTask
java -agentpath:$AGENT_ARGS -cp $APP_JAR_PATH "cz.cuni.mff.d3s.distrace.examples.MainWithTask" -name kuba_cluster