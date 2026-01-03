package com.realtime.analytics.telemetry

import cats.effect._
import cats.effect.std.Console
import org.typelevel.otel4s.metrics.MeterProvider
import org.typelevel.otel4s.oteljava.OtelJava
import org.typelevel.otel4s.trace.TracerProvider
import org.typelevel.otel4s.context.LocalProvider
import org.typelevel.otel4s.oteljava.context.{Context, IOLocalContextStorage}

case class Telemetry[F[_]](
    traceProvider: TracerProvider[F],
    meterProvider: MeterProvider[F]
)

object Telemetry {
  def init[F[_]: Async: Console: LiftIO](name: String, endpoint: String): Resource[F, Telemetry[F]] = {
    sys.props.getOrElseUpdate("otel.service.name", name)
    sys.props.getOrElseUpdate("otel.exporter.otlp.endpoint", endpoint)
    sys.props.getOrElseUpdate("otel.exporter.otlp.logs.endpoint", endpoint)
    sys.props.getOrElseUpdate("otel.exporter.otlp.protocol", "http/protobuf")
    implicit val provider: LocalProvider[F, Context] = IOLocalContextStorage.localProvider[F]
    OtelJava.autoConfigured[F]().map { otel =>
      Telemetry(otel.tracerProvider, otel.meterProvider)
    }
  }
}
