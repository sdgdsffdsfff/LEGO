package com.ecfront.lego.core.component.communication

import com.ecfront.lego.core.foundation.protocol.{Req, Resp}
import com.typesafe.scalalogging.slf4j.LazyLogging

trait Communication extends LazyLogging {

  def send[REQ, RES](address: String, request: Req[REQ], responseBodyClass: Class[RES]): Resp[RES]

  def consumer[REQ](address: String, process: => Req[REQ] => Resp[_], requestBodyClass: Class[REQ]): Resp[_]

}
