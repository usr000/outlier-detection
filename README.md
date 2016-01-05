# Outlier Detection Demo

The project shows how to use a simple algorithm to find outliers in readings coming from a topic in Kafka, save in Mongo DB, and then provide a simple REST-ful API to consume data.
Example of input message:
{"publisher": "publisher-1", "readings": [3, 106, 103, 3, 4, 5, 2, 7, 5, 106, 4], "time": "2016-01-04 19:22:35.209"}

## Prerequisite

1. Java 8
2. mvn 3.3+
3. docker 1.9.1 & docker-compose 1.5.2, in case of Mac or Win environment also docker-machine 0.5.4
4. free ports as exposed by docker-compose: 9092, 2181, 27017, 8081

## How to try

1. Run ./rebuildAllImages.sh to rebuild all docker images. Note, that for the first run it may take quite some time, as both Maven and Docker will need to download dependencies.
2. Run ./startAll.sh to start all needed environment. docker-compose will create and link required containers.  
3. After some short period messages should start flowing through the system, and will be visible in the log.
4. To access REST-ful API, please see details below.

## Moving parts

(Please look at docker-compose.yml to see how dependencies are defined)

0. docker and docker-compose
- for building and managing images and linking them together at run-time

1. kafka_host: Kafka and Zookeeper
- container for both Kafka and Zookeeper, provides queue functionality

2. kafka_initializer: 
- for inializing Kafka with needed topic ('test')
 
3. mongo_host:
- for storing processed messages. DB name: 'outliers', collection: 'output'.

4. inbound:
- Spark streaming process listening on Kafka topic and periodically (2 sec window) writing to MongoDB processed messages with calculated outliers. Please note that although Spark streaimng is used, calculation is done on individual messages.

5. webview:
- provides REST-ful API, uses spring-data-rest

6. python_producer:
- python process which generates and writes messages to a Kafka topic. 	

## REST-ful API

1. to get a list of all publishers:
curl http://192.168.99.100:8081/publishers

2. find the most recent message for a given publisher:
curl http://192.168.99.100:8081/outliers/search/findByPublisher?publisher=publisher-1\&sort=time,desc\&page=0\&size=1

Note: to get recent N messages change 'size' parameter to the desired value.

192.168.99.100 is the IP address of the docker-vm virtual machine.
To get this address, run 
docker-machine ip $(docker-machine active)

Similarly MongoDB can be accessed at this IP and port 27017. 



 