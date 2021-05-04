package com.whiteklay

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import org.apache.avro.{Schema, Schemas}
import org.apache.avro.generic.GenericRecord
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema
import org.apache.flink.formats.avro.typeutils.GenericRecordAvroTypeInfo
import org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders.ParentFirstClassLoader
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.kafka.clients.consumer.ConsumerRecord

import java.util.Properties

object StreamingJob {
  @throws[Exception]
  def main(args: Array[String]): Unit = { // set up the streaming execution environment
//    val configuration: Configuration = new Configuration()
//    configuration.setString("classloader.resolve-order", "parent-first")

    val env = StreamExecutionEnvironment.getExecutionEnvironment

//    env.getConfig.disableForceKryo()



//    env.configure(new Configuration() ,ParentFirstClassLoader)

//    val flinkKafkaConsumer = createAvroConsumerForTopic("datagen_test", "kafka.cluster.local:31090", "flink" + System.currentTimeMillis)
    val flinkKafkaConsumer = createAvroConsumerForTopic("elab_raw", "kafka.cluster:31090", "flink" + System.currentTimeMillis)


    flinkKafkaConsumer.setStartFromEarliest()

    val stream = env.addSource(flinkKafkaConsumer)
    //		DataStream<String> stream = env.addSource(flinkKafkaConsumer);
    //		DataStream<String> stream2 = env.addSource(flinkKafkaConsumer2);
    //		stream.keyBy(x->x.offset());
//    stream.map((e) =>  e.value.asInstanceOf[Nothing]).keyBy((e) => e.get("user_id")).flatMap(new StreamingJob.Deduplicate).print
    //		https://stackoverflow.com/questions/56897367/filtering-unique-events-in-apache-flink
    stream.print();
    //		stream2.print();
    // execute program
    env.execute("Flink Streaming Sala API Skeleton")
  }

  //	public static FlinkKafkaConsumer<String> createStringConsumerForTopic(
  //			String topic, String kafkaAddress, String kafkaGroup) {
  //
  //		Properties props = new Properties();
  //		props.setProperty("bootstrap.servers", kafkaAddress);
  //		props.setProperty("group.id",kafkaGroup);
  //		return new FlinkKafkaConsumer<String>(
  //				topic, new SimpleStringSchema(), props);
  //	}

//  def createAvroConsumerForTopic(topic: String, kafkaAddress: String, kafkaGroup: String): FlinkKafkaConsumer[GenericRecord] = {
////        val schemaRegistryUrl = "http://34.122.3.223:30081"
//    val schemaRegistryUrl = "http://192.168.49.2:30081"
//
//    val props = new Properties
//        props.setProperty("bootstrap.servers", kafkaAddress)
//        props.setProperty("group.id", kafkaGroup)
//
//    val confluentSchema = scala.io.Source.fromURL(schemaRegistryUrl+"/subjects/" + topic + "-value/versions/latest").mkString
//
//
//    val schemaIndex = confluentSchema.indexOf("schema")
//
//    val schemaString = confluentSchema.substring(schemaIndex + 9, confluentSchema.length - 2)
//
//    val schema = Schema.parse(schemaString.replace("\\\"", "\""))
//
//
//
//    //    val schemaRegistry = new CachedSchemaRegistryClient(schemaRegistryUrl, 100)
////
////        val schemas = schemaRegistry.getLatestSchemaMetadata(topic + "-value").getSchema
////
////        val readerSchema = new Schema.Parser().parse(schemas)
//
//
//        new FlinkKafkaConsumer[GenericRecord](topic, ConfluentRegistryAvroDeserializationSchema.forGeneric(schema, schemaRegistryUrl), props)
//    //    new FlinkKafkaConsumer[GenericRecord](topic, ConfluentRegistryAvroDeserializationSchema.forGeneric(readerSchema, schemaRegistryUrl, 1000), props)
//  }


  def createAvroConsumerForTopic(topic: String, kafkaAddress: String, kafkaGroup: String): FlinkKafkaConsumer[ConsumerRecord[AnyRef, AnyRef]] = {
//    val schemaRegistryUrl = "http://34.122.3.223:30081"
        val schemaRegistryUrl = "http://192.168.49.2:30081"

    val props = new Properties
    props.setProperty("bootstrap.servers", kafkaAddress)
    props.setProperty("group.id", kafkaGroup)
    val schemaRegistry = new CachedSchemaRegistryClient(schemaRegistryUrl, 100)

    val schemas = schemaRegistry.getLatestSchemaMetadata(topic + "-value").getSchema

    val readerSchema = new Schema.Parser().parse(schemas)


    new FlinkKafkaConsumer[ConsumerRecord[AnyRef, AnyRef]](topic, new KafkaGenericAvroDeserializationSchema(schemaRegistryUrl), props)
//    new FlinkKafkaConsumer[GenericRecord](topic, ConfluentRegistryAvroDeserializationSchema.forGeneric(readerSchema, schemaRegistryUrl, 1000), props)
  }

//  def createProtoConsumerForTopic(topic: String, kafkaAddress: String, kafkaGroup: String): FlinkKafkaConsumer[DynamicMessage] = {
//    val schemaRegistryUrl = "http://34.122.3.223:30081"
//    val props = new Properties
//    props.setProperty("bootstrap.servers", kafkaAddress)
//    props.setProperty("group.id", kafkaGroup)
//    val schemaRegistry = new CachedSchemaRegistryClient(schemaRegistryUrl, 100)
//
//    val schemas = schemaRegistry.getLatestSchemaMetadata(topic + "-value").getSchema
//
//    println(schemas)
//
//
//    new FlinkKafkaConsumer[DynamicMessage](topic, new KafkaGenericProtoDeserializationSchema2(schemaRegistryUrl), props)
//  }
}
