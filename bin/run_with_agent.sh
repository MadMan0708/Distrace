#!/usr/bin/env bash
TOPDIR=$(cd `dirname $0`/..;pwd)
source $TOPDIR/bin/prepare-env.sh

# Each example is in package cz.cuni.mff.d3s.distrace.examples.<example_name>. Inside this package is always
# main class with the name <example_name> which is main starting point for the example and class named "Starter"
# which starts the Instrumentor for the given example

# Get the example name
DEFAULT_EXAMPLE_NAME="SingleJVMThreadTest"
if [ $1 ]; then
  EXAMPLE_NAME=$1
  shift
else
  EXAMPLE_NAME=$DEFAULT_EXAMPLE_NAME
fi

EXAMPLE_AGENT_JAR_NAME="distrace-examples-$EXAMPLE_NAME-agent-$VERSION-all.jar"
EXAMPLE_AGENT_JAR_PATH="$TOPDIR/examples/$EXAMPLE_NAME/agent/build/libs/$EXAMPLE_AGENT_JAR_NAME"

EXAMPLE_APP_JAR_NAME="distrace-examples-$EXAMPLE_NAME-app-$VERSION.jar"
EXAMPLE_APP_JAR_PATH="$TOPDIR/examples/$EXAMPLE_NAME/app/build/libs/$EXAMPLE_APP_JAR_NAME"


# For testing purposes we always expect main class to be this one
INSTRUMENTOR_MAIN_CLASS="cz.cuni.mff.d3s.distrace.examples.Starter"
LOG_DIR="logs"
LOG_LEVEL="off"
COMM_TYPE="ipc"

echo
echo "Running example: $EXAMPLE_NAME"
echo

echo
echo "Using following configuration:"
echo
echo "Example JAR file:           $EXAMPLE_APP_JAR_PATH"
echo "Instrumentor JAR file:      $EXAMPLE_AGENT_JAR_PATH"
echo "Instrumentor main class:    $INSTRUMENTOR_MAIN_CLASS"
echo "Socket address:             $COMM_TYPE"
echo "Log dir:                    $LOG_DIR"
echo "Log level:                  $LOG_LEVEL"
echo

# remove previous logs
rm -rf $LOG_DIR

# Attach the agent library and start it together with the start of the application
java -agentpath:"$NATIVE_AGENT_LIB_PATH=log_dir=$LOG_DIR;log_level=$LOG_LEVEL;instrumentor_jar=$EXAMPLE_AGENT_JAR_PATH;instrumentor_main_class=$INSTRUMENTOR_MAIN_CLASS;comm_type=$COMM_TYPE" -jar $EXAMPLE_APP_JAR_PATH

# Stop running instrumentor JVM in case of some kind of failure
pids=$(jps -l | grep "distrace-examples\|cz.cuni.mff.d3s.distrace.examples.Starter" | cut -d" " -f1)

for pid in $pids
do
 kill -9 $pid
done



