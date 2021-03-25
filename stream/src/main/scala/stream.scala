import com.example.ExampleRecord
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization._
import org.apache.kafka.streams.kstream.{Consumed, KStream}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, _}
import java.util.{Collections, Properties}

object stream extends App{

  val brokerUrl = sys.env("KAFKA_BROKER_URL")
  val schemaUrl = sys.env("DEFAULT_SCHEMA_REGISTRY_URL")

  val settings = new Properties
  settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-template")
  settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl)
  // Specify default (de)serializers for record keys and for record values.
  settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, classOf[Serdes.StringSerde])
  settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, classOf[SpecificAvroSerde[_ <: SpecificRecord]])
  settings.put("schema.registry.url", schemaUrl)

  val serdeConfig = Collections.singletonMap("schema.registry.url", schemaUrl)
  val valueSpecificAvroSerde = new SpecificAvroSerde[ExampleRecord]
  valueSpecificAvroSerde.configure(serdeConfig, false)

  val builder = new StreamsBuilder

  //Reads from topic Topics.Test1
  val stream: KStream[String, ExampleRecord] =
      builder.stream(Topics.Test1, Consumed.`with`(Serdes.String(), valueSpecificAvroSerde))

  //Write to topic Topics.Test2
  stream
    .mapValues{v => println(v.msg); new ExampleRecord(v.timestamp, v.msg + "!!!")}
    .to(Topics.Test2)

  val kafkaStreams = new KafkaStreams(builder.build, settings)

  kafkaStreams.start()
}
