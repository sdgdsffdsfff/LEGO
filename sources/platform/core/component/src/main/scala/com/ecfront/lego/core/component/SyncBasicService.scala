package com.ecfront.lego.core.component

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.storage.PageModel

trait SyncBasicService[M <: AnyRef] extends BasicService[M] {

  def getById(id: String, request: RequestProtocol): Option[M] = {
    executeGetById(id, request)
  }

  def getByCondition(condition: String, request: RequestProtocol): Option[M] = {
    executeGetByCondition(condition, request)
  }

  def findAll(request: RequestProtocol): Option[List[M]] = {
    executeFindAll(request)
  }

  def findByCondition(condition: String, request: RequestProtocol): Option[List[M]] = {
    executeFindByCondition(condition, request)
  }

  def pageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = {
    executePageAll(pageNumber, pageSize, request)
  }

  def pageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = {
    executePageByCondition(condition, pageNumber, pageSize, request)
  }

  def save(model: M, request: RequestProtocol): Option[String] = {
    executeSave(model, request)
  }

  def update(id: String, model: M, request: RequestProtocol): Option[String] = {
    executeUpdate(id, model, request)
  }

  def deleteById(id: String, request: RequestProtocol): Option[String] = {
    executeDeleteById(id, request)
  }

  def deleteByCondition(condition: String, request: RequestProtocol): Option[List[String]] = {
    executeDeleteByCondition(condition, request)
  }

  def deleteAll(request: RequestProtocol): Option[List[String]] = {
    executeDeleteAll(request)
  }

}



