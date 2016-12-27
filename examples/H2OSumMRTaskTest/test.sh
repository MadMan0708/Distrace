#!/usr/bin/env bash

TOPDIR=$(cd `dirname $0`/../..;pwd)
EXAMPLE_NAME=$(basename `pwd`)
source $TOPDIR/bin/prepare-env.sh

SAVER="cz.cuni.mff.d3s.distrace.examples.H2OSpanSaver(${ZIPKIN_IP:-localhost}:9411)"
AGENT_ARGS="$NATIVE_AGENT_LIB_PATH=instrumentor_server_cp=$INSTRUMETOR_CP;saver=$SAVER;log_dir=$LOG_DIR;log_level=$LOG_LEVEL;instrumentor_server_jar=$SERVER_JAR_PATH;instrumentor_main_class=$INSTRUMENTOR_MAIN_CLASS;connection_str=$CONNECTION_STR"

# First start h2o nodes with agents attached to them
java -agentpath:$AGENT_ARGS -jar $APP_JAR_PATH -name kuba_cluster &
java -agentpath:$AGENT_ARGS -jar $APP_JAR_PATH -name kuba_cluster &

# Start h2o node which starts the MRTask
java -agentpath:$AGENT_ARGS -cp $APP_JAR_PATH "cz.cuni.mff.d3s.distrace.examples.MainWithTask" -name kuba_cluster