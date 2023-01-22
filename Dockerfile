FROM azul/zulu-openjdk:17

RUN mkdir -p /usr/abn-pfm-ms

WORKDIR /usr/abn-pfm-ms

ADD ./Docker-Image/abn-pfm-ms-0.0.1-SNAPSHOT.jar /usr/abn-pfm-ms/app.jar

RUN sh -c 'touch /usr/abn-pfm-ms/app.jar'

EXPOSE 8081

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS --enable-preview -jar /usr/abn-pfm-ms/app.jar" ]