import com.example.ExampleRecord
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer
import java.time.Instant
import java.util.Properties

object Producer extends App{

  val brokerUrl = sys.env("KAFKA_BROKER_URL")
  val schemaUrl = sys.env("DEFAULT_SCHEMA_REGISTRY_URL")

  val props = new Properties
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl)
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,classOf[StringSerializer].getCanonicalName)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getCanonicalName)
  props.put("schema.registry.url", schemaUrl)
  //Throughput
  //props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy") --Might be interesting when not using avro
  props.put(ProducerConfig.LINGER_MS_CONFIG, "20")
  //props.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024)) --Might be interesting in real life
  //Safety
  props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
  //Other tuning
  //Take a look here: https://strimzi.io/blog/2020/10/15/producer-tuning/

  val producer = new KafkaProducer[String, ExampleRecord](props)

  val producerCallback: Callback = new Callback {
    override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {

      if(!Option(exception).isEmpty) {
       println("Not Fine: " + exception.getMessage)
          producer.close();
      }
    }
  }

  while (true) {

      val record = new ExampleRecord(Instant.now.getEpochSecond, "Hello there")
      val producerRecord = new ProducerRecord[String, ExampleRecord](Topics.Test1, Instant.now.getEpochSecond.toString, record)

      producer.send(producerRecord, producerCallback).get()
      Thread.sleep(5000)
  }
}
