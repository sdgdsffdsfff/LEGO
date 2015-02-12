package com.ecfront.lego.core.component

import java.lang.reflect.ParameterizedType
import java.util.UUID

import com.ecfront.common.BeanHelper
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.foundation.{AppSecureModel, IdModel, PageModel, SecureModel}
import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BasicService[M <: AnyRef] extends LazyLogging {

  protected val modelClazz = this.getClass.getGenericInterfaces()(0).asInstanceOf[ParameterizedType].getActualTypeArguments()(0).asInstanceOf[Class[M]]

  logger.info( """Create Service: model: %s""".format(modelClazz.getSimpleName))

  //=========================Common=========================

  protected def convertToView(model: M, request: RequestProtocol): Option[M] = {
    Some(model)
  }

  protected def convertToViews(models: List[M], request: RequestProtocol): Option[List[M]] = {
    Some(models)
  }

  protected def isSystem(request: RequestProtocol): Boolean = {
    AppSecureModel.LEGO_APP_FLAG == request.appId
  }

  protected def init(modelClazz: Class[M]): Unit

  init(modelClazz)

  //=========================GetByID=========================

  protected def preGetById(id: String, request: RequestProtocol): (Boolean, Any) = {
    (true, null)
  }

  protected def postGetById(result: Option[M], preResult: Any, request: RequestProtocol): Option[M] = {
    result
  }

  def getById(id: String, request: RequestProtocol): Future[Option[M]] = Future {
    executeGetById(id, request)
  }

  protected def executeGetById(id: String, request: RequestProtocol): Option[M] = {
    val (continue, preResult) = preGetById(id, request)
    if (continue) {
      val result = doGetById(id, request)
      if (result != null) {
        postGetById(convertToView(result.get, request), preResult, request)
      } else {
        postGetById(result, preResult, request)
      }
    } else {
      null
    }
  }

  protected def doGetById(id: String, request: RequestProtocol): Option[M] = ???


  //=========================GetByCondition=========================

  protected def preGetByCondition(condition: String, request: RequestProtocol): (Boolean, Any) = {
    (true, null)
  }

  protected def postGetByCondition(result: Option[M], preResult: Any, request: RequestProtocol): Option[M] = {
    result
  }

  def getByCondition(condition: String, request: RequestProtocol): Future[Option[M]] = Future {
    executeGetByCondition(condition, request)
  }

  protected def executeGetByCondition(condition: String, request: RequestProtocol): Option[M] = {
    val (continue, preResult) = preGetByCondition(condition, request)
    if (continue) {
      val result = doGetByCondition(condition, request)
      if (result != null) {
        postGetByCondition(convertToView(result.get, request), preResult, request)
      } else {
        postGetByCondition(result, preResult, request)
      }
    } else {
      null
    }
  }

  protected def doGetByCondition(condition: String, request: RequestProtocol): Option[M] = ???

  //=========================FindAll=========================

  protected def preFindAll(request: RequestProtocol): (Boolean, Any) = {
    (true, null)
  }

  protected def postFindAll(result: Option[List[M]], preResult: Any, request: RequestProtocol): Option[List[M]] = {
    result
  }

  def findAll(request: RequestProtocol): Future[Option[List[M]]] = Future {
    executeFindAll(request)
  }

  protected def executeFindAll(request: RequestProtocol): Option[List[M]] = {
    val (continue, preResult) = preFindAll(request)
    if (continue) {
      val result = doFindAll(request)
      if (result != null && result.nonEmpty) {
        postFindAll(convertToViews(result.get, request), preResult, request)
      } else {
        postFindAll(result, preResult, request)
      }
    } else {
      null
    }
  }

  protected def doFindAll(request: RequestProtocol): Option[List[M]] = ???

  //=========================FindByCondition=========================

  protected def preFindByCondition(condition: String, request: RequestProtocol): (Boolean, Any) = {
    (true, null)
  }

  protected def postFindByCondition(result: Option[List[M]], preResult: Any, request: RequestProtocol): Option[List[M]] = {
    result
  }

  def findByCondition(condition: String, request: RequestProtocol): Future[Option[List[M]]] = Future {
    executeFindByCondition(condition, request)
  }

  protected def executeFindByCondition(condition: String, request: RequestProtocol): Option[List[M]] = {
    val (continue, preResult) = preFindByCondition(condition, request)
    if (continue) {
      val result = doFindByCondition(condition, request)
      if (result != null && result.nonEmpty) {
        postFindByCondition(convertToViews(result.get, request), preResult, request)
      } else {
        postFindByCondition(result, preResult, request)
      }
    } else {
      null
    }
  }

  protected def doFindByCondition(condition: String, request: RequestProtocol): Option[List[M]] = ???

  //=========================PageAll=========================

  protected def prePageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): (Boolean, Any) = {
    (true, null)
  }

  protected def postPageAll(result: Option[PageModel[M]], preResult: Any, pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = {
    result
  }

  def pageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): Future[Option[PageModel[M]]] = Future {
    executePageAll(pageNumber, pageSize, request)
  }

  protected def executePageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = {
    val (continue, preResult) = prePageAll(pageNumber, pageSize, request)
    if (continue) {
      val result = doPageAll(pageNumber, pageSize, request)
      if (result != null && result.get.results.nonEmpty) {
        result.get.results = convertToViews(result.get.results, request).getOrElse(null)
      }
      postPageAll(result, preResult, pageNumber, pageSize, request)
    } else {
      null
    }
  }

  protected def doPageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = ???

  //=========================PageByCondition=========================

  protected def prePageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): (Boolean, Any) = {
    (true, null)
  }

  protected def postPageByCondition(result: Option[PageModel[M]], preResult: Any, pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = {
    result
  }

  def pageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): Future[Option[PageModel[M]]] = Future {
    executePageByCondition(condition, pageNumber, pageSize, request)
  }

  protected def executePageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = {
    val (continue, preResult) = prePageByCondition(condition, pageNumber, pageSize, request)
    if (continue) {
      val result = doPageByCondition(condition, pageNumber, pageSize, request)
      if (result != null && result.get.results.nonEmpty) {
        result.get.results = convertToViews(result.get.results, request).getOrElse(null)
      }
      postPageByCondition(result, preResult, pageNumber, pageSize, request)
    } else {
      null
    }
  }

  protected def doPageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = ???

  //=========================Save=========================

  protected def preSave(model: M, request: RequestProtocol): (Boolean, Any) = {
    (true, null)
  }

  protected def postSave(result: Option[String], preResult: Any, request: RequestProtocol): Option[String] = {
    result
  }

  def save(model: M, request: RequestProtocol): Future[Option[String]] = Future {
    executeSave(model, request)
  }

  protected def executeSave(model: M, request: RequestProtocol): Option[String] = {
    model match {
      case tModel: IdModel =>
        if (tModel.id == null) {
          tModel.id = UUID.randomUUID().toString
        }
      case _ =>
    }
    model match {
      case tModel: SecureModel =>
        tModel.createTime = System.currentTimeMillis()
        tModel.createUser = request.userId
        tModel.updateTime = System.currentTimeMillis()
        tModel.updateUser = request.userId
      case _ =>
    }
    model match {
      case tModel: AppSecureModel =>
        tModel.appId = request.appId
      case _ =>
    }
    val (continue, preResult) = preSave(model, request)
    if (continue) {
      postSave(doSave(model, request), preResult, request)
    } else {
      null
    }
  }

  protected def doSave(model: M, request: RequestProtocol): Option[String] = ???

  //=========================Update=========================

  protected def preUpdate(id: String, model: M, request: RequestProtocol): (Boolean, Any) = {
    (true, null)
  }

  protected def postUpdate(result: Option[String], preResult: Any, request: RequestProtocol): Option[String] = {
    result
  }

  def update(id: String, model: M, request: RequestProtocol): Future[Option[String]] = Future {
    executeUpdate(id, model, request)
  }

  protected def executeUpdate(id: String, model: M, request: RequestProtocol): Option[String] = {
    val getResult = doGetById(id, request)
    if (getResult != null) {
      BeanHelper.copyProperties(getResult.get, model)
      model match {
        case tModel: SecureModel if !isSystem(request) =>
          tModel.updateTime = System.currentTimeMillis()
          tModel.updateUser = request.userId
        case _ =>
      }
      model match {
        case tModel: AppSecureModel if !isSystem(request) =>
          tModel.appId = request.appId
        case _ =>
      }
      val (continue, preResult) = preUpdate(id, model, request)
      if (continue) {
        postUpdate(doUpdate(id, model, request), preResult, request)
      } else {
        null
      }
    } else {
      null
    }
  }

  protected def doUpdate(id: String, model: M, request: RequestProtocol): Option[String] = ???

  //=========================DeleteById=========================

  protected def preDeleteById(id: String, request: RequestProtocol): (Boolean, Any) = {
    (true, null)
  }

  protected def postDeleteById(result: Option[String], preResult: Any, request: RequestProtocol): Option[String] = {
    result
  }

  def deleteById(id: String, request: RequestProtocol): Future[Option[String]] = Future {
    executeDeleteById(id, request)
  }

  protected def executeDeleteById(id: String, request: RequestProtocol): Option[String] = {
    val (continue, preResult) = preDeleteById(id, request)
    if (continue) {
      postDeleteById(doDeleteById(id, request), preResult, request)
    } else {
      null
    }
  }

  protected def doDeleteById(id: String, request: RequestProtocol): Option[String] = ???

  //=========================DeleteByCondition=========================

  protected def preDeleteByCondition(condition: String, request: RequestProtocol): (Boolean, Any) = {
    (true, null)
  }

  protected def postDeleteByCondition(result: Option[List[String]], preResult: Any, request: RequestProtocol): Option[List[String]] = {
    result
  }

  def deleteByCondition(condition: String, request: RequestProtocol): Future[Option[List[String]]] = Future {
    executeDeleteByCondition(condition, request)
  }

  protected def executeDeleteByCondition(condition: String, request: RequestProtocol): Option[List[String]] = {
    val (continue, preResult) = preDeleteByCondition(condition, request)
    if (continue) {
      postDeleteByCondition(doDeleteByCondition(condition, request), preResult, request)
    } else {
      null
    }
  }

  protected def doDeleteByCondition(condition: String, request: RequestProtocol): Option[List[String]] = ???

  //=========================DeleteAll=========================

  protected def preDeleteAll(request: RequestProtocol): (Boolean, Any) = {
    (true, null)
  }

  protected def postDeleteAll(result: Option[List[String]], preResult: Any, request: RequestProtocol): Option[List[String]] = {
    result
  }

  def deleteAll(request: RequestProtocol): Future[Option[List[String]]] = Future {
    executeDeleteAll(request)
  }

  protected def executeDeleteAll(request: RequestProtocol): Option[List[String]] = {
    val (continue, preResult) = preDeleteAll(request)
    if (continue) {
      postDeleteAll(doDeleteAll(request), preResult, request)
    } else {
      null
    }
  }

  protected def doDeleteAll(request: RequestProtocol): Option[List[String]] = ???


}



