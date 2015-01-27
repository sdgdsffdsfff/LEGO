package com.ecfront.lego.core.component.storage

import com.ecfront.easybi.dbutils.exchange.{DB, DS}
import com.ecfront.lego.core.component.CoreService
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.foundation.IdModel
import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.collection.mutable.ArrayBuffer

trait JDBCService[M <: IdModel] extends CoreService[M] {

  protected val tableName = modelClazz.getSimpleName

  protected def preSave(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(null)
  }

  def save(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit = {
    preSave(sql, params, request, {
      preResult =>
        doSave(sql, params, request, {
          result =>
            postSave("", preResult, request, {
              id =>
                success(null)
            }, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doSave(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit

  protected def preUpdate(id: String, sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(null)
  }

  def update(id: String, sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit = {
    preUpdate(id, sql, params, request, {
      preResult =>
        doUpdate(id, sql, params, request, {
          result =>
            postUpdate("", preResult, request, {
              id =>
                success(null)
            }, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doUpdate(id: String, sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit

  override protected def preSave(model: M, request: RequestProtocol, success: => (Any) => Unit, fail: => (String, String) => Unit = null): Unit = ???

  override protected def save(model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit = null): Unit = ???

  override protected def doSave(model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit = null): Unit = ???

  override protected def preUpdate(id: String, model: M, request: RequestProtocol, success: => (Any) => Unit, fail: => (String, String) => Unit = null): Unit = ???

  override protected def update(id: String, model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit = null): Unit = ???

  override protected def doUpdate(id: String, model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit = null): Unit = ???

}

object JDBCService extends LazyLogging {

  var db: DB = _

  def init(dbConfig: String): Unit = {
    DS.setConfigPath(dbConfig)
    db = new DB()
  }

}
