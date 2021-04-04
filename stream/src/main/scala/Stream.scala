import com.example.ExampleRecord
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.kafka.common.serialization._
import org.apache.kafka.streams.kstream.{Consumed, KStream}
import org.apache.kafka.streams.{KafkaStreams, _}


object Stream extends App{

  val valueSpecificAvroSerde = new SpecificAvroSerde[ExampleRecord]
  valueSpecificAvroSerde.configure(StreamProperties.serdeConfig, false)

  val builder = new StreamsBuilder

  //Reads from topic Topics.Test1
  val stream: KStream[String, ExampleRecord] = builder.stream(Topics.Test1, Consumed.`with`(Serdes.String(), valueSpecificAvroSerde))

  //Write to topic Topics.Test2
  stream
    .mapValues{v => println(v.msg); new ExampleRecord(v.timestamp, s"${v.msg}!!!")}
    .to(Topics.Test2)

  val kafkaStreams = new KafkaStreams(builder.build, StreamProperties.defaultAvro("stream-template"))

  kafkaStreams.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
    override def uncaughtException(t: Thread, e: Throwable) = println(s"Exception on thread ${t.getName}: $e.getMessage")
  })

  kafkaStreams.start()

  sys.addShutdownHook {
    kafkaStreams.close()
  }
}
