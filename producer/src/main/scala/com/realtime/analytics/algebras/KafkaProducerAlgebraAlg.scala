package com.realtime.analytics.algebras

import cats.effect.kernel.Async
import cats.syntax.all._
import com.realtime.analytics.domain.BookingEvent
import com.realtime.analytics.services.LoggerService
import fs2.kafka._
import io.circe.syntax._
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.context.Context
import io.opentelemetry.context.propagation.TextMapSetter
import org.typelevel.otel4s.Attribute
import org.typelevel.otel4s.metrics.Counter
import org.typelevel.otel4s.trace.Tracer

trait KafkaProducerAlgebraAlg[F[_]] {
  def produce(event: BookingEvent): F[Unit]
}

object KafkaProducerAlgebraAlg {
  def make[F[_]: Async](
      logger: LoggerService[F],
      topic: String,
      producer: KafkaProducer.Metrics[F, Int, String],
      tracer: Tracer[F],
      bookingCounter: Counter[F, Long]
  ): KafkaProducerAlgebraAlg[F] =
    new KafkaProducerAlgebraAlg[F] {
      private val otel       = GlobalOpenTelemetry.get()
      private val propagator = otel.getPropagators.getTextMapPropagator

      override def produce(event: BookingEvent): F[Unit] =
        tracer.span(s"kafka-send-$topic").use { span =>
          span.addAttribute(Attribute("messaging.system", "kafka"))
          span.addAttribute(Attribute("messaging.destination.name", topic))
          val eventName = event.eventType
          for {
            _ <- logger.info(s"Producing booking event [${event.bookingId}] to topic [$topic]")

            headers = {
              val ctx     = Context.current()
              val builder = scala.collection.mutable.ListBuffer.empty[Header]

              propagator.inject(
                ctx,
                builder,
                new TextMapSetter[scala.collection.mutable.ListBuffer[Header]] {
                  override def set(carrier: scala.collection.mutable.ListBuffer[Header], key: String, value: String)
                    : Unit =
                    carrier += Header(key, value.getBytes("UTF-8"))
                }
              )
              Headers.fromIterable(builder.toList)
            }

            record  = ProducerRecord(topic, event.bookingId, event.asJson.noSpaces).withHeaders(headers)
            message = ProducerRecords.one(record)

            result <- producer.produce(message).flatten.attempt
            _ <- result match {
              case Right(_) =>
                bookingCounter.add(1, Attribute("event.type", eventName.toString), Attribute("outcome", "success")) >>
                  logger.info(s"✅ Successfully produced event ${event.bookingId} to [$topic]")
              case Left(e) =>
                bookingCounter.add(1, Attribute("event.type", eventName.toString), Attribute("outcome", "failure"))
                span.recordException(e) >>
                  logger.error(s"❌ Failed to produce event ${event.bookingId} to [$topic]: ${e.getMessage}")
            }
          } yield ()
        }

    }
}
