package com.ecfront.lego.core.component.storage

import com.ecfront.common.BeanHelper
import com.ecfront.easybi.dbutils.exchange.{DB, DS}
import com.ecfront.lego.core.component.CoreService
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.foundation.{AppSecureModel, IdModel, PageModel}
import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

trait JDBCService[M <: IdModel] extends CoreService[M] {

  protected val tableName = modelClazz.getSimpleName

  override protected def init(modelClazz: Class[M]): Unit = {
    JDBCService.db.createTableIfNotExist(modelClazz.getSimpleName, BeanHelper.getFields(modelClazz), "id")
  }

  override protected def doGetById(id: String, request: RequestProtocol): Option[M] = {
    doGetByCondition(s"${IdModel.ID_FLAG} = '$id'", request)
  }

  override protected def doGetByCondition(condition: String, request: RequestProtocol): Option[M] = {
    Some(JDBCService.db.getObject("SELECT * FROM " + tableName + " WHERE " + condition + appendAuth(request), modelClazz))
  }

  override protected def doFindAll(request: RequestProtocol): Option[List[M]] = {
    doFindByCondition("1=1", request)
  }

  override protected def doFindByCondition(condition: String, request: RequestProtocol): Option[List[M]] = {
    Some(JDBCService.db.findObjects("SELECT * FROM " + tableName + " WHERE " + condition + appendAuth(request), modelClazz).toList)
  }

  override protected def doPageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = {
    doPageByCondition("1=1", pageNumber, pageSize, request)
  }

  override protected def doPageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = {
    val page = JDBCService.db.findObjects("SELECT * FROM " + tableName + " WHERE " + condition + appendAuth(request), pageNumber, pageSize, modelClazz)
    Some(PageModel(page.pageNumber, page.pageSize, page.pageTotal, page.recordTotal, page.objects.toList))
  }

  override protected def doSave(model: M, request: RequestProtocol): Option[String] = {
    JDBCService.db.open()
    doSaveWithoutTransaction(model, request)
    JDBCService.db.commit()
    Some(model.id)
  }

  protected def doSaveWithoutTransaction(model: M, request: RequestProtocol): Option[String] = {
    JDBCService.db.save(tableName, BeanHelper.getValues(model).asInstanceOf[Map[String, AnyRef]])
    Some(model.id)
  }

  protected def doSaveManyToManyRel(mainId: String, relIds: List[String], relTableName: String, request: RequestProtocol): Unit = {
    val (mainField, relField) = JDBCService.parseManyToManyRelTableFields(relTableName, tableName)
    if (relIds != null && relIds.nonEmpty) {
      val params = ArrayBuffer[Array[AnyRef]]()
      relIds.foreach {
        relId =>
          params += Array(mainId, relId)
      }
      JDBCService.db.batch(s"INSERT INTO $relTableName  (${mainField + "_" + IdModel.ID_FLAG},${relField + "_" + IdModel.ID_FLAG})  VALUES (?,?)", params.toArray)
    }
  }

  override protected def doUpdate(id: String, model: M, request: RequestProtocol): Option[String] = {
    JDBCService.db.open()
    doUpdateWithoutTransaction(id, model, request)
    JDBCService.db.commit()
    Some(model.id)
  }

  protected def doUpdateWithoutTransaction(id: String, model: M, request: RequestProtocol): Option[String] = {
    JDBCService.db.update(tableName, id, BeanHelper.getValues(model).asInstanceOf[Map[String, AnyRef]])
    Some(model.id)
  }

  protected def doUpdateManyToManyRel(mainId: String, relIds: List[String], relTableName: String, request: RequestProtocol): Unit = {
    val (mainField, relField) = JDBCService.parseManyToManyRelTableFields(relTableName, tableName)
    if (relIds != null && relIds.nonEmpty) {
      val params = ArrayBuffer[Array[AnyRef]]()
      relIds.foreach {
        relId =>
          params += Array(mainId, relId)
      }
      JDBCService.db.batch(s"DELETE FROM $relTableName WHERE ${mainField + "_" + IdModel.ID_FLAG} = ? AND ${relField + "_" + IdModel.ID_FLAG} =? ", params.toArray)
      JDBCService.db.batch(s"INSERT INTO $relTableName  (${mainField + "_" + IdModel.ID_FLAG},${relField + "_" + IdModel.ID_FLAG})  VALUES (?,?)", params.toArray)
    }
  }

  override protected def doDeleteById(id: String, request: RequestProtocol): Option[String] = {
    doDeleteByCondition(s"${IdModel.ID_FLAG} = '$id'", request)
    Some(id)
  }

  protected def doDeleteByIdWithoutTransaction(id: String, request: RequestProtocol): Option[String] = {
    doDeleteByConditionWithoutTransaction(s"${IdModel.ID_FLAG} = '$id'", request)
    Some(id)
  }

  override protected def doDeleteAll(request: RequestProtocol): Option[List[String]] = {
    doDeleteByCondition("1=1", request)
  }

  protected def doDeleteAllWithoutTransaction(request: RequestProtocol): Option[List[String]] = {
    doDeleteByConditionWithoutTransaction("1=1", request)
  }

  override protected def doDeleteByCondition(condition: String, request: RequestProtocol): Option[List[String]] = {
    JDBCService.db.open()
    doDeleteByConditionWithoutTransaction(condition, request)
    JDBCService.db.commit()
    Some(List())
  }

  protected def doDeleteByConditionWithoutTransaction(condition: String, request: RequestProtocol): Option[List[String]] = {
    JDBCService.db.update("DELETE FROM " + tableName + " WHERE " + condition + appendAuth(request))
    Some(List())
  }

  protected def doDeleteManyToManyRel(relTableName: String, condition: String, request: RequestProtocol): Unit = {
    val sql = if (condition == "1=1") {
      "DELETE FROM " + relTableName + " WHERE " + condition
    } else {
      s"DELETE FROM $relTableName WHERE" +
        s" ${tableName + "_" + IdModel.ID_FLAG} in" +
        s" (SELECT ${IdModel.ID_FLAG} FROM $tableName WHERE $condition ${appendAuth(request)})"
    }
    JDBCService.db.update(sql)
  }

  protected def appendAuth(request: RequestProtocol): String = {
    JDBCService.appendAuth(modelClazz, request)
  }

}

object JDBCService extends LazyLogging {

  var db: DB = _

  def init(dbConfig: String): Unit = {
    DS.setConfigPath(dbConfig)
    db = new DB()
  }

  def appendAuth(modelClazz: Class[_], request: RequestProtocol): String = {
    if (modelClazz.isAssignableFrom(classOf[AppSecureModel]) && AppSecureModel.LEGO_APP_FLAG != request.appId) {
      " AND %s = '%s' ".format(AppSecureModel.APP_ID_FLAG, request.appId)
    } else {
      ""
    }
  }

  def createManyToManyRelTable(mainModel: Class[_], relModel: Class[_]): String = {
    val tableName = "rel_" + mainModel.getSimpleName + "_" + relModel.getSimpleName
    JDBCService.db.createTableIfNotExist(
      tableName,
      Map[String, String](
        mainModel.getSimpleName + "_" + IdModel.ID_FLAG -> "String",
        relModel.getSimpleName + "_" + IdModel.ID_FLAG -> "String"
      ),
      null)
    tableName
  }

  /**
   * 解析多对多关联表的字段，如 rel_Role_Resource ,currentTableName=Role时，返回 (Role,Resource)，反之返回 (Resource,Role)
   * @param relTableName 关联表名
   * @param currentTableName 当前表名
   * @return 字段
   */
  private def parseManyToManyRelTableFields(relTableName: String, currentTableName: String): (String, String) = {
    val f = relTableName.split("_")
    if (currentTableName == f(1)) {
      (f(1), f(2))
    } else {
      (f(2), f(1))
    }
  }

}
