package com.ecfront.lego.web

import io.vertx.core.http.{HttpServerOptions, HttpServerRequest}
import io.vertx.core.{Handler, Vertx}
import io.vertx.ext.apex.core.Router

object Startup extends App {

  val vertx = Vertx.vertx()
  val router = Router.router(vertx)
  HttpProcessor.router = router
  HttpProcessor.vertx = vertx
  val server = vertx.createHttpServer(new HttpServerOptions().setPort(Config.getPort).setMaxWebsocketFrameSize(1000000))
  server.requestHandler(new Handler[HttpServerRequest] {
    override def handle(event: HttpServerRequest): Unit = {
      router.accept(event)
    }
  }).listen()


}
