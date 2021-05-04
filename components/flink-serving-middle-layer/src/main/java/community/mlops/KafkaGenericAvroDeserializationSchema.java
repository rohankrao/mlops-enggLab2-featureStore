package community.mlops;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.generic.GenericRecord;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.streaming.util.serialization.KeyedDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.HashMap;
import java.util.Map;

public class KafkaGenericAvroDeserializationSchema implements KafkaDeserializationSchema<ConsumerRecord> {
    private final String registryUrl;
    private transient KafkaAvroDeserializer inner;
    private transient StringDeserializer inner2 = new StringDeserializer();

    public KafkaGenericAvroDeserializationSchema(String registryUrl) {
        this.registryUrl = registryUrl;
    }

//    @Override
//    public ConsumerRecord deserialize(
//            byte[] messageKey, byte[] message, String topic, int partition, long offset) {
//        checkInitialized();
//        return new ConsumerRecord(topic, partition, offset, inner.deserialize(topic, messageKey), inner.deserialize(topic, message));
//    }

    @Override
    public boolean isEndOfStream(ConsumerRecord nextElement) {
        return false;
    }

    @Override
    public ConsumerRecord deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception {
        if (inner == null) {
            Map<String, Object> props = new HashMap<>();
            props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, registryUrl);
            props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, false);
            SchemaRegistryClient client =
                    new CachedSchemaRegistryClient(
                            registryUrl, AbstractKafkaAvroSerDeConfig.MAX_SCHEMAS_PER_SUBJECT_DEFAULT);
            inner = new KafkaAvroDeserializer(client, props);
        }
        return new ConsumerRecord<>(consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.timestamp(), consumerRecord.timestampType(), consumerRecord.checksum(), consumerRecord.serializedKeySize(), consumerRecord.serializedValueSize(), inner.deserialize(consumerRecord.topic(), consumerRecord.key()), inner.deserialize(consumerRecord.topic(), consumerRecord.value()));
    }

    @Override
    public TypeInformation<ConsumerRecord> getProducedType() {
        return TypeExtractor.getForClass(ConsumerRecord.class);
    }

    private void checkInitialized() {
        if (inner == null) {
            Map<String, Object> props = new HashMap<>();
            props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, registryUrl);
            props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, false);
            SchemaRegistryClient client =
                    new CachedSchemaRegistryClient(
                            registryUrl, AbstractKafkaAvroSerDeConfig.MAX_SCHEMAS_PER_SUBJECT_DEFAULT);
            inner = new KafkaAvroDeserializer(client, props);
        }
    }
}
