FROM java:8

#WORKDIR .
#RUN curl -O http://10.0.0.2/dist/spark/spark-1.5.2/spark-1.5.2-bin-hadoop2.6.tgz && \
RUN curl -O http://www.eu.apache.org/dist/spark/spark-1.5.2/spark-1.5.2-bin-hadoop2.6.tgz && \
	tar zxvf spark-1.5.2-bin-hadoop2.6.tgz && \
	rm spark-1.5.2-bin-hadoop2.6.tgz

COPY spark-inbound/target/spark-inbound-0.0.1-SNAPSHOT-allinone.jar  /spark/spark-inbound.jar

RUN bash -c 'touch /spark/spark-inbound.jar'
	
ENTRYPOINT ["spark-1.5.2-bin-hadoop2.6/bin/spark-submit","--class","outlier.detection.spark.JavaKafkaInboundProcessor", \
"--master", "local[*]", "/spark/spark-inbound.jar", "kafka_host:2181","group1", "test", "3"]
