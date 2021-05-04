/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package community.mlops;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Properties;

/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class StreamingJob {

	public static void main(String[] args) throws Exception {
		// set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

//		String broker = "kafka.cluster.local:31090";
		String broker = "kafka.cluster:31090";
		FlinkKafkaConsumer<ConsumerRecord> flinkKafkaConsumer = createStringConsumerForTopic("elab_raw", broker, "flink"+System.currentTimeMillis());
//		FlinkKafkaConsumer<String> flinkKafkaConsumer = createStringConsumerForTopic("view_25", "localhost:9092", "flink"+System.currentTimeMillis());
		flinkKafkaConsumer.setStartFromEarliest();

		DataStream<ConsumerRecord> stream = env.addSource(flinkKafkaConsumer);
//		DataStream<String> stream = env.addSource(flinkKafkaConsumer);

//		stream.keyBy(x->x.offset());
//		DataStream<String> stream = env.addSource(flinkKafkaConsumer);


		stream.print();

		// execute program
//		env.execute("Flink Streaming Java API Skeleton");
		env.execute();

	}

//	public static FlinkKafkaConsumer<String> createStringConsumerForTopic(
//			String topic, String kafkaAddress, String kafkaGroup) {
//
//		Properties props = new Properties();
//		props.setProperty("bootstrap.servers", kafkaAddress);
//		props.setProperty("group.id",kafkaGroup);
//
//		return new FlinkKafkaConsumer<String>(
//				topic, new SimpleStringSchema(), props);
//	}

	public static FlinkKafkaConsumer<ConsumerRecord> createStringConsumerForTopic(
			String topic, String kafkaAddress, String kafkaGroup) {

		Properties props = new Properties();
		props.setProperty("bootstrap.servers", kafkaAddress);
		props.setProperty("group.id",kafkaGroup);

		//		String schemaUrl = "http://34.122.3.223:30081";
		String schemaUrl = "http://192.168.49.2:30081";

		return new FlinkKafkaConsumer<>(
				topic, new KafkaGenericAvroDeserializationSchema(schemaUrl), props);
	}

}
