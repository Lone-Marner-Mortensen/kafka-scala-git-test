version := "0.1"

scalaVersion := "2.13.5"

lazy val `kafka-scala-git-test` = project
  .in(file("."))
  .aggregate(
    avro,
    producer,
    consumer,
    stream,
    common)

lazy val avro = project
  .settings(
    name := "avro",
    resolvers += "confluent-release" at "https://packages.confluent.io/maven/",
    sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue,
    libraryDependencies ++= avroKafkaDependencies
  )

lazy val common = project
  .settings(
    name := "common"
  )

lazy val producer = project
  .enablePlugins(JavaAppPackaging, AshScriptPlugin)
  .settings(
    name := "producer",
    version in Docker := "latest",
    libraryDependencies ++= avroKafkaDependencies
  )
  .dependsOn(
    avro,
    common
  )

lazy val consumer = project
  .enablePlugins(JavaAppPackaging, AshScriptPlugin)
  .settings(
    name := "consumer",
    version in Docker := "latest",
    libraryDependencies ++= avroKafkaDependencies
  )
  .dependsOn(
    avro,
    common
  )

lazy val stream = project
  .enablePlugins(JavaAppPackaging, AshScriptPlugin)
  .settings(
    name := "stream",
    version in Docker := "latest",
    resolvers += "confluent-release" at "https://packages.confluent.io/maven/",
    libraryDependencies ++= avroKafkaDependencies ++ Seq(
      dependencies.kafkaStreams, dependencies.kafkaStreamsAvro
    )
  )
  .dependsOn(
    avro,
    common
  )

lazy val dependencies =
  new {
    val kafkaVersion = "2.3.0"
    val confluentPlatformVersion = "5.3.2"

    val kafkaStreams       = "org.apache.kafka"     % "kafka-streams" % kafkaVersion
    val kafkaStreamsAvro   = "io.confluent"         % "kafka-streams-avro-serde" % "3.3.0"
    val avroConfluent      = "io.confluent"         % "kafka-avro-serializer" % confluentPlatformVersion
    val kafkaClient        = "org.apache.kafka"     % "kafka-clients" % kafkaVersion
  }

lazy val avroKafkaDependencies = Seq(
  dependencies.avroConfluent,
  dependencies.kafkaClient
)
