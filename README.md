# Outlier Detection Demo

The project shows how to use a simple algorithm to find outliers in readings coming from a topic in Kafka, save in Mongo DB, and then provide a simple REST-ful API to consume data.

Example of input message:
```json
{"publisher": "publisher-1", "readings": [3, 106, 103, 3, 4, 5, 2, 7, 5, 106, 4], "time": "2016-01-04 19:22:35.209"}
```
## Prerequisite

1. Java 8
2. mvn 3.3+
3. docker 1.9.1 & docker-compose 1.5.2, in case of Mac or Win environment also docker-machine 0.5.4
4. free ports as exposed by docker-compose: 9092, 2181, 27017, 8081
5. Servers run in UTC timezone and time format assumes UTC.
6. If using virtual machine for docker, make sure to allocate >=4 processors (needed for Spark), tested with 8. 

## How to try

0. Make sure that docker is available on the command line and all the environment variables are set. On Mac/Win may need to run: 
```sh
eval "$(docker-machine env $(docker-machine active))"
```

1. Run ./rebuildAllImages.sh to rebuild all docker images. Note, that for the first run it may take quite some time, as both Maven and Docker will need to download dependencies.
2. Run ./startAll.sh to start all needed environment. docker-compose will create and link required containers.  
3. After some short period messages should start flowing through the system, and will be visible in the log.
4. To access REST-ful API, please see details below.
5. When finished, press Control+C.

## Moving parts

(Please look at docker-compose.yml to see how dependencies are defined)

0. docker and docker-compose
> for building and managing images and linking them together at run-time

1. kafka_host: Kafka and Zookeeper
> container for both Kafka and Zookeeper, provides queue functionality

2. kafka_initializer: 
> for inializing Kafka with needed topic ('test')
 
3. mongo_host:
> for storing processed messages. DB name: 'outliers', collection: 'output'.

4. inbound:
> Spark streaming process listening on Kafka topic and periodically (2 sec window) writing to MongoDB processed messages with calculated outliers. Please note that although Spark streaimng is used, calculation is done on individual messages.

5. webview:
> provides REST-ful API, uses spring-data-rest

6. python_producer:
> python process which generates and writes messages to a Kafka topic. 	

## REST-ful API

1. to get a list of all publishers:
```sh
curl http://192.168.99.100:8081/publishers
```

sample output:
```json
["publisher-0","publisher-1","publisher-2","publisher-3","publisher-4","publisher-5"]
```

2. find the most recent message for a given publisher:
```sh
curl http://192.168.99.100:8081/outliers/search/findByPublisher?publisher=publisher-1\&sort=time,desc\&page=0\&size=1
```
sample output:
```json
{
  "_embedded" : {
    "output" : [ {
      "publisher" : "publisher-1",
      "time" : "2016-01-05 10:25:59.059",
      "readings" : [ 103.0, 6.0, 104.0, 7.0, 102.0, 7.0, 2.0, 5.0, 3.0, 3.0, 2.0, 6.0, 5.0 ],
      "outliers" : [ {
        "detectionMethod" : "local_MedAbsDev",
        "readings" : [ 103.0, 104.0, 102.0 ]
      } ],
      "stats" : {
        "median" : 6.0,
        "mean" : 27.30769230769231,
        "sd" : 43.18638793143162
      },
      "_links" : {
        "self" : {
          "href" : "http://192.168.99.100:8081/outliers/568b9a3852faff00012e8086"
        },
        "outputMessage" : {
          "href" : "http://192.168.99.100:8081/outliers/568b9a3852faff00012e8086"
        }
      }
    } ]
  },
  "_links" : {
    "first" : {
      "href" : "http://192.168.99.100:8081/outliers/search/findByPublisher?publisher=publisher-1&page=0&size=1&sort=time,desc"
    },
    "self" : {
      "href" : "http://192.168.99.100:8081/outliers/search/findByPublisher?publisher=publisher-1&sort=time,desc&page=0&size=1"
    },
    "next" : {
      "href" : "http://192.168.99.100:8081/outliers/search/findByPublisher?publisher=publisher-1&page=1&size=1&sort=time,desc"
    },
    "last" : {
      "href" : "http://192.168.99.100:8081/outliers/search/findByPublisher?publisher=publisher-1&page=43&size=1&sort=time,desc"
    }
  },
  "page" : {
    "size" : 1,
    "totalElements" : 44,
    "totalPages" : 44,
    "number" : 0
  }

```

> Note: to get recent N messages change 'size' parameter to the desired value.

**192.168.99.100** is the IP address of the docker-vm virtual machine.
To get this address, run 

```sh
docker-machine ip $(docker-machine active)
```

Similarly MongoDB can be accessed at this IP and port 27017. 

## Known Issues

1. In case some container fails to stop, simply restart the virtual machine, e.g:
```sh
docker-machine restart <docker-vm-name>
```
To validate all containers stopped, the below command must return empty list:
```sh
docker ps
```
