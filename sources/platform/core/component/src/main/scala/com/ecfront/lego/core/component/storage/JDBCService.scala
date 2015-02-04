package com.ecfront.lego.core.component.storage

import com.ecfront.common.BeanHelper
import com.ecfront.easybi.dbutils.exchange.{DB, DS}
import com.ecfront.lego.core.component.CoreService
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.foundation.{IdModel, PageModel}
import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.collection.JavaConversions._

trait JDBCService[M <: IdModel] extends CoreService[M] {

  protected val tableName = modelClazz.getSimpleName


  override protected def executeGetById(id: String, request: RequestProtocol): M = {
    JDBCService.db.getObjectByPk(tableName, id, modelClazz)
  }

  override protected def executeGetByCondition(condition: String, request: RequestProtocol): M = {
    JDBCService.db.getObject("SELECT * FROM " + tableName + " WHERE " + condition, modelClazz)
  }

  override protected def executeFindAll(request: RequestProtocol): List[M] = {
    JDBCService.db.findObjects("SELECT * FROM %s".format(tableName), modelClazz).toList
  }

  override protected def executeFindByCondition(condition: String, request: RequestProtocol): List[M] = {
    JDBCService.db.findObjects("SELECT * FROM " + tableName + " WHERE " + condition, modelClazz).toList
  }

  override protected def executePageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): PageModel[M] = {
    val page = JDBCService.db.findObjects("SELECT * FROM %s".format(tableName), pageNumber, pageSize, modelClazz)
    PageModel(page.pageNumber, page.pageSize, page.pageTotal, page.recordTotal, page.objects.toList)
  }

  override protected def executePageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): PageModel[M] = {
    val page = JDBCService.db.findObjects("SELECT * FROM " + tableName + " WHERE " + condition, pageNumber, pageSize, modelClazz)
    PageModel(page.pageNumber, page.pageSize, page.pageTotal, page.recordTotal, page.objects.toList)
  }

  override protected def executeSave(model: M, request: RequestProtocol): String = {
    var map = new java.util.HashMap[String, AnyRef]
    BeanHelper.getValues(model).foreach(it => map += it._1 -> it._2.asInstanceOf[AnyRef])
    JDBCService.db.open()
    JDBCService.db.save(tableName, map)
    JDBCService.db.commit()
    ""
  }

  override protected def executeUpdate(id: String, model: M, request: RequestProtocol): String = {
    var map = new java.util.HashMap[String, AnyRef]
    BeanHelper.getValues(model).foreach(it => map += it._1 -> it._2.asInstanceOf[AnyRef])
    JDBCService.db.open()
    JDBCService.db.update(tableName, id, BeanHelper.getValues(model).asInstanceOf[Map[String, AnyRef]])
    JDBCService.db.commit()
    ""
  }

  override protected def executeDeleteById(id: String, request: RequestProtocol): Unit = {
    JDBCService.db.open()
    JDBCService.db.deleteByPk(tableName, id)
    JDBCService.db.commit()
  }

  override protected def executeDeleteByCondition(condition: String, request: RequestProtocol): Unit = {
    JDBCService.db.open()
    JDBCService.db.update("DELETE FROM " + tableName + " WHERE " + condition)
    JDBCService.db.commit()
  }

  override protected def executeDeleteAll(request: RequestProtocol): Unit = {
    JDBCService.db.open()
    JDBCService.db.deleteAll(tableName)
    JDBCService.db.commit()
  }
}

object JDBCService extends LazyLogging {

  var db: DB = _

  def init(dbConfig: String): Unit = {
    DS.setConfigPath(dbConfig)
    db = new DB()
  }


}
