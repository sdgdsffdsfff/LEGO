package com.ecfront.lego.core.component.communication

import com.ecfront.common.JsonHelper
import com.ecfront.lego.core.foundation.protocol.{Req, RespSimple}
import com.ecfront.lego.core.foundation.StandardCode
import io.vertx.core.eventbus.Message
import io.vertx.core.{AsyncResult, Handler, Vertx}

object VertxCommunication extends Communication {

  //TODO
  private val vertx = Vertx.vertx()

  def send(address: String, request: Req, complete: => RespSimple => Unit, fail: => (String, String) => Unit = null): Unit = {
    vertx.eventBus().send(address, JsonHelper.toJsonString(request), new Handler[AsyncResult[Message[String]]] {
      override def handle(e: AsyncResult[Message[String]]): Unit = {
        if (e.succeeded()) {
          complete(JsonHelper.toObject(e.result().body(), classOf[RespSimple]))
        } else {
          if (fail != null) {
            fail(StandardCode.SERVICE_UNAVAILABLE_CODE, "Service not responding.")
          }
        }
      }
    })
  }
}
