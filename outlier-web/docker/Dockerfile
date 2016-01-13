FROM java:8

#WORKDIR .

COPY outlier-web/target/outlier-web-0.0.1-SNAPSHOT.jar  /outlier/outlier-web.jar

RUN bash -c 'touch /outlier/outlier-web.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/outlier/outlier-web.jar"]	
