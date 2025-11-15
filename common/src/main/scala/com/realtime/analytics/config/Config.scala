package com.realtime.analytics.config

import pureconfig.generic.auto._
import pureconfig._
import cats.effect.Sync
import pureconfig.generic.ProductHint

object Config {

  implicit def hint[T]: ProductHint[T] = ProductHint[T](
    ConfigFieldMapping(CamelCase, CamelCase)
  )

  def load[F[_]: Sync]: F[AppConfig] =
    Sync[F].delay(ConfigSource.default.at("app").loadOrThrow[AppConfig])
}
