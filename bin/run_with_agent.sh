#!/usr/bin/env bash
TOPDIR=$(cd `dirname $0`/..;pwd)
source $TOPDIR/bin/prepare-env.sh

# Each example is in package cz.cuni.mff.d3s.distrace.examples.<example_name>. Inside this package is always
# main class with the name <example_name> which is main starting point for the example and class named "Starter"
# which starts the Instrumentor for the given example

PREFIX="cz.cuni.mff.d3s.distrace.examples"

# Get the example name
DEFAULT_EXAMPLE_NAME="SimpleTest"
if [ $1 ]; then
  EXAMPLE_NAME=$1
  shift
else
  EXAMPLE_NAME=$DEFAULT_EXAMPLE_NAME
fi

EXAMPLE_PACKAGE=$PREFIX.$EXAMPLE_NAME
EXAMPLE_CLASS=$EXAMPLE_PACKAGE.$EXAMPLE_NAME


INSTRUMENTOR_LIB_FILE=$EXAMPLES_JAR_FILE
INSTRUMENTOR_MAIN_CLASS="$EXAMPLE_PACKAGE.Starter"
LOG_DIR="logs"
LOG_LEVEL="info"
COMM_TYPE="ipc"

echo
echo "Running example: $EXAMPLE_CLASS"
echo

echo
echo "Using following configuration:"
echo
echo "Instrumentor JAR file:      $INSTRUMENTOR_LIB_FILE"
echo "Instrumentor main class:    $INSTRUMENTOR_MAIN_CLASS"
echo "Socket address:             $COMM_TYPE"
echo "Log dir:                    $LOG_DIR"
echo "Log level:                  $LOG_LEVEL"
echo

# remove previous logs
rm -rf $LOG_DIR

# Attach the agent library and start it together with the start of the application
java -agentpath:"$AGENT_LIB_FILE=log_dir=$LOG_DIR;log_level=$LOG_LEVEL;instrumentor_jar=$INSTRUMENTOR_LIB_FILE;instrumentor_main_class=$INSTRUMENTOR_MAIN_CLASS;comm_type=$COMM_TYPE" -cp $EXAMPLES_JAR_FILE $EXAMPLE_CLASS

# Stop running instrumentor JVM in case of some kind of failure
pids=$(jps -l | grep "distrace-example-apps-0.0.0-all.jar" | cut -d" " -f1)

for pid in $pids
do
 kill -9 $pid
done
