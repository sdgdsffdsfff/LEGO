package com.ecfront.lego.core.component.storage

import java.util.UUID

import com.ecfront.easybi.dbutils.exchange.{DB, DS}
import com.ecfront.lego.core.component.CoreService
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.foundation.IdModel
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.apache.commons.beanutils.PropertyUtils

import scala.collection.mutable.ArrayBuffer

trait JDBCService[M <: IdModel] extends CoreService[M] {

  protected val tableName = modelClazz.getSimpleName

  protected def packageSaveSql(model: M, request: RequestProtocol): (String, Array[Any]) = {
    val sb = new StringBuilder("INSERT INTO " + tableName + " ( ")
    val keys = new ArrayBuffer[String]()
    val values = new ArrayBuffer[String]()
    val params = ArrayBuffer[Any]()
    val fields = JDBCService.getFields(model)
    fields.foreach {
      field =>
        keys += field._1
        values += "?"
        val value= field._1 match {
          case f if f == "id" && field._2 == null => UUID.randomUUID().toString
          case f if f == "createUser" && field._2 == null => request.userId
          case f if f == "updateUser" && field._2 == null => request.userId
          case f if f == "createTime" && field._2 == null => System.currentTimeMillis()
          case f if f == "updateTime" && field._2 == null => System.currentTimeMillis()
          case f if f == "appId" && field._2 == null => request.appId
          case _ => field._2
        }
        params +=value
    }
    sb.append(keys.mkString(",") + ") VALUES ( " + values.mkString(",") + " )")
    (sb.toString(), params.toArray)
  }

  protected def packageUpdateSql(id:String,model: M, request: RequestProtocol): (String, Array[AnyRef]) = {
    val sb = new StringBuilder("UPDATE " + tableName + " SET ")
    val keys = new ArrayBuffer[String]()
    val params = ArrayBuffer[AnyRef]()
    val fields = JDBCService.getFields(model)
    fields.foreach {
      field =>
        keys += field._1+"=?"
        params += field._1 match {
          case f if f == "updateUser" && field._2 == null => request.userId
          case f if f == "updateTime" && field._2 == null => System.currentTimeMillis()
          case f if f == "appId" && field._2 == null => request.appId
          case _ => field._2
        }
    }
    params+=id
    sb.append(keys.mkString(",") + " WHERE id= ? ")
    (sb.toString(), params.toArray)
  }

}

object JDBCService extends LazyLogging {

  var db: DB = _

  def init(dbConfig: String): Unit = {
    DS.setConfigPath(dbConfig)
    db = new DB()
  }

  def getFields[M <: IdModel](model: M): Map[String, Any] = {
    import scala.collection.JavaConversions._
    PropertyUtils.describe(model).toMap
  }

}
