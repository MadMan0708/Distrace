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
    echo "Example name is an expected argument!"
    printExamples
    exit
fi

EXAMPLE_DIR=$TOPDIR/examples/$1
if [ -d $EXAMPLE_DIR ]; then
    cd $EXAMPLE_DIR && ./test.sh
else
    echo "Example $1 doesn't exist"
    printExamples
fi

