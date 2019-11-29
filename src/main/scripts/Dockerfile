FROM adoptopenjdk/openjdk11:latest
VOLUME /tmp
ADD ./project-base-1.0-SNAPSHOT.jar project-base.jar
RUN sh -c 'touch /project-base.jar' \
&& ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /project-base.jar" ]