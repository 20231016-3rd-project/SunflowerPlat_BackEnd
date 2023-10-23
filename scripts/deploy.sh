#!/bin/bash

# 작업 디렉토리를 /home/ec2-user/app으로 변경
cd /home/ec2-user/app

export DOCKER_REGISTRY=bae3007 DOCKER_APP_NAME=back-zero-downtime IMAGE_TAG=latest

EXIST_BLUE=$(docker compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yaml ps | grep Up)

if [ -z "$EXIST_BLUE" ]; then
    echo "blueis is not exist. so make blue container"
    echo "blue up"
    docker compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yaml up -d
    BEFORE_COMPOSE_COLOR="green"
    AFTER_COMPOSE_COLOR="blue"
    echo "end"
else
    echo "blue is exist. so make green container"
    echo "green up"
    docker compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yaml up -d
    BEFORE_COMPOSE_COLOR="blue"
    AFTER_COMPOSE_COLOR="green"
fi

sleep 20

EXIST_AFTER=$(docker compose -p ${DOCKER_APP_NAME}-${AFTER_COMPOSE_COLOR} -f docker-compose.${AFTER_COMPOSE_COLOR}.yaml ps | grep Up)
if [ -n "$EXIST_AFTER" ]; then

    docker compose -p ${DOCKER_APP_NAME}-${BEFORE_COMPOSE_COLOR} -f docker-compose.${BEFORE_COMPOSE_COLOR}.yaml down
    echo "$BEFORE_COMPOSE_COLOR down"
fi