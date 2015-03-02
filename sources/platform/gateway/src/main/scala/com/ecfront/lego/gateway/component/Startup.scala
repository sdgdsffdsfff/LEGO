package com.ecfront.lego.gateway.component

import com.ecfront.lego.core.component.communication.VertxCommunication
import com.ecfront.lego.core.component.{BasicStartup, Global}
import com.ecfront.lego.core.foundation.protocol.Req
import com.ecfront.lego.rbac.foundation.ServiceInfo
import com.ecfront.rpc.RPC

object Startup extends BasicStartup {

  override def registerServices(): Unit = {
    RPC.server.setHost(Global.config.get("host").asText()).
      setPort(Global.config.get("port").asInt()).
      setChannel(true).startup().
      post[Req[String]]("/", classOf[Req[String]], {
      (_, req) =>
        req.action match {
          case action if action == ServiceInfo.LOGIN || action == ServiceInfo.GET_LOGIN_INFO || action == ServiceInfo.LOGOUT =>
            Global.communication.send(action, req, classOf[String])
          case action if action == ServiceInfo.AUTH =>
            req.action=req.parameters("action").asInstanceOf[String]
            Global.communication.send(action, req, classOf[String])
          case _ =>
            req.accountId=Global.communication.send(req.copy(action), req, classOf[String])
            Global.communication.send(req.action, req, classOf[String])
        }
    })
  }

  override val id: String = "gateway"
  override val version: String = "1.0"
  override val name: String = "服务网关"
  override val dependency: Map[String, String] = Map[String, String]()
}

