#!/usr/bin/env bash
# This shell script is used to start DependencyInstrTest

TOPDIR=$(cd `dirname $0`/../..;pwd)
source $TOPDIR/bin/prepare-env.sh
TESTDIR=$(cd `dirname $0`;pwd)

EXAMPLE_NAME=$(basename $TESTDIR)

SERVER_JAR_NAME="distrace-examples-$EXAMPLE_NAME-agent-$VERSION-all.jar"
SERVER_JAR_PATH="$TOPDIR/examples/$EXAMPLE_NAME/agent/build/libs/$SERVER_JAR_NAME"

APP_JAR_NAME="distrace-examples-$EXAMPLE_NAME-app-$VERSION.jar"
APP_JAR_PATH="$TOPDIR/examples/$EXAMPLE_NAME/app/build/libs/$APP_JAR_NAME"


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

INSTRUMENTOR_MAIN_CLASS="cz.cuni.mff.d3s.distrace.examples.Starter"
LOG_DIR="logs"
LOG_LEVEL="info"
CONNECTION_STR="ipc"
INSTRUMETOR_CP=""

echo
echo "Using following agent arguments:"
echo
echo "Instrumentor main class : $INSTRUMENTOR_MAIN_CLASS"
echo "Socket address          : $CONNECTION_STR"
echo "Log dir                 : $LOG_DIR"
echo "Log level               : $LOG_LEVEL"
echo "Extra server classpath  : $INSTRUMETOR_CP"
echo


AGENT_ARGS="$NATIVE_AGENT_LIB_PATH=instrumentor_server_cp=$INSTRUMETOR_CP;log_dir=$LOG_DIR;log_level=$LOG_LEVEL;instrumentor_server_jar=$SERVER_JAR_PATH;instrumentor_main_class=$INSTRUMENTOR_MAIN_CLASS;connection_str=$CONNECTION_STR"

java -agentpath:$AGENT_ARGS -jar $APP_JAR_PATH
