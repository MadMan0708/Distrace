#!/usr/bin/env bash
TOPDIR=$(cd `dirname $0`/..;pwd)
source $TOPDIR/bin/prepare-env.sh

PREFIX="cz.cuni.mff.d3s.distrace.examples"
DEFAULT_EXAMPLE="InfiniteLoop"
if [ $1 ]; then
  EXAMPLE=$PREFIX.$1
  shift
else
  EXAMPLE=$PREFIX.$DEFAULT_EXAMPLE
fi

IPC_FILE="file.ipc"
LOG_DIR="logs"

# remove previous logs
rm -rf $LOG_DIR
echo
echo "Running example: $EXAMPLE"
echo

# Attach the agent library and start it together with the start of the application
java -agentpath:"$AGENT_LIB_FILE=log_dir=$LOG_DIR;log_level=info;instrumentor_jar=$INSTRUMENTOR_LIB_FILE;sock_address=ipc://$IPC_FILE" -cp $EXAMPLES_JAR_FILE $EXAMPLE


pids=$(jps -l | grep "distrace-core-instrumenter-0.0.0-all.jar" | cut -d" " -f1)

for pid in $pids
do
 kill -9 $pid
done

rm -rf $IPC_FILE
