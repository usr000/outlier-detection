FROM python:2.7.11 

RUN bash -c 'apt-get update && apt-get --assume-yes install supervisor'
RUN mkdir -p /var/log/supervisor

#RUN curl -O http://10.0.0.2/dpkp/kafka-python/archive/v0.9.5.tar.gz  && \
RUN curl -O -L https://github.com/dpkp/kafka-python/archive/v0.9.5.tar.gz && \
	tar zxvf v0.9.5.tar.gz && \
	rm v0.9.5.tar.gz

RUN easy_install six

COPY python-producer/generator.py  /kafka-python-0.9.5/generator.py

COPY python-producer/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

#CMD ["python", "/kafka-python-0.9.5/generator.py"]
CMD ["/usr/bin/supervisord", "-n"]