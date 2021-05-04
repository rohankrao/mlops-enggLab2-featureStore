//package com.whiteklay
//
//import com.google.protobuf.DynamicMessage
//import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
//import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
//import io.confluent.kafka.serializers.protobuf.{KafkaProtobufDeserializer, KafkaProtobufDeserializerConfig}
//import org.apache.flink.api.common.typeinfo.TypeInformation
//import org.apache.flink.api.java.typeutils.TypeExtractor
//import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
//import org.apache.flink.streaming.util.serialization.KeyedDeserializationSchema
//import org.apache.kafka.clients.consumer.ConsumerRecord
//
//import java.util
//
//class KafkaGenericProtoDeserializationSchema2(val registryUrl: String) extends KeyedDeserializationSchema[DynamicMessage] {
//  private var inner: KafkaProtobufDeserializer[DynamicMessage] = null
//
//  private def checkInitialized(): Unit = {
//    if (inner == null) {
//      val props = new util.HashMap[String, String]
//      props.put(KafkaProtobufDeserializerConfig.DERIVE_TYPE_CONFIG, "true")
//      props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, registryUrl);
//      val client = new CachedSchemaRegistryClient(registryUrl,  AbstractKafkaSchemaSerDeConfig.MAX_SCHEMAS_PER_SUBJECT_DEFAULT)
//      inner = new KafkaProtobufDeserializer(client)
//      inner.configure(props, false)
//    }
//  }
//
//  override def deserialize(messageKey: Array[Byte], message: Array[Byte], topic: String, partition: Int, offset: Long): DynamicMessage = {
//    checkInitialized()
//    inner.deserialize(topic, message).asInstanceOf[DynamicMessage]
//  }
//
//  override def isEndOfStream(nextElement: DynamicMessage): Boolean = false
//
//  override def getProducedType: TypeInformation[DynamicMessage] = TypeExtractor.getForClass(classOf[DynamicMessage])
//}
