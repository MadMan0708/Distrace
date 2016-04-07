#!/usr/bin/env bash

# Version of this distribution
VERSION=$( cat $TOPDIR/gradle.properties | grep version | grep -v '#' | sed -e "s/.*=//" )
EXAMPLES_JAR="distrace-example-apps-$VERSION.jar"
EXAMPLES_JAR_FILE="$TOPDIR/example-apps/build/libs/$EXAMPLES_JAR"

AGENT="lib-distrace-agent-core.dylib"
AGENT_FILE="$TOPDIR/agent-core/build/$AGENT"