package com.ecfront.lego.client.jvm

import java.util.UUID
import java.util.concurrent.CountDownLatch

import com.ecfront.common.{JsonHelper, ConfigHelper}
import com.ecfront.lego.core.foundation.protocol.{Req, Resp}
import com.ecfront.rpc.RPC
import com.ecfront.rpc.RPC.Result.Code
import com.fasterxml.jackson.databind.JsonNode

object Exchange {

  private[jvm] val config: JsonNode = ConfigHelper.init(InitConfig.CUSTOM_CONFIG_PATH).get

  private val appId = config.get("appId").asText()

  private val client = RPC.client.setHost(config.get("host").asText()).setPort(config.get("port").asInt()).setChannel(true).startup()

  private[jvm] def execute[REQ, RES](action: String, parameters: Map[String, Any], body: REQ, token: String, responseClass: Class[RES]): Resp[RES] = {
    val req = Req[REQ](UUID.randomUUID().toString, token, appId)
    req.action = action
    req.parameters = parameters
    req.body = body
    var resp: Resp[RES] = null
    val latch = new CountDownLatch(1)
    client.post("/", JsonHelper.toJsonString(req), responseClass, {
      result =>
        if (result.code == Code.SUCCESS) {
          resp = Resp.success(JsonHelper.toObject(result.body,responseClass))
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
