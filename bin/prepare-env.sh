#!/usr/bin/env bash

# Version of this distribution
VERSION=$( cat $TOPDIR/gradle.properties | grep version | grep -v '#' | sed -e "s/.*=//" )

ATTACHER_JAR_NAME="distrace-core-agent-attacher-$VERSION.jar"
ATTACHER_JAR_FILE="$TOPDIR/core-agent-attacher/build/libs/$ATTACHER_JAR_NAME"

NATIVE_AGENT_LIB_NAME="lib-distrace-agent-core.dylib"
NATIVE_AGENT_LIB_PATH="$TOPDIR/core-agent/build/$NATIVE_AGENT_LIB_NAME"