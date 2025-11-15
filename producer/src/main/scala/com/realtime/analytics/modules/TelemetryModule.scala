package com.realtime.analytics.modules

import cats.effect._
import cats.syntax.all._
import com.realtime.analytics.telemetry.Telemetry
import org.typelevel.otel4s.metrics.{Counter, Meter}
import org.typelevel.otel4s.trace.Tracer

case class TelemetryModule[F[_]](
    meter: Meter[F],
    tracer: Tracer[F],
    bookingEventCounter: Counter[F, Long]
)

object TelemetryModule {
  def make[F[_]: Async](appName: String, telemetry: Telemetry[F]): Resource[F, TelemetryModule[F]] =
    Resource.eval {
      val name    = appName + ".producer"
      val tracerF = telemetry.traceProvider.tracer(name).get
      val meterF  = telemetry.meterProvider.meter(name).get

      val counterF = meterF.flatMap { meter =>
        meter
          .counter[Long]("booking.event.produced.total")
          .withUnit("events")
          .withDescription("Total number of booking events produced, tagged by event type.")
          .create
      }

      (meterF, tracerF, counterF).mapN(TelemetryModule.apply[F])
    }
}
