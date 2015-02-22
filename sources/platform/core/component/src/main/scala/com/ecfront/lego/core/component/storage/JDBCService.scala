package com.ecfront.lego.core.component.storage

import com.ecfront.lego.core.component.BasicService
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.foundation.{AppSecureModel, IdModel}
import com.ecfront.storage.{JDBCStorable, PageModel}
import com.typesafe.scalalogging.slf4j.LazyLogging

trait JDBCService[M <: IdModel] extends BasicService[M] with JDBCStorable[M, RequestProtocol] {

  protected override def _appendAuth(request: RequestProtocol): String = {
    if (modelClazz.isAssignableFrom(classOf[AppSecureModel]) && AppSecureModel.LEGO_APP_FLAG != request.appId) {
      " AND %s = '%s' ".format(AppSecureModel.APP_ID_FLAG, request.appId)
    } else {
      ""
    }
  }

  override protected def doFindAll(request: RequestProtocol): Option[List[M]] = {
    _findAll(request)
  }

  override protected def doGetByCondition(condition: String, request: RequestProtocol): Option[M] = {
    _getByCondition(condition, request)
  }

  override protected def doPageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = {
    _pageByCondition(condition, pageNumber, pageSize, request)
  }

  override protected def doSave(model: M, request: RequestProtocol): Option[String] = {
    _save(model, request)
  }

  override protected def doFindByCondition(condition: String, request: RequestProtocol): Option[List[M]] = {
    _findByCondition(condition, request)
  }

  override protected def doPageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = {
    _pageAll(pageNumber, pageSize, request)
  }

  override protected def doGetById(id: String, request: RequestProtocol): Option[M] = {
    _getById(id, request)
  }

  override protected def doUpdate(id: String, model: M, request: RequestProtocol): Option[String] = {
    _update(id, model, request)
  }

  protected def doUpdateWithoutTransaction(id: String, model: M, request: RequestProtocol): Option[String] = {
    _updateWithoutTransaction(id, model, request)
  }

  override protected def doDeleteById(id: String, request: RequestProtocol): Option[String] = {
    _deleteById(id, request)
  }

  protected def doDeleteByIdWithoutTransaction(id: String, request: RequestProtocol): Option[String] = {
    _deleteByIdWithoutTransaction(id, request)
  }

  override protected def doDeleteAll(request: RequestProtocol): Option[List[String]] = {
    _deleteAll(request)
  }

  protected def doDeleteAllWithoutTransaction(request: RequestProtocol): Option[List[String]] = {
    _deleteAllWithoutTransaction(request)
  }

  override protected def doDeleteByCondition(condition: String, request: RequestProtocol): Option[List[String]] = {
    _deleteByCondition(condition, request)
  }

  protected def doDeleteByConditionWithoutTransaction(condition: String, request: RequestProtocol): Option[List[String]] = {
    _deleteByConditionWithoutTransaction(condition, request)
  }

}

object JDBCService extends LazyLogging {

  def init(dbConfig: String): Unit = {
    JDBCStorable.init(dbConfig)
  }

}
