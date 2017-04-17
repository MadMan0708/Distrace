#!/usr/bin/env bash

TOPDIR=$(cd `dirname $0`/../..;pwd)
EXAMPLE_NAME=$(basename `pwd`)
source $TOPDIR/bin/prepare-env.sh

# set our custom saver type
SAVER="cz.cuni.mff.d3s.distrace.examples.H2OSpanSaver(${ZIPKIN_IP:-localhost}:9411)"

finalize_configuration

# First start h2o nodes with agents attached to them
java -agentpath:"$NATIVE_AGENT_LIB_PATH=$AGENT_ARGS" -jar $APP_JAR_PATH -name test_cluster &
java -agentpath:"$NATIVE_AGENT_LIB_PATH=$AGENT_ARGS" -jar $APP_JAR_PATH -name test_cluster &

# Start h2o node which starts the MRTask
java -agentpath:"$NATIVE_AGENT_LIB_PATH=$AGENT_ARGS" -cp $APP_JAR_PATH "cz.cuni.mff.d3s.distrace.examples.MainWithTask" -name test_cluster