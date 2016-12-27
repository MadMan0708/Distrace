#!/usr/bin/env bash
TOPDIR=$(cd `dirname $0`/..;pwd)

printExamples () {
    echo "Available examples are: "
    echo
    ls $TOPDIR/examples/ | xargs -n 1 basename
    echo
    echo
}

if [ "$#" -le 0 ]; then
    echo "Example name is mandatory argument!"
    printExamples
    exit
fi

# second argument is optional and overrides default ( localhost ) ip
# address on which Zipkin service is running
if [ -n "$2" ]; then
    export ZIPKIN_IP=$2
fi

EXAMPLE_DIR=$TOPDIR/examples/$1
if [ -d $EXAMPLE_DIR ]; then
    cd $EXAMPLE_DIR && ./test.sh
else
    echo "Example $1 doesn't exist"
    printExamples
fi

# this is here in order to keep docker-compose running even after the instrumented program has finished
# in order to be able to see the collected data in zipkin UI
sleep infinity