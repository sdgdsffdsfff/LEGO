package com.ecfront.lego.core.component.communication

import com.ecfront.lego.core.component.protocol.{Req, RespSimple}

trait Communication {

  def send(address: String, request: Req, success: => RespSimple => Unit, fail: => (String, String) => Unit = null): Unit

}
