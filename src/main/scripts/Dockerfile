FROM adoptopenjdk/openjdk11:latest
ENV PROJECT_NAME=project-base \
VERSION=1.0-SNAPSHOT
VOLUME /tmp
COPY ./$PROJECT_NAME-$VERSION.jar $PROJECT_NAME.jar
RUN sh -c 'touch /$PROJECT_NAME.jar' \
&& ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /$PROJECT_NAME.jar" ]