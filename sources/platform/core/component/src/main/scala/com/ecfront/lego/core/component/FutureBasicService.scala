package com.ecfront.lego.core.component

import com.ecfront.lego.core.component.protocol.{Req, Resp}
import com.ecfront.storage.PageModel

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait FutureBasicService[M <: AnyRef] extends BasicService[M] {

  def getById(id: String, request: Req): Future[Resp[M]] = Future {
    executeGetById(id, request)
  }

  def getByCondition(condition: String, request: Req): Future[Resp[M]] = Future {
    executeGetByCondition(condition, request)
  }

  def findAll(request: Req): Future[Resp[List[M]]] = Future {
    executeFindAll(request)
  }

  def findByCondition(condition: String, request: Req): Future[Resp[List[M]]] = Future {
    executeFindByCondition(condition, request)
  }

  def pageAll(pageNumber: Long, pageSize: Long, request: Req): Future[Resp[PageModel[M]]] = Future {
    executePageAll(pageNumber, pageSize, request)
  }

  def pageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: Req): Future[Resp[PageModel[M]]] = Future {
    executePageByCondition(condition, pageNumber, pageSize, request)
  }

  def save(model: M, request: Req): Future[Resp[String]] = Future {
    executeSave(model, request)
  }

  def update(id: String, model: M, request: Req): Future[Resp[String]] = Future {
    executeUpdate(id, model, request)
  }

  def deleteById(id: String, request: Req): Future[Resp[String]] = Future {
    executeDeleteById(id, request)
  }

  def deleteByCondition(condition: String, request: Req): Future[Resp[List[String]]] = Future {
    executeDeleteByCondition(condition, request)
  }

  def deleteAll(request: Req): Future[Resp[List[String]]] = Future {
    executeDeleteAll(request)
  }

}



