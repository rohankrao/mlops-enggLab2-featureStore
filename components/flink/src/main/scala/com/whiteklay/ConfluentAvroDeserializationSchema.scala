package com.whiteklay

import io.confluent.kafka.schemaregistry.client.{CachedSchemaRegistryClient, SchemaRegistryClient}
import io.confluent.kafka.serializers.KafkaAvroDecoder
import org.apache.avro.generic.GenericRecord
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor


class ConfluentAvroDeserializationSchema(val schemaRegistryUrl: String, val identityMapCapacity: Int) extends DeserializationSchema[GenericRecord] {
  private var kafkaAvroDecoder: KafkaAvroDecoder = null

  def this(schemaRegistyUrl: String) {
    this(schemaRegistyUrl, 1000)
  }

  override def deserialize(bytes: Array[Byte]): GenericRecord = {
    if (kafkaAvroDecoder == null) {
      val schemaRegistry = new CachedSchemaRegistryClient(this.schemaRegistryUrl, this.identityMapCapacity)
      this.kafkaAvroDecoder = new KafkaAvroDecoder(schemaRegistry)
    }
    this.kafkaAvroDecoder.fromBytes(bytes).asInstanceOf[GenericRecord]
  }

  override def isEndOfStream(string: GenericRecord) = false

  override def getProducedType: TypeInformation[GenericRecord] = TypeExtractor.getForClass(classOf[GenericRecord])
}
