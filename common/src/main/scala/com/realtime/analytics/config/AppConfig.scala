package com.realtime.analytics.config

import scala.concurrent.duration.FiniteDuration

case class AppConfig(
    name: String,
    http: HttpConfig,
    kafka: KafkaConfig,
    telemetry: TelemetryConfig
)

case class HttpConfig(
    host: String,
    port: Int,
    timeout: FiniteDuration
)

case class KafkaConfig(
    bootstrapServers: String,
    topic: String,
    clientId: String,
    group: String
)

case class TelemetryConfig(
    endpoint: String
)
