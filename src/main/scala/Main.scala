
object Main  {
  println("hello")
  /*private val server = "localhost:9092"
  private val topic  = "com.agoda.propery_view"
  val run: IO[Unit] = {
    val consumerSettings = ConsumerSettings[IO, String, String]
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers(server)
      .withGroupId("group")

    val producerSettings = ProducerSettings[IO, String, String].withBootstrapServers(server)

    def processRecords(
        producer: KafkaProducer[IO, String, String]
    )(records: Chunk[ConsumerRecord[String, String]]): IO[CommitNow] =
      IO {
        /*val producerRecords =
          records.map(consumerRecord => ProducerRecord(topic, consumerRecord.key, consumerRecord.value))
        producer.produce(producerRecords).flatten.as(CommitNow)*/

        println("=== New Messages ===")
        records.foreach { record =>
          println(s"Key: ${record.key}, Value: ${record.value}")
        }
      }.as(CommitNow)

    val stream = KafkaProducer.stream(producerSettings).evalMap { producer =>
      KafkaConsumer
        .stream(consumerSettings)
        .subscribeTo(topic)
        .consumeChunk(chunk => processRecords(producer)(chunk))
    }

    stream.compile.drain
  }*/
}
