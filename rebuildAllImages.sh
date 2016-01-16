#!/usr/bin/env bash

function mvn-there() {
  DIR="$1"
  shift
  (cd $DIR; mvn "$@")     
} 

source dockerUtils.sh

ensure-docker-up && \

delete-container-and-image outlier-detection/spark-inbound && \
delete-container-and-image outlier-detection/outlier-web && \
delete-container-and-image outlier-detection/mongo-custom && \
delete-container-and-image outlier-detection/python-producer && \
delete-container-and-image outlier-detection/kafka-initializer && \
 
mvn-there ./spark-inbound clean compile package && \
mvn-there ./outlier-web clean compile package && \

docker build -t outlier-detection/spark-inbound -f ./spark-inbound/docker/Dockerfile . && \
docker build -t outlier-detection/outlier-web -f ./outlier-web/docker/Dockerfile . && \

docker build -t outlier-detection/mongo-custom -f ./mongo-custom/Dockerfile . && \
docker build -t outlier-detection/python-producer -f ./python-producer/Dockerfile . && \
docker build -t outlier-detection/kafka-initializer -f ./kafka-initializer/Dockerfile .