#!/usr/bin/env bash

# This script expects TOPDIR and EXAMPLE_NAME to be set

# Version of this distribution
VERSION=$( cat $TOPDIR/gradle.properties | grep version | grep -v '#' | sed -e "s/.*=//" )

ATTACHER_JAR_NAME="distrace-agent-attacher-$VERSION.jar"
ATTACHER_JAR_FILE="$TOPDIR/agent-attacher/build/libs/$ATTACHER_JAR_NAME"

NATIVE_AGENT_LIB_DIR="$TOPDIR/agent/build"

NATIVE_AGENT_LIB_NAME=`basename $(ls $NATIVE_AGENT_LIB_DIR/lib-distrace-agent.*)`
NATIVE_AGENT_LIB_PATH="$NATIVE_AGENT_LIB_DIR/$NATIVE_AGENT_LIB_NAME"


SERVER_JAR_NAME="distrace-examples-$EXAMPLE_NAME-agent-$VERSION-all.jar"
SERVER_JAR_PATH="$TOPDIR/examples/$EXAMPLE_NAME/agent/build/libs/$SERVER_JAR_NAME"

APP_JAR_NAME_PREFIX="distrace-examples-$EXAMPLE_NAME-app-$VERSION"
APP_JAR_PATH_PREFIX="$TOPDIR/examples/$EXAMPLE_NAME/app/build/libs/$APP_JAR_NAME_PREFIX"

if [ -f "$APP_JAR_PATH_PREFIX-all.jar" ]
then
    APP_JAR_PATH="$APP_JAR_PATH_PREFIX-all.jar"
else
    APP_JAR_PATH="$APP_JAR_PATH_PREFIX.jar"
fi

TESTDIR=$TOPDIR/examples/$EXAMPLE_NAME

echo
echo "Top project dir is    : $TOPDIR"
echo "Test location is      : $TESTDIR"
echo "Example name          : $EXAMPLE_NAME"
echo "Project version       : $VERSION"
echo "Native agent library  : $NATIVE_AGENT_LIB_PATH"
echo "Server JAR            : $SERVER_JAR_PATH"
echo "App JAR               : $APP_JAR_PATH"
echo "Instrumentor lib JAR  : $SERVER_JAR_PATH"
echo

# For testing purposes we always expect main class to be this one
INSTRUMENTOR_MAIN_CLASS="cz.cuni.mff.d3s.distrace.examples.Starter"
LOG_DIR="logs"
LOG_LEVEL="error"
CONNECTION_STR="ipc"
INSTRUMETOR_CP="$APP_JAR_PATH"
SAVER="directZipkin(${ZIPKIN_IP:-localhost}:9411)"

echo
echo "Using following agent arguments:"
echo
echo "Instrumentor main class : $INSTRUMENTOR_MAIN_CLASS"
echo "Socket address          : $CONNECTION_STR"
echo "Log dir                 : $LOG_DIR"
echo "Log level               : $LOG_LEVEL"
echo "Extra server classpath  : $INSTRUMETOR_CP"
echo "Saver type              : $SAVER"
echo

# Remove previous logs
rm -rf $LOG_DIR