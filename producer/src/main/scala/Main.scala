import cats.effect.{IO, IOApp, Resource}
import com.realtime.analytics.config._
import com.realtime.analytics.http.{BookingRoutes, HttpServer}
import com.comcast.ip4s._
import com.realtime.analytics.modules.{KafkaModule, TelemetryModule}
import com.realtime.analytics.services.{BookingService, LoggerService}
import com.realtime.analytics.telemetry.Telemetry

object Main extends IOApp.Simple {
  def run: IO[Unit] =
    (for {
      config        <- Resource.eval(Config.load[IO])
      logger        <- Resource.eval(LoggerService.make[IO](config))
      telemetry     <- Telemetry.init[IO](config.name, config.telemetry.endpoint)
      otelModule    <- TelemetryModule.make[IO](config.name, telemetry)
      kafkaProducer <- KafkaModule.make[IO](logger, config.kafka, otelModule.tracer, otelModule.bookingEventCounter)
      server <- {
        val host           = Host.fromString(config.http.host).getOrElse(ipv4"0.0.0.0")
        val port           = Port.fromInt(config.http.port).getOrElse(port"8080")
        val bookingService = new BookingService[IO](logger, kafkaProducer)
        val bookingRoutes  = new BookingRoutes[IO](logger, bookingService)
        new HttpServer[IO](host, port, bookingRoutes.getRoutes).run()
      }
    } yield server).use(_ => IO.never)
}
