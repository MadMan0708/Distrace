#!/usr/bin/env bash
TOPDIR=$(cd `dirname $0`/..;pwd)

cd $TOPDIR/docker && docker-compose pull
cd $TOPDIR/docker && EXAMPLE_NAME=${1:-None} docker-compose up distrace