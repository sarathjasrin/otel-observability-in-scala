package com.realtime.analytics.http

import cats.effect._
import com.comcast.ip4s._
import org.http4s.HttpRoutes
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router

class HttpServer[F[_]: Async](host: Host, port: Port, routes: HttpRoutes[F]) {
  private val httpApp = Router("/" -> routes).orNotFound

  def run(): Resource[F, Unit] =
    EmberServerBuilder
      .default[F]
      .withHost(host)
      .withPort(port)
      .withHttpApp(httpApp = httpApp)
      .build
      .map(_ => ())
}
