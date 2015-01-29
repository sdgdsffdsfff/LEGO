package com.ecfront.lego.core.component.storage

import com.ecfront.common.BeanHelper
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.foundation.{IdModel, PageModel, StandardCode}
import com.typesafe.scalalogging.slf4j.LazyLogging
import io.vertx.core.{AsyncResult, Future, Handler, Vertx}

import scala.collection.JavaConversions._

trait VertxStorageService[M <: IdModel] extends JDBCService[M] {

  override protected def init(modelClazz: Class[M]): Unit = {
    JDBCService.db.createTableIfNotExist(modelClazz.getSimpleName, BeanHelper.getFields(modelClazz), "id")
  }

  override protected def doGetById(id: String, request: RequestProtocol, success: => (M) => Unit, fail: => (String, String) => Unit): Unit =
    VertxStorageService.execute[M]({
      (Void) =>
        JDBCService.db.getObjectByPk(tableName, id, modelClazz)
    }, success, fail)

  override protected def doGetByCondition(condition: String, request: RequestProtocol, success: => (M) => Unit, fail: => (String, String) => Unit): Unit =
    VertxStorageService.execute[M]({
      (Void) =>
        JDBCService.db.getObject("SELECT * FROM " + tableName + " WHERE " + condition, modelClazz)
    }, success, fail)

  override protected def doFindByCondition(condition: String, request: RequestProtocol, success: => (List[M]) => Unit, fail: => (String, String) => Unit): Unit =
    VertxStorageService.execute[List[M]]({
      (Void) =>
        JDBCService.db.findObjects("SELECT * FROM " + tableName + " WHERE " + condition, modelClazz).toList
    }, success, fail)

  override protected def doPageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => (PageModel[M]) => Unit, fail: => (String, String) => Unit): Unit =
    VertxStorageService.execute[PageModel[M]]({
      (Void) =>
        val page = JDBCService.db.findObjects("SELECT * FROM " + tableName + " WHERE " + condition, pageNumber, pageSize, modelClazz)
        PageModel(page.pageNumber, page.pageSize, page.pageTotal, page.recordTotal, page.objects.toList)
    }, success, fail)

  override protected def doFindAll(request: RequestProtocol, success: => (List[M]) => Unit, fail: => (String, String) => Unit): Unit =
    VertxStorageService.execute[List[M]]({
      (Void) =>
        JDBCService.db.findObjects("SELECT * FROM %s".format(tableName), modelClazz).toList
    }, success, fail)

  override protected def doPageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => (PageModel[M]) => Unit, fail: => (String, String) => Unit): Unit =
    VertxStorageService.execute[PageModel[M]]({
      (Void) =>
        val page = JDBCService.db.findObjects("SELECT * FROM %s".format(tableName), pageNumber, pageSize, modelClazz)
        PageModel(page.pageNumber, page.pageSize, page.pageTotal, page.recordTotal, page.objects.toList)
    }, success, fail)

  override protected def doSave(model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit): Unit = {
    var map = new java.util.HashMap[String, AnyRef]
    BeanHelper.getValues(model).foreach(it => map += it._1 -> it._2.asInstanceOf[AnyRef])
    VertxStorageService.execute[String]({
      (Void) =>
        JDBCService.db.save(tableName, map)
        ""
    }, success, fail)
  }

  override protected def doUpdate(id: String, model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit): Unit = {
    var map = new java.util.HashMap[String, AnyRef]
    BeanHelper.getValues(model).foreach(it => map += it._1 -> it._2.asInstanceOf[AnyRef])
    VertxStorageService.execute[String]({
      (Void) =>
        JDBCService.db.update(tableName, id, BeanHelper.getValues(model).asInstanceOf[Map[String, AnyRef]])
        ""
    }, success, fail)
  }

  override protected def doDeleteById(id: String, request: RequestProtocol, success: => (Unit) => Unit, fail: => (String, String) => Unit): Unit =
    VertxStorageService.execute[String]({
      (Void) =>
        JDBCService.db.deleteByPk(tableName, id)
        ""
    }, { String => success()}, fail)

  override protected def doDeleteByCondition(condition: String, request: RequestProtocol, success: => (Unit) => Unit, fail: => (String, String) => Unit): Unit =
    VertxStorageService.execute[String]({
      (Void) =>
        JDBCService.db.update("DELETE FROM " + tableName + " WHERE " + condition)
        ""
    }, { String => success()}, fail)

  override protected def doDeleteAll(request: RequestProtocol, success: => (Unit) => Unit, fail: => (String, String) => Unit): Unit =
    VertxStorageService.execute[String]({
      (Void) =>
        JDBCService.db.deleteAll(tableName)
        ""
    }, { String => success()}, fail)

}

object VertxStorageService extends LazyLogging {

  private var vertx: Vertx = _

  def init(vert: Vertx, dbConfig: String): Unit = {
    vertx = vert
    JDBCService.init(dbConfig)
  }

  private def execute[M](sqlExecute: => Unit => M, success: => (M) => Unit, fail: => (String, String) => Unit = null): Unit = {
    vertx.executeBlocking(new Handler[Future[M]] {
      override def handle(future: Future[M]): Unit = {
        try {
          future.complete(sqlExecute())
        } catch {
          case e =>
            logger.error("SQL execute error", e)
            future.fail(e)
        }
      }
    }, new Handler[AsyncResult[M]] {
      override def handle(e: AsyncResult[M]): Unit = {
        if (e.succeeded()) {
          success(e.result())
        } else {
          if (fail != null) {
            fail(StandardCode.FORBIDDEN_CODE, "SQL execute error:" + e.cause().getMessage)
          }
        }
      }
    })
  }
}
