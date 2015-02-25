package com.ecfront.lego.core.component.storage

import com.ecfront.lego.core.component.BasicService
import com.ecfront.lego.core.component.protocol.{Req, Resp}
import com.ecfront.lego.core.foundation.{AppSecureModel, IdModel, SecureModel}
import com.ecfront.storage.{JDBCStorable, PageModel}
import com.typesafe.scalalogging.slf4j.LazyLogging

trait JDBCService[M <: IdModel] extends BasicService[M] with JDBCStorable[M, Req] {

  protected override def _appendAuth(request: Req): String = {
    if (modelClazz.isAssignableFrom(classOf[AppSecureModel]) && AppSecureModel.LEGO_APP_FLAG != request.appId) {
      " AND %s = '%s' ".format(AppSecureModel.APP_ID_FLAG, request.appId)
    } else {
      ""
    }
  }

  override protected def doFindAll(request: Req): Resp[List[M]] = {
    doFindByCondition("1=1", request)
  }

  override protected def doFindByCondition(condition: String, request: Req): Resp[List[M]] = {
    Resp.success(_findByCondition(addDefaultSort(condition), request).orNull)
  }

  override protected def doGetByCondition(condition: String, request: Req): Resp[M] = {
    Resp.success(_getByCondition(condition, request).get)
  }

  override protected def doPageAll(pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = {
    doPageByCondition("1=1", pageNumber, pageSize, request)
  }

  override protected def doPageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = {
    Resp.success(_pageByCondition(addDefaultSort(condition), pageNumber, pageSize, request).orNull)
  }

  override protected def doSave(model: M, request: Req): Resp[String] = {
    Resp.success(_save(model, request).orNull)
  }

  protected def doSaveWithoutTransaction(model: M, request: Req): Resp[String] = {
    Resp.success(_saveWithoutTransaction(model, request).orNull)
  }

  override protected def doGetById(id: String, request: Req): Resp[M] = {
    Resp.success(_getById(id, request).get)
  }

  override protected def doUpdate(id: String, model: M, request: Req): Resp[String] = {
    Resp.success(_update(id, model, request).orNull)
  }

  protected def doUpdateWithoutTransaction(id: String, model: M, request: Req): Resp[String] = {
    Resp.success(_updateWithoutTransaction(id, model, request).orNull)
  }

  override protected def doDeleteById(id: String, request: Req): Resp[String] = {
    Resp.success(_deleteById(id, request).orNull)
  }

  protected def doDeleteByIdWithoutTransaction(id: String, request: Req): Resp[String] = {
    Resp.success(_deleteByIdWithoutTransaction(id, request).orNull)
  }

  override protected def doDeleteAll(request: Req): Resp[List[String]] = {
    Resp.success(_deleteAll(request).orNull)
  }

  protected def doDeleteAllWithoutTransaction(request: Req): Resp[List[String]] = {
    Resp.success(_deleteAllWithoutTransaction(request).orNull)
  }

  override protected def doDeleteByCondition(condition: String, request: Req): Resp[List[String]] = {
    Resp.success(_deleteByCondition(condition, request).orNull)
  }

  protected def doDeleteByConditionWithoutTransaction(condition: String, request: Req): Resp[List[String]] = {
    Resp.success(_deleteByConditionWithoutTransaction(condition, request).orNull)
  }

  private def addDefaultSort(condition: String): String = {
    if (condition.toLowerCase.indexOf("order by") == -1 && classOf[SecureModel].isAssignableFrom(modelClazz)) {
      condition + " ORDER BY updateTime desc"
    } else {
      condition
    }
  }

}

object JDBCService extends LazyLogging {

  def init: Unit = {
    var path = this.getClass.getResource("/").getPath
    if (System.getProperties.getProperty("os.name").toUpperCase.indexOf("WINDOWS") != -1) {
      path = path.substring(1)
    }
    JDBCStorable.init(path)
  }

}
