# 贝尔实验室 Spring 官方推荐镜像 JDK下载地址 https://bell-sw.com/pages/downloads/
FROM bellsoft/liberica-openjdk-rocky:21.0.8-cds
#FROM findepi/graalvm:java17-native

LABEL maintainer="Lion Li"

RUN mkdir -p /unimed/seata-server/logs \
    /unimed/skywalking/agent

WORKDIR /unimed/seata-server

ENV TZ=PRC LANG=C.UTF-8 LC_ALL=C.UTF-8 JAVA_OPTS="" SEATA_IP="" SEATA_PORT=""
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 7091
EXPOSE 8091

ADD ./target/unimed-seata-server.jar ./app.jar

SHELL ["/bin/bash", "-c"]

ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom \
           #-Dskywalking.agent.service_name=unimed-seata-server \
           #-Dskywalking.plugin.seata.server=true \
           #-javaagent:/unimed/skywalking/agent/skywalking-agent.jar \
           ${JAVA_OPTS} -jar app.jar
