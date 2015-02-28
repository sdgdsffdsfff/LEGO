package com.ecfront.lego.gateway.component

import com.ecfront.lego.core.component.{BasicComponentInfo, BasicStartup}
import com.ecfront.lego.core.foundation.protocol.Req
import com.ecfront.lego.rbac.foundation.ServiceInfo
import com.ecfront.rpc.RPC

object Startup extends BasicStartup[ComponentInfo] {

  override def registerServices(): Unit = {
    RPC.server.setHost(getComponentInfo.config.get("host").asText()).
      setPort(getComponentInfo.config.get("port").asInt()).
      setChannel(true).startup().
      post[Req]("/", classOf[Req], {
      (_, req) =>
        req.action match {
          case action if action == ServiceInfo.LOGIN || action == ServiceInfo.GET_LOGIN_INFO || action == ServiceInfo.LOGOUT =>

        }
    })
  }

}

class ComponentInfo extends BasicComponentInfo {
  override def id: String = "gateway"

  override def version: String = "1.0"

  override def name: String = "服务网关"

  override def dependency: Map[String, String] = Map[String, String]()


}
