import com.example.ExampleRecord
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.Properties
import scala.collection.JavaConversions._

object Consumer extends App {

  val brokerUrl = sys.env("KAFKA_BROKER_URL")
  val schemaUrl = sys.env("DEFAULT_SCHEMA_REGISTRY_URL")

  val props = new Properties
  props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl)
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group-name")
  props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100")
  //Tuning
  //props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");//Might be good in real life
  //Take a look here: https://strimzi.io/blog/2021/01/07/consumer-tuning/

  // avro part (deserializer)
  props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[KafkaAvroDeserializer].getName)
  props.setProperty("schema.registry.url", schemaUrl)
  props.setProperty("specific.avro.reader", "true")

  val consumer: KafkaConsumer[String, ExampleRecord] = new KafkaConsumer[String, ExampleRecord](props)
  consumer.subscribe(List(Topics.Test1, Topics.Test2))

  System.out.println("Waiting for data...")

  while (true) {
    System.out.println("Polling...")
    val records: ConsumerRecords[String, ExampleRecord] = consumer.poll(Duration.ofSeconds(1))

    for (record <- records) {
      System.out.println(s"From consumer: ${record.value.msg}")
    }
  }
}