package com.ecfront.lego.core.component.storage

import com.ecfront.easybi.dbutils.exchange.{DB, DS}
import com.ecfront.lego.core.component.CoreService
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.foundation.{IdModel, PageModel}
import com.fasterxml.jackson.databind.node.ObjectNode
import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.collection.mutable.ArrayBuffer

trait JDBCService[M <: IdModel] extends CoreService[M] {

  protected val tableName=modelClazz.getSimpleName

  protected def preSave(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit =null): Unit = {
    success(null)
  }

  protected def save(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit =null): Unit = {
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

  protected def doSave(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit =null): Unit

  protected def preUpdate(id: String, sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit =null): Unit = {
    success(null)
  }

  protected def update(id: String, sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit =null): Unit = {
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

  protected def doUpdate(id: String, sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit =null): Unit

  protected def preGetByCondition(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit =null): Unit = {
    success(null)
  }

  protected def getByCondition(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => M => Unit, fail: => (String, String) => Unit =null): Unit = {
    preGetByCondition(sql, params, request, {
      preResult =>
        doGetByCondition(sql, params, request, {
          result =>
            postGetByCondition(result, preResult, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doGetByCondition(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => M => Unit, fail: => (String, String) => Unit =null): Unit


  protected def preFindByCondition(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit =null): Unit = {
    success(null)
  }

  protected def findByCondition(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => List[M] => Unit, fail: => (String, String) => Unit =null): Unit = {
    preFindByCondition(sql, params, request, {
      preResult =>
        doFindByCondition(sql, params, request, {
          result =>
            postFindByCondition(result, preResult, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doFindByCondition(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => List[M] => Unit, fail: => (String, String) => Unit =null): Unit

  protected def preFindByCondition(sql: String, params: ArrayBuffer[AnyRef], pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit =null): Unit = {
    success(null)
  }

  protected def findByCondition(sql: String, params: ArrayBuffer[AnyRef], pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => PageModel[M] => Unit, fail: => (String, String) => Unit =null): Unit = {
    preFindByCondition(sql, params, pageNumber, pageSize, request, {
      preResult =>
        doFindByCondition(sql, params, pageNumber, pageSize, request, {
          result =>
            postFindByCondition(result, preResult, pageNumber, pageSize, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doFindByCondition(sql: String, params: ArrayBuffer[AnyRef], pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => PageModel[M] => Unit, fail: => (String, String) => Unit =null): Unit

  protected def preDeleteByCondition(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit =null): Unit = {
    success(null)
  }

  protected def deleteByCondition(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit =null): Unit = {
    preDeleteByCondition(sql, params, request, {
      preResult =>
        doDeleteByCondition(sql, params, request, {
          result =>
            postDeleteByCondition(preResult, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doDeleteByCondition(sql: String, params: ArrayBuffer[AnyRef], request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit =null): Unit

  override protected def deleteByCondition(condition: ObjectNode, request: RequestProtocol, success: => () => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def findByCondition(condition: ObjectNode, request: RequestProtocol, success: => (List[M]) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def findByCondition(condition: ObjectNode, pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => (PageModel[M]) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def getByCondition(condition: ObjectNode, request: RequestProtocol, success: => (M) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def preDeleteByCondition(condition: ObjectNode, request: RequestProtocol, success: => (Any) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def preFindByCondition(condition: ObjectNode, request: RequestProtocol, success: => (Any) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def preFindByCondition(condition: ObjectNode, pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => (Any) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def preGetByCondition(condition: ObjectNode, request: RequestProtocol, success: => (Any) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def preSave(model: M, request: RequestProtocol, success: => (Any) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def preUpdate(id: String, model: M, request: RequestProtocol, success: => (Any) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def save(model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def update(id: String, model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def doUpdate(id: String, model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def doGetByCondition(condition: ObjectNode, request: RequestProtocol, success: => (M) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def doFindByCondition(condition: ObjectNode, request: RequestProtocol, success: => (List[M]) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def doFindByCondition(condition: ObjectNode, pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => (PageModel[M]) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def doSave(model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit =null): Unit = ???

  override protected def doDeleteByCondition(condition: ObjectNode, request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit =null): Unit = ???

}

object JDBCService extends LazyLogging {

  var db: DB = _

  def init(dbConfig: String): Unit = {
    DS.setConfigPath(dbConfig)
    db = new DB()
  }

}
