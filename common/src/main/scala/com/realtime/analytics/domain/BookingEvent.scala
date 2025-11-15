package com.realtime.analytics.domain

import com.realtime.analytics.enums.BookingEventType
import io.circe._
import io.circe.generic.semiauto._

case class BookingEvent(
    bookingId: Int,
    userId: Int,
    propertyId: Int,
    price: Double,
    timestamp: Long,
    eventType: BookingEventType
)

object BookingEvent {
  implicit val encoder: Encoder[BookingEvent] = deriveEncoder
  implicit val decoder: Decoder[BookingEvent] = deriveDecoder
}
