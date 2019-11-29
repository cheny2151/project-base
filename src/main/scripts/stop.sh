#!/bin/sh
PROJECT_NAME=project-base
PID=`ps ax | grep -i "$PROJECT_NAME" |grep java | grep -v grep | awk '{print $1}'`
if [ -z "$PID" ]; then
    echo "Service $PROJECT_NAME is no running"
else
     kill -9 ${PID}
     echo "Service $PROJECT_NAME is stop success!"
fi