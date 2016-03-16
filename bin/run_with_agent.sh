#!/usr/bin/env bash
TOPDIR=$(cd `dirname $0`/..;pwd)
source $TOPDIR/bin/prepare-env.sh


EXAMPLE="com.distrace.examples.$1"
echo $EXAMPLE
# Attach the agent library prior the start of the application we want to monitor
java -javaagent:$AGENT_JAR_FILE -cp $EXAMPLES_JAR_FILE $EXAMPLE