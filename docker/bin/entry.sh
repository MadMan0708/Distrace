#!/bin/bash

java -jar /opt/distrace/zipkin/zipkin.jar > zipkin.stdout 2>zipkin.stderr &

/bin/bash