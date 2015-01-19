package com.ecfront.lego.core.component.protocol

import com.fasterxml.jackson.databind.JsonNode

case class RequestProtocol(
                            cId: String,
                            userId: String,
                            appId: String
                            ) {
  var action: String = _
  var body: JsonNode = _
}

object RequestProtocol {
  def createBySystem = RequestProtocol("", "__system", "LEGO")
}
