#!/bin/sh

#jenkins部署声明
BUILD_ID=dontKillMe
#project_name
PROJECT_NAME=project-base
BASE_PATH=/data/work/project-base
# 源jar路径
SOURCE_PATH=/var/lib/jenkins/workspace/project-base
JAR_PATH=$SOURCE_PATH/target

#jdk1.8
#JVM_OPTS='-Xms717M -Xmx717M -Xmn250M -Xss512K -XX:+DisableExplicitGC -XX:SurvivorRatio=4
#    -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled
#		-XX:LargePageSizeInBytes=128M -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly
#		-XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -Dspring.profiles.active=dev -Dserver.port=8090'
JVM_OPTS='-Xms717M -Xmx717M -XX:+UseG1GC -XX:+PrintGCDetails -Dspring.profiles.active=pro -Dserver.port=8090'

function before(){
  # 复制脚本
  if [ ! -d 'scripts' ]; then
    mkdir scripts
  fi
  cp $SOURCE_PATH/src/main/scripts/* $BASE_PATH/scripts

  # 删除原jar
  if [ -f "$PROJECT_NAME-1.0-SNAPSHOT.jar" ]; then
      echo "删除原jar包..."
          sudo rm -rf ./$PROJECT_NAME-1.0-SNAPSHOT.jar
      echo "删除jar包完成"
  fi

  # 最新构建代码复制到项目环境
  echo "最新构建代码 $JAR_PATH/$PROJECT_NAME-1.0-SNAPSHOT.jar 复制至 $BASE_PATH ...."
      cp $JAR_PATH/$PROJECT_NAME-1.0-SNAPSHOT.jar $BASE_PATH
  echo "完成复制"
}

cd $BASE_PATH
before
if [ ! -d "logs" ]; then
   mkdir logs
fi
PID=`ps ax | grep -i "$PROJECT_NAME" |grep java | grep -v grep | awk '{print $1}'`
if [ -z "$PID" ]; then
    echo "Service $PROJECT_NAME start ..."
    nohup java $JVM_OPTS -jar $PROJECT_NAME-1.0-SNAPSHOT.jar > logs/project-base.out 2>&1 &
    echo "Service $PROJECT_NAME start SUCCESS "
else
    echo "Service $PROJECT_NAME is already start ..."
fi