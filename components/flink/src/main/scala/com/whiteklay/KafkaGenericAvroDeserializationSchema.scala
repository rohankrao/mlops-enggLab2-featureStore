package com.whiteklay

import io.confluent.kafka.schemaregistry.client.{CachedSchemaRegistryClient, SchemaRegistryClient}
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializer, KafkaAvroDeserializerConfig}
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.formats.avro.typeutils.GenericRecordAvroTypeInfo
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
import org.apache.kafka.clients.consumer.ConsumerRecord

import java.util
import java.util.{HashMap, Map}

class KafkaGenericAvroDeserializationSchema(val registryUrl: String) extends KafkaDeserializationSchema[ConsumerRecord[AnyRef, AnyRef]] {
  @transient var inner: KafkaAvroDeserializer = null

  def getProducedType: TypeInformation[ConsumerRecord[AnyRef, AnyRef]] = TypeExtractor.getForClass(classOf[ConsumerRecord[AnyRef, AnyRef]])
//  def getProducedType: TypeInformation[GenericRecord] = new GenericRecordAvroTypeInfo()
//  def getProducedType: TypeInformation[GenericRecord] = new GenericRecordAvroTypeInfo(this.schema).asInstanceOf[TypeInformation[GenericRecord]]



  private def checkInitialized(): Unit = {
    if (inner == null) {
      val props = new util.HashMap[String, String]
      props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, registryUrl)
      props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "false")
      val client = new CachedSchemaRegistryClient(registryUrl, AbstractKafkaAvroSerDeConfig.MAX_SCHEMAS_PER_SUBJECT_DEFAULT)
      inner = new KafkaAvroDeserializer(client, props)
    }
  }

  override def isEndOfStream(nextElement: ConsumerRecord[AnyRef, AnyRef]): Boolean = false

  override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]]): ConsumerRecord[AnyRef, AnyRef] = {
    checkInitialized()
    new ConsumerRecord[AnyRef, AnyRef](record.topic, record.partition, record.offset, record.timestamp, record.timestampType, record.checksum, record.serializedKeySize, record.serializedValueSize, inner.deserialize(record.topic, record.key), inner.deserialize(record.topic, record.value))

//    inner.deserialize(record.topic(), record.value()).asInstanceOf[GenericRecord]
  }
}
