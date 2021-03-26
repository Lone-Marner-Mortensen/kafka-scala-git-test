import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig

import java.util.{Collections, Properties}

object StreamProperties {

  val brokerUrl: String = sys.env("KAFKA_BROKER_URL")
  val schemaUrl: String = sys.env("DEFAULT_SCHEMA_REGISTRY_URL")

  val serdeConfig = Collections.singletonMap("schema.registry.url", schemaUrl)

  def defaultAvro(applicationName: String) = {
    val props: Properties = new Properties
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationName)
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl)
    // Specify default (de)serializers for record keys and for record values.
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, classOf[Serdes.StringSerde])
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, classOf[SpecificAvroSerde[_ <: SpecificRecord]])
    props.put("schema.registry.url", schemaUrl)

    props
  }

}
