package com.ecfront.lego.core.component

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.storage.PageModel

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait FutureBasicService[M <: AnyRef] extends BasicService[M] {

  def getById(id: String, request: RequestProtocol): Future[Option[M]] = Future {
    executeGetById(id, request)
  }

  def getByCondition(condition: String, request: RequestProtocol): Future[Option[M]] = Future {
    executeGetByCondition(condition, request)
  }

  def findAll(request: RequestProtocol): Future[Option[List[M]]] = Future {
    executeFindAll(request)
  }

  def findByCondition(condition: String, request: RequestProtocol): Future[Option[List[M]]] = Future {
    executeFindByCondition(condition, request)
  }

  def pageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): Future[Option[PageModel[M]]] = Future {
    executePageAll(pageNumber, pageSize, request)
  }

  def pageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): Future[Option[PageModel[M]]] = Future {
    executePageByCondition(condition, pageNumber, pageSize, request)
  }

  def save(model: M, request: RequestProtocol): Future[Option[String]] = Future {
    executeSave(model, request)
  }

  def update(id: String, model: M, request: RequestProtocol): Future[Option[String]] = Future {
    executeUpdate(id, model, request)
  }

  def deleteById(id: String, request: RequestProtocol): Future[Option[String]] = Future {
    executeDeleteById(id, request)
  }

  def deleteByCondition(condition: String, request: RequestProtocol): Future[Option[List[String]]] = Future {
    executeDeleteByCondition(condition, request)
  }

  def deleteAll(request: RequestProtocol): Future[Option[List[String]]] = Future {
    executeDeleteAll(request)
  }

}



