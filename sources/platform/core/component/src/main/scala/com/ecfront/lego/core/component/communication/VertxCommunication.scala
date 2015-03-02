package com.ecfront.lego.core.component.communication

import com.ecfront.common.JsonHelper
import com.ecfront.lego.core.component.Global
import com.ecfront.lego.core.foundation.StandardCode
import com.ecfront.lego.core.foundation.protocol.{Req, Resp}
import io.vertx.core.eventbus.Message
import io.vertx.core.{AsyncResult, Handler}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}

class VertxCommunication extends Communication {

  override def send[REQ, RES](address: String, request: Req[REQ], responseBodyClass: Class[RES]): Resp[RES] = {
    Await.result(doSend(address, request, responseBodyClass), Duration.Inf)
  }

  private def doSend[RES, REQ](address: String, request: Req[REQ], responseBodyClass: Class[RES]): Future[Resp[RES]] = {
    val p = Promise[Resp[RES]]()
    Global.vertx.eventBus().send(address, JsonHelper.toJsonString(request), new Handler[AsyncResult[Message[String]]] {
      override def handle(e: AsyncResult[Message[String]]): Unit = {
        if (e.succeeded()) {
          val json = JsonHelper.toJson(e.result().body())
          val body = JsonHelper.toObject(json.get("body"), responseBodyClass)
          val response = Resp(json.get("code").asText(), json.get("message").asText(), Some(body))
          response.body = body
          p.success(response)
        } else {
          p.success(Resp.fail(StandardCode.SERVICE_UNAVAILABLE_CODE, "The handler[%s] not responding.".format(address)))
          logger.warn("The handler[%s] not responding.".format(address))
        }
      }
    })
    p.future
  }

  override def consumer[REQ](address: String, process: => Req[REQ] => Resp[_], requestBodyClass: Class[REQ]): Unit = {
    val consumer = Global.vertx.eventBus().consumer[String](address)
    consumer.handler(new Handler[Message[String]] {
      override def handle(event: Message[String]): Unit = {
        val json = JsonHelper.toJson(event.body())
        val request = Req[REQ](json.get("cId").asText(), json.get("accountId").asText(), json.get("appId").asText())
        request.action = json.get("action").asText()
        request.parameters = JsonHelper.toObject(json.get("parameters"), classOf[Map[String, Any]])
        request.body = JsonHelper.toObject(json.get("body"), requestBodyClass)
        val result = process(request)
        if (result != null) {
          event.reply(result)
        }
      }
    })
    consumer.completionHandler(new Handler[AsyncResult[Void]] {
      override def handle(event: AsyncResult[Void]): Unit = {
        if (event.succeeded()) {
          logger.info("The handler[%s] registration has reached all nodes.".format(address))
        } else {
          logger.error("The handler[%s] registration failed!".format(address), event.cause())
        }
      }
    })
  }
}
