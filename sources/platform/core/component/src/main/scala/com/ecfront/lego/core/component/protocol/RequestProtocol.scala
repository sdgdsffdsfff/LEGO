package com.ecfront.lego.core.component.protocol

import com.ecfront.lego.core.foundation.{AppSecureModel, SecureModel}

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
  def createBySystem = RequestProtocol(System.nanoTime() + "", SecureModel.SYSTEM_USER_FLAG, AppSecureModel.LEGO_APP_FLAG)
}
