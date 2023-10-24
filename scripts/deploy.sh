#!/bin/bash
cd /home/ec2-user/app

export DOCKER_APP_NAME= sunflowerplate DOCKER_APP_NAME=back-zero-downtime IMAGE_TAG=latest

EXIST_BLUE=$(docker compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml ps | grep Up)

if [ -z "$EXIST_BLUE" ]; then
    echo "blueis is not exist. so make blue container"
    echo "blue up"
    docker compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml up -d
    BEFORE_COMPOSE_COLOR="green"
    AFTER_COMPOSE_COLOR="blue"
    echo "end"
else
    echo "blue is exist. so make green container"
    echo "green up"
    docker compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml up -d
    BEFORE_COMPOSE_COLOR="blue"
    AFTER_COMPOSE_COLOR="green"
fi

sleep 20

EXIST_AFTER=$(docker compose -p ${DOCKER_APP_NAME}-${AFTER_COMPOSE_COLOR} -f docker-compose.${AFTER_COMPOSE_COLOR}.yml ps | grep Up)
if [ -n "$EXIST_AFTER" ]; then

    docker compose -p ${DOCKER_APP_NAME}-${BEFORE_COMPOSE_COLOR} -f docker-compose.${BEFORE_COMPOSE_COLOR}.yml down
    echo "$BEFORE_COMPOSE_COLOR down"
fi