package com.ecfront.lego.client.jvm

import java.util.UUID
import java.util.concurrent.CountDownLatch

import com.ecfront.common.ConfigHelper
import com.ecfront.lego.core.foundation.protocol.{Req, Resp}
import com.ecfront.rpc.RPC
import com.ecfront.rpc.RPC.Result.Code
import com.fasterxml.jackson.databind.JsonNode

object Exchange {

  private[jvm] val config: JsonNode = ConfigHelper.init(InitConfig.CUSTOM_CONFIG_PATH).get

  private val appId = config.get("appId").asText()

  private val client = RPC.client.setHost(config.get("host").asText()).setPort(config.get("port").asInt()).setChannel(true).startup()

  private[jvm] def execute[E](action: String, parameters: Map[String, Any], body: Any, token: String, responseClass: Class[E]): Resp[E] = {
    val req = Req(UUID.randomUUID().toString, token, appId)
    req.action = action
    req.parameters = parameters
    req.body = body
    var resp: Resp[E] = null
    val latch = new CountDownLatch(1)
    client.post("/", req, responseClass, {
      result =>
        if (result.code == Code.SUCCESS) {
          resp = Resp.success(result.body)
        } else {
          resp = Resp.fail(result.code, result.message)
        }
        latch.countDown()
    })
    latch.await()
    resp
  }
}

object InitConfig {
  private[jvm] var CUSTOM_CONFIG_PATH: String = this.getClass.getResource("/lego.json").getPath
}
