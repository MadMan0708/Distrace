#!/usr/bin/env bash
TOPDIR=$(cd `dirname $0`/..;pwd)
source $TOPDIR/bin/prepare-env.sh

EXAMPLE="com.distrace.examples.$1"

# Start the example
java -cp $EXAMPLES_JAR_FILE $EXAMPLE
