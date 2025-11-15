package com.realtime.analytics.services

import cats.effect.kernel.Sync
import com.realtime.analytics.config.AppConfig
import org.typelevel.log4cats.slf4j.Slf4jLogger

trait LoggerService[F[_]] {
  def info(msg: String): F[Unit]
  def warn(msg: String): F[Unit]
  def error(msg: String): F[Unit]
  def debug(msg: String): F[Unit]
}

object LoggerService {
  def make[F[_]: Sync](config: AppConfig): F[LoggerService[F]] = Sync[F].delay {
    val baseLogger = Slf4jLogger.getLoggerFromName[F](config.name)

    new LoggerService[F] {
      override def info(msg: String): F[Unit] = baseLogger.info(msg)

      override def warn(msg: String): F[Unit] = baseLogger.warn(msg)

      override def error(msg: String): F[Unit] = baseLogger.error(msg)

      override def debug(msg: String): F[Unit] = baseLogger.debug(msg)
    }
  }
}
