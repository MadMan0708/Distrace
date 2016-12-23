#!/usr/bin/env bash
TOPDIR=$(cd `dirname $0`/..;pwd)

printExamples () {
    echo "Available examples are: "
    echo
    ls $TOPDIR/examples/ | xargs -n 1 basename
    echo
    echo
}

if [ "$#" -ne 1 ]; then
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

