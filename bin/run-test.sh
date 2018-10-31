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

export NRUNS=1

# second argument is optional specifies number of application runs. Default to 1
if [ -n "$2" ]; then
    export NRUNS=$2
fi

EXAMPLE_DIR=$TOPDIR/examples/$1
if [ -d $EXAMPLE_DIR ]; then

    cd $EXAMPLE_DIR && (for i in $(seq 1 $NRUNS); do ./test.sh; done)
    mkdir traces
    name=$(date +%s%3N)
    wget -O traces/${name}.json http://localhost:9411/api/v1/traces/
else
    echo "Example $1 doesn't exist"
    printExamples
fi

