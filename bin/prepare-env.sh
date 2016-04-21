#!/usr/bin/env bash

# Version of this distribution
VERSION=$( cat $TOPDIR/gradle.properties | grep version | grep -v '#' | sed -e "s/.*=//" )
EXAMPLES_JAR_NAME="distrace-example-apps-$VERSION.jar"
EXAMPLES_JAR_FILE="$TOPDIR/example-apps/build/libs/$EXAMPLES_JAR_NAME"

ATTACHER_JAR_NAME="distrace-core-agent-attacher-$VERSION.jar"
ATTACHER_JAR_FILE="$TOPDIR/core-agent-attacher/build/libs/$ATTACHER_JAR_NAME"

INSTRUMENTOR_LIB_NAME="distrace-core-instrumenter-$VERSION.jar"
INSTRUMENTOR_LIB_FILE="$TOPDIR/core-instrumenter/build/libs/$INSTRUMENTOR_LIB_NAME"

AGENT_LIB_NAME="lib-distrace-agent-core.dylib"
AGENT_LIB_FILE="$TOPDIR/core-agent/build/$AGENT_LIB_NAME"