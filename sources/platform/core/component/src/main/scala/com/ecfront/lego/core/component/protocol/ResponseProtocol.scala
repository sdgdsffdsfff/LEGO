package com.ecfront.lego.core.component.protocol

case class ResponseProtocol(
                             code: String,
                             message: String,
                             body: String=null)

object ResponseProtocol{

  implicit def log(response: ResponseProtocol) = new {
    def failLog =" [ %s ] : %s".format(response.code,response.message)
  }

}

case class ResponseException(code: String,
                        message: String) extends Exception
