package com.realtime.analytics.enums

import io.circe.{Decoder, Encoder}

sealed abstract class BookingEventType

object BookingEventType {
  case object PropertyView   extends BookingEventType
  case object RoomSelected   extends BookingEventType
  case object Checkout       extends BookingEventType
  case object Paid           extends BookingEventType
  case object RoomAllocation extends BookingEventType
  case object Completed      extends BookingEventType

  implicit val encodeBookingEventType: Encoder[BookingEventType] =
    Encoder.encodeString.contramap {
      case PropertyView   => "PropertyView"
      case RoomSelected   => "RoomSelected"
      case Checkout       => "Checkout"
      case Paid           => "Paid"
      case RoomAllocation => "RoomAllocation"
      case Completed      => "Completed"
    }

  implicit val decodeBookingEventType: Decoder[BookingEventType] =
    Decoder.decodeString.emap {
      case "PropertyView" => Right(PropertyView)
      case "RoomSelected" => Right(RoomSelected)
      case "Checkout"       => Right(Checkout)
      case "Paid"           => Right(Paid)
      case "RoomAllocation" => Right(RoomAllocation)
      case "Completed"      => Right(Completed)
    }
}
