#!/usr/bin/env bash

TOPDIR=$(cd `dirname $0`/../..;pwd)
EXAMPLE_NAME=$(basename `pwd`)
source $TOPDIR/bin/prepare-env.sh

finalize_configuration

java -agentpath:$AGENT_ARGS -jar $APP_JAR_PATH
