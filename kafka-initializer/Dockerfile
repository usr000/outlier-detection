FROM spotify/kafka 

#RUN bash -c 'apt-get update && apt-get --assume-yes install supervisor'
#RUN mkdir -p /var/log/supervisor

COPY kafka-initializer/initialize.sh  /initialize.sh

#COPY kafka-initializer/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

ENTRYPOINT ["/initialize.sh"]