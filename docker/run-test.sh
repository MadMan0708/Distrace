#!/usr/bin/env bash

docker-compose pull
EXAMPLE_NAME=${1:-None} docker-compose up distrace