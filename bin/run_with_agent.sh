#!/usr/bin/env bash
TOPDIR=$(cd `dirname $0`/..;pwd)
source $TOPDIR/bin/prepare-env.sh

EXAMPLE="com.distrace.examples.$1"

echo
echo "Running example: $EXAMPLE"
echo "Instrumentor JAR: $INSTRUMENTOR_LIB_FILE"
echo

# Attach the agent library and start it together with the start of the application
java -agentpath:"$AGENT_LIB_FILE=instrumentorJar=$INSTRUMENTOR_LIB_FILE;jvmLib=/Library/Java/JavaVirtualMachines/jdk1.7.0_80.jdk/Contents/Home/jre/lib/libjava.dylib" -cp $EXAMPLES_JAR_FILE $EXAMPLE