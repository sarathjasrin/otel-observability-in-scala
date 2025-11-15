package com.realtime.analytics.algebras

import cats.effect.kernel.{Async, Resource}
import com.realtime.analytics.config.KafkaConfig
import com.realtime.analytics.domain.BookingEvent
import com.realtime.analytics.services.LoggerService
import fs2.kafka._
import io.circe.parser._
import cats.syntax.all._
import io.circe.generic.auto._

trait BookingEventProcessAlg[F[_]] {
  def process: F[Unit]
}

object BookingEventProcessAlg {
  def make[F[_]: Async](logger: LoggerService[F], config: KafkaConfig): Resource[F, BookingEventProcessAlg[F]] = {
    val consumerSettings: ConsumerSettings[F, Int, String] =
      ConsumerSettings[F, Int, String]
        .withBootstrapServers(config.bootstrapServers)
        .withGroupId(config.group)
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
    Resource.pure[F, BookingEventProcessAlg[F]](
      new BookingEventProcessAlg[F] {
        override def process: F[Unit] = {
          val stream = KafkaConsumer
            .stream(consumerSettings)
            .subscribeTo(config.topic)
            .records
            .evalMap { committable =>
              val record = committable.record
              decode[BookingEvent](record.value) match {
                case Right(event) =>
                  for {
                    _ <- logger.info(s"✅ Consumed event: ${event.bookingId}, type: ${event.eventType}")
                    _ <- committable.offset.commit
                  } yield ()
                case Left(error) =>
                  logger.error(s"Failed to decode BookingEvent: ${error.getMessage}") *>
                    committable.offset.commit
              }
            }
          stream.compile.drain
        }
      }
    )

  }
}
