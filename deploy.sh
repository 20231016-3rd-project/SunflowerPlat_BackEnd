EXIST_BLUE=$(docker-compose -p hospital-blue -f docker-compose.blue.yml ps | grep Up)

if [ -z "$EXIST_BLUE" ]; then
    docker-compose -p sunflowerplate-blue -f docker-compose.blue.yml up -d
    BEFORE_COLOR="green"
    AFTER_COLOR="blue"
    BEFORE_PORT=8001
    AFTER_PORT=8000
else
    docker-compose -p sunflowerplate-green -f docker-compose.green.yml up -d
    BEFORE_COLOR="blue"
    AFTER_COLOR="green"
    BEFORE_PORT=8000
    AFTER_PORT=8001
fi

echo "${AFTER_COLOR} server up(port:${AFTER_PORT})"