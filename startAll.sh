#!/usr/bin/env bash
source dockerUtils.sh

ensure-docker-up && \

delete-container-for-image outlier-detection/spark-inbound && \
delete-container-for-image outlier-detection/outlier-web && \
delete-container-for-image outlier-detection/mongo-custom && \
delete-container-for-image outlier-detection/python-producer && \
delete-container-for-image outlier-detection/kafka-initializer && \

docker-compose up --force-recreate