#!/bin/bash

export APP_HOME=/zyvika/app

echo "***** Running the application *******"
java -Dspring.profiles.active=${PROFILE} -jar ${APP_HOME}/bin/*.jar &
