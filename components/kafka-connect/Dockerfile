



FROM confluentinc/cp-kafka-connect:6.1.0

ENV CONNECT_PLUGIN_PATH='usr/share/java,/usr/share/confluent-hub-components'

#RUN confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:0.2.0

COPY confluentinc-kafka-connect-datagen /usr/share/confluent-hub-components/

COPY kafka-connect-fs /usr/share/java/kafka-connect-fs

VOLUME /tmp

ENTRYPOINT /etc/confluent/docker/run
