package com.realtime.analytics.services

import cats.effect.kernel.Sync
import com.realtime.analytics.algebras.KafkaProducerAlgebraAlg
import com.realtime.analytics.domain.BookingEvent

class BookingService[F[_]: Sync](logger: LoggerService[F], kafkaProducer: KafkaProducerAlgebraAlg[F]) {
  def createBookingEvent(event: BookingEvent): F[Unit] = {
    logger.info(s"Calling Kafka Producer ${event.bookingId}")
    kafkaProducer.produce(event)
  }
}
