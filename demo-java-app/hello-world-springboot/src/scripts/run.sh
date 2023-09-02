#!/bin/bash

APP_HOME=/zyvika/app

LOGS_HOME=${APP_HOME}/logs
mkdir -p ${LOGS_HOME}

echo "***** Running the application *******"
java -DLOGS=${LOGS_HOME} -jar ${APP_HOME}/bin/*.jar
