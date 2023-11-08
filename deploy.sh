#1
EXIST_BLUE=$('docker-compose' -p sunflowerplate-blue -f /home/ec2-user/app/docker-compose.blue.yml ps | grep Up)

if [ -z "$EXIST_BLUE" ]; then
    docker-compose -p sunflowerplate-blue -f /home/ec2-user/app/docker-compose.blue.yml up -d
    BEFORE_COLOR="green"
    AFTER_COLOR="blue"
    BEFORE_PORT=8081
    AFTER_PORT=8080
else
    docker-compose -p sunflowerplate-green -f /home/ec2-user/app/docker-compose.green.yml up -d
    BEFORE_COLOR="blue"
    AFTER_COLOR="green"
    BEFORE_PORT=8080
    AFTER_PORT=8081
fi

echo "${AFTER_COLOR} server up(port:${AFTER_PORT})"



# 3
sudo sed -i "s/${BEFORE_PORT}/${AFTER_PORT}/" /etc/nginx/conf.d/service-url.inc
sudo nginx -s reload
echo "Deploy Completed!!"

# 4
echo "$BEFORE_COLOR server down(port:${BEFORE_PORT})"
docker-compose -p sunflowerplate-${BEFORE_COLOR} -f /home/ec2-user/app/docker-compose.${BEFORE_COLOR}.yml down