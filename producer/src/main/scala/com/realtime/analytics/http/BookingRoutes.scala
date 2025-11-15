package com.realtime.analytics.http

import cats.syntax.all._
import cats.effect.kernel.Concurrent
import com.realtime.analytics.domain.BookingEvent
import com.realtime.analytics.services.{BookingService, LoggerService}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class BookingRoutes[F[_]: Concurrent](logger: LoggerService[F], bookingService: BookingService[F])
    extends Http4sDsl[F] {
  private[http] val prefix                             = "booking"
  implicit val decoder: EntityDecoder[F, BookingEvent] = jsonOf[F, BookingEvent]

  def getRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        Ok(s"Hello, $name.")
      case req @ POST -> Root / `prefix` / "new-event" =>
        handleRequest(req) { event =>
          bookingService.createBookingEvent(event) >>
            Ok(s"Booking event ${event.bookingId} updated.")
        }
    }

  private def handleRequest(request: Request[F])(logic: BookingEvent => F[Response[F]]) =
    (for {
      event <- request.as[BookingEvent]
      resp  <- logic(event)
    } yield resp).handleErrorWith { e =>
      logger.error(s"[ERROR] Failed processing request: ${e.getMessage}")
      BadRequest(s"Invalid request: ${e.getMessage}")
    }
}
