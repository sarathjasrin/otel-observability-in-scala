package com.realtime.analytics.modules

import cats.effect.kernel._
import com.realtime.analytics.algebras.KafkaProducerAlgebraAlg
import com.realtime.analytics.config.KafkaConfig
import com.realtime.analytics.services.LoggerService
import org.typelevel.otel4s.trace.Tracer
import fs2.kafka._
import org.typelevel.otel4s.metrics.Counter

object KafkaModule {
  def make[F[_]: Async](
      logger: LoggerService[F],
      config: KafkaConfig,
      tracer: Tracer[F],
      bookingCount: Counter[F, Long]
  ): Resource[F, KafkaProducerAlgebraAlg[F]] = {
    val producerSettings = ProducerSettings[F, Int, String]
      .withBootstrapServers(config.bootstrapServers)
      .withAcks(Acks.All)

    KafkaProducer.resource(producerSettings).map { producer =>
      KafkaProducerAlgebraAlg.make[F](logger, config.topic, producer, tracer, bookingCount)
    }
  }
}
