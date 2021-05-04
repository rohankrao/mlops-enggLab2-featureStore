ThisBuild / resolvers ++= Seq(
    "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
    Resolver.mavenLocal
)

name := "flink"

version := "0.1-SNAPSHOT"

organization := "com.whiteklay"

ThisBuild / scalaVersion := "2.12.12"

val flinkVersion = "1.14-SNAPSHOT"

val confluentVersion = "5.3.0"

//val flinkDependencies = Seq(
//  "org.apache.flink" %% "flink-clients" % flinkVersion % "provided",
//  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
//  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided")

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-clients" % flinkVersion,
  "org.apache.flink" %% "flink-scala" % flinkVersion,
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion)

lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= flinkDependencies
  )

assembly / mainClass := Some("com.whiteklay.Job")

// make run command include the provided dependencies
Compile / run  := Defaults.runTask(Compile / fullClasspath,
                                   Compile / run / mainClass,
                                   Compile / run / runner
                                  ).evaluated

// stays inside the sbt console when we press "ctrl-c" while a Flink programme executes with "run" or "runMain"
Compile / run / fork := true
Global / cancelable := true

// exclude Scala library from assembly
assembly / assemblyOption  := (assembly / assemblyOption).value.copy(includeScala = false)

resolvers += "confluent" at "https://packages.confluent.io/maven/"

// https://mvnrepository.com/artifact/org.apache.flink/flink-connector-kafka
libraryDependencies += "org.apache.flink" %% "flink-connector-kafka" % flinkVersion

// https://mvnrepository.com/artifact/org.apache.flink/flink-avro-confluent-registry
libraryDependencies += "org.apache.flink" % "flink-avro-confluent-registry" % flinkVersion

// https://mvnrepository.com/artifact/org.apache.flink/flink-avro
libraryDependencies += "org.apache.flink" % "flink-avro" % flinkVersion


// https://mvnrepository.com/artifact/org.apache.flink/flink-queryable-state-runtime
libraryDependencies += "org.apache.flink" %% "flink-queryable-state-runtime" % flinkVersion

// https://mvnrepository.com/artifact/org.apache.flink/flink-core
libraryDependencies += "org.apache.flink" % "flink-core" % flinkVersion


// https://mvnrepository.com/artifact/io.confluent/kafka-avro-serializer
libraryDependencies += "io.confluent" % "kafka-avro-serializer" % confluentVersion

// https://mvnrepository.com/artifact/io.confluent/common-config
libraryDependencies += "io.confluent" % "common-config" % confluentVersion

// https://mvnrepository.com/artifact/org.apache.avro/avro
libraryDependencies += "org.apache.avro" % "avro" % "1.10.2"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.12.3"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.12.3"

// https://mvnrepository.com/artifact/org.apache.kafka/kafka
libraryDependencies += "org.apache.kafka" %% "kafka" % "2.8.0"



// https://mvnrepository.com/artifact/io.confluent/kafka-protobuf-serializer
//libraryDependencies += "io.confluent" % "kafka-protobuf-serializer" % confluentVersion
