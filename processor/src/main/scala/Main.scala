import cats.effect.{IO, IOApp}
import com.realtime.analytics.algebras.BookingEventProcessAlg
import com.realtime.analytics.config._
import com.realtime.analytics.services.{BookingService, LoggerService}

object Main extends IOApp.Simple {

  override def run: IO[Unit] =
    for {
      config <- Config.load[IO]
      logger <- LoggerService.make[IO](config)
      _ <- BookingEventProcessAlg.make[IO](logger, config.kafka).use { bookingEventProcess =>
        val bookingService = new BookingService[IO](bookingEventProcess)
        bookingService.processEvent()
      }
    } yield ()
}
