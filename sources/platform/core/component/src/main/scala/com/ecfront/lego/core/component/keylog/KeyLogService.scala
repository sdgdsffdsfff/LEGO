package com.ecfront.lego.core.component.keylog

import com.ecfront.lego.core.ComponentInfo
import com.ecfront.lego.core.component.SyncBasicService
import com.ecfront.lego.core.component.protocol.Req
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.{KeyLog, StandardCode}

object KeyLogService extends JDBCService[KeyLog] with SyncBasicService[KeyLog] {

  def success(message: String, request: Req): Unit = {
    log(StandardCode.SUCCESS_CODE, message, request)
  }

  def notFound(message: String, request: Req): Unit = {
    log(StandardCode.NOT_FOUND_CODE, message, request)
  }

  def badRequest(message: String, request: Req): Unit = {
    log(StandardCode.BAD_REQUEST_CODE, message, request)
  }

  def forbidden(message: String, request: Req): Unit = {
    log(StandardCode.FORBIDDEN_CODE, message, request)
  }

  def unAuthorized(message: String, request: Req): Unit = {
    log(StandardCode.UNAUTHORIZED_CODE, message, request)
  }

  def serverError(message: String, request: Req): Unit = {
    log(StandardCode.INTERNAL_SERVER_CODE, message, request)
  }

  def notImplemented(message: String, request: Req): Unit = {
    log(StandardCode.NOT_IMPLEMENTED_CODE, message, request)
  }

  def serverUnavailable(message: String, request: Req): Unit = {
    log(StandardCode.SERVICE_UNAVAILABLE_CODE, message, request)
  }

  def customFail(code: String, message: String, request: Req): Unit = {
    log(code, message, request)
  }

  private def log(code: String, message: String, request: Req): Unit = {
    val log = KeyLog()
    log.code = code
    log.message = message
    log.action = request.action
    log.componentId = ComponentInfo.id
    log.trackId = request.cId
    save(log, request)
  }

}
