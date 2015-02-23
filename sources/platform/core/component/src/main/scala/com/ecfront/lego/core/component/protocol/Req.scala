package com.ecfront.lego.core.component.protocol

import com.ecfront.lego.core.foundation.{AppSecureModel, SecureModel}

case class Req(
                            cId: String,
                            accountId: String,
                            appId: String
                            ) {
  var action: String = _
  var parameters: Map[String, Any] = _
  var body: String = _
}

object Req {
  def createBySystem = Req(System.nanoTime() + "", SecureModel.SYSTEM_USER_FLAG, AppSecureModel.LEGO_APP_FLAG)
}
