//package com.whiteklay
//
//import com.google.protobuf.DynamicMessage
//import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
//import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
//import io.confluent.kafka.serializers.protobuf.{KafkaProtobufDeserializer, KafkaProtobufDeserializerConfig}
//import org.apache.flink.api.common.typeinfo.TypeInformation
//import org.apache.flink.api.java.typeutils.TypeExtractor
//import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
//import org.apache.kafka.clients.consumer.ConsumerRecord
//
//import java.util
//
//class KafkaGenericProtoDeserializationSchema(val registryUrl: String) extends KafkaDeserializationSchema[DynamicMessage] {
//  private var inner: KafkaProtobufDeserializer[DynamicMessage] = null
//
//    def getProducedType: TypeInformation[DynamicMessage] = TypeExtractor.getForClass(classOf[DynamicMessage])
//
//
//
//  private def checkInitialized(): Unit = {
//    if (inner == null) {
//      val props = new util.HashMap[String, String]
//      props.put(KafkaProtobufDeserializerConfig.DERIVE_TYPE_CONFIG, "true")
//      props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, registryUrl);
//      val client = new CachedSchemaRegistryClient(registryUrl,  AbstractKafkaSchemaSerDeConfig.MAX_SCHEMAS_PER_SUBJECT_DEFAULT)
//      inner = new KafkaProtobufDeserializer(client, props)
//    }
//  }
//
//  override def isEndOfStream(nextElement: DynamicMessage): Boolean = false
//
//  override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]]): DynamicMessage = {
//    checkInitialized()
//    inner.deserialize(record.topic(), record.value()).asInstanceOf[DynamicMessage]
//  }
//}
