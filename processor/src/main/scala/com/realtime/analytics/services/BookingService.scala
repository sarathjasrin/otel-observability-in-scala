package com.realtime.analytics.services

import cats.effect.kernel.Sync
import com.realtime.analytics.algebras.BookingEventProcessAlg

class BookingService[F[_]: Sync](bookingEventConsumer: BookingEventProcessAlg[F]) {
  def processEvent(): F[Unit] =
    bookingEventConsumer.process
}
