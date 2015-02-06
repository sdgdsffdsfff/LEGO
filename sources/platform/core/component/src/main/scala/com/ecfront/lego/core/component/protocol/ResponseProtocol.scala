package com.ecfront.lego.core.component.protocol

case class ResponseProtocol(
                             override val code: String,
                             override val message: String,
                             override val body: String) extends ResponseDTO[String](code,message,body)
