package com.ecfront.lego.core.component.communication

import com.ecfront.lego.core.component.protocol.{ResponseProtocol, RequestProtocol}

trait Communication {

  def send(address: String, request: RequestProtocol, success: => ResponseProtocol => Unit, fail: => (String, String) => Unit = null): Unit

}
