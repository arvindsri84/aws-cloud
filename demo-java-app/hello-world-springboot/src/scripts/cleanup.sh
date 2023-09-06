#!/bin/bash

# Script responsible for cleaning up the setup, so that new version can be deployed
export APP_HOME=/zyvika/app

# stop the running java process
PID=`ps -eaf | grep "java" | grep "hello-world" | awk '{print $2}'`
if [[ -z "$PID" ]];
then
  echo "Application is not running!"
else
  kill -9 $PID
fi

# delete the content of APP home
rm -rf ${APP_HOME}