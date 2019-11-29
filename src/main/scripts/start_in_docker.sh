#!/bin/bash

#project_name
PROJECT_NAME=project-base
#操作/项目路径(Dockerfile存放的路劲)
BASE_PATH=/data/work/project-base
# 源jar路径
SOURCE_PATH=/var/lib/jenkins/workspace/project-base
JAR_PATH=$SOURCE_PATH/target
#容器id
CID=$(docker ps | grep "$PROJECT_NAME" | awk '{print $1}')
#jvm opts
JVM_OPTS='-Xms1024M -Xmx1024M -XX:+UseG1GC -XX:+PrintGCDetails -Dspring.profiles.active=pro'

DATE=`date +%Y%m%d%H%M`
 
# 构建前
function before(){
  # 复制Dockerfile,start脚本
  cp $SOURCE_PATH/src/main/scripts/* $BASE_PATH/scripts

	# 删除原jar
	if [ -f "$BASE_PATH/$PROJECT_NAME-1.0-SNAPSHOT.jar" ]; then
        	echo "删除原jar包..."
                sudo rm -rf $BASE_PATH/$PROJECT_NAME-1.0-SNAPSHOT.jar
	        echo "删除jar包完成"
        fi
	
	# 最新构建代码复制到项目环境
	echo "最新构建代码 $JAR_PATH/$PROJECT_NAME-1.0-SNAPSHOT.jar 复制至 $BASE_PATH ...."
        cp $JAR_PATH/$PROJECT_NAME-1.0-SNAPSHOT.jar $BASE_PATH
	echo "完成复制"
}
 
# 构建docker镜像
function build(){
	echo "开始构建镜像..."
	cd $BASE_PATH
	docker build -t $PROJECT_NAME -f scripts/Dockerfile .
}
 
# 运行docker容器
function run(){
	before
	build
	if [ -n "$CID" ]; then
		echo "存在容器，CID=$CID,移除原容器..."
			docker stop $CID
			docker rm $CID	
		echo "完成移除容器"
	fi
	startdocker
}

#启动容器
function startdocker(){
	echo "docker run 容器..."
	docker run -d -p 8090:8080 -e JAVA_OPTS="$JVM_OPTS" --cap-add=SYS_PTRACE -v $BASE_PATH/logs:/logs/ $PROJECT_NAME
	echo "容器创建完成"
}

# run
run