package com.ecfront.lego.core.component.protocol

case class RequestProtocol(
                            cId: String,
                            userId: String,
                            appId: String
                            ) {
  var action: String = _
  var parameters: Map[String, Any] = _
  var body: String = _
}

object RequestProtocol {
  def createBySystem = RequestProtocol("", "__system", "LEGO")
}
