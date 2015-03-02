package com.ecfront.lego.core.foundation.protocol

import com.ecfront.lego.core.foundation.{AppSecureModel, SecureModel}

case class Req[E](
                            cId: String,
                            accountId: String,
                            appId: String
                            ) {
  var action: String = _
  var parameters: Map[String, Any] = _
  var body: E = _
}

object Req {
  def createBySystem = Req(System.nanoTime() + "", SecureModel.SYSTEM_USER_FLAG, AppSecureModel.LEGO_APP_FLAG)
}
