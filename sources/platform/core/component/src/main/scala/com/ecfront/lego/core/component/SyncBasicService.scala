package com.ecfront.lego.core.component

import com.ecfront.lego.core.foundation.protocol.{Req, Resp}
import com.ecfront.storage.PageModel

trait SyncBasicService[M <: AnyRef] extends BasicService[M] {

  def getById(id: String, request: Req): Resp[M] = {
    executeGetById(id, request)
  }

  def getByCondition(condition: String, request: Req): Resp[M] = {
    executeGetByCondition(condition, request)
  }

  def findAll(request: Req): Resp[List[M]] = {
    executeFindAll(request)
  }

  def findByCondition(condition: String, request: Req): Resp[List[M]] = {
    executeFindByCondition(condition, request)
  }

  def pageAll(pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = {
    executePageAll(pageNumber, pageSize, request)
  }

  def pageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = {
    executePageByCondition(condition, pageNumber, pageSize, request)
  }

  def save(model: M, request: Req): Resp[String] = {
    executeSave(model, request)
  }

  def update(id: String, model: M, request: Req): Resp[String] = {
    executeUpdate(id, model, request)
  }

  def deleteById(id: String, request: Req): Resp[String] = {
    executeDeleteById(id, request)
  }

  def deleteByCondition(condition: String, request: Req): Resp[List[String]] = {
    executeDeleteByCondition(condition, request)
  }

  def deleteAll(request: Req): Resp[List[String]] = {
    executeDeleteAll(request)
  }

}



