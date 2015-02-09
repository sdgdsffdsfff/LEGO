package com.ecfront.lego.core.component

import java.lang.reflect.ParameterizedType
import java.util.UUID

import com.ecfront.common.BeanHelper
import com.ecfront.lego.core.component.protocol.Response.isSuccess
import com.ecfront.lego.core.component.protocol.{RequestProtocol, Response, ResponseDTO}
import com.ecfront.lego.core.foundation.{AppSecureModel, IdModel, PageModel, SecureModel}
import com.typesafe.scalalogging.slf4j.LazyLogging
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BasicService[M <: AnyRef] extends LazyLogging {

  protected val modelClazz = this.getClass.getGenericInterfaces()(0).asInstanceOf[ParameterizedType].getActualTypeArguments()(0).asInstanceOf[Class[M]]

  logger.info( """Create Service: model: %s""".format(modelClazz.getSimpleName))

  //=========================Common=========================

  protected def convertToView(model: M, request: RequestProtocol): ResponseDTO[M] = {
    Response.success(model)
  }

  protected def convertToViews(models: List[M], request: RequestProtocol): ResponseDTO[List[M]] = {
    Response.success(models)
  }

  protected def isSystem(request: RequestProtocol): Boolean = {
    AppSecureModel.LEGO_APP_FLAG == request.appId
  }

  protected def init(modelClazz: Class[M]): Unit

  init(modelClazz)

  //=========================GetByID=========================

  protected def preGetById(id: String, request: RequestProtocol): ResponseDTO[Any] = {
    Response.success(null)
  }

  protected def postGetById(result: M, preResult: Any, request: RequestProtocol): ResponseDTO[M] = {
    Response.success(result)
  }


  def getById(id: String, request: RequestProtocol): Future[ResponseDTO[M]] = Future {
    val preResult = preGetById(id, request)
    if (preResult) {
      val result = doGetById(id, request)
      if (result) {
        if (result.body != null) {
          val cvtResult = convertToView(result.body, request)
          if (cvtResult) {
            postGetById(cvtResult.body, preResult.body, request)
          } else {
            Response.fail(cvtResult.code, cvtResult.message)
          }
        } else {
          postGetById(result.body, preResult.body, request)
        }
      } else {
        Response.fail(result.code, result.message)
      }
    } else {
      Response.fail(preResult.code, preResult.message)
    }
  }

  protected def doGetById(id: String, request: RequestProtocol): ResponseDTO[M] = {
    Response.success(executeGetById(id, request))
  }

  protected def executeGetById(id: String, request: RequestProtocol): M = ???

  //=========================GetByCondition=========================

  protected def preGetByCondition(condition: String, request: RequestProtocol): ResponseDTO[Any] = {
    Response.success(null)
  }

  protected def postGetByCondition(result: M, preResult: Any, request: RequestProtocol): ResponseDTO[M] = {
    Response.success(result)
  }

  def getByCondition(condition: String, request: RequestProtocol): Future[ResponseDTO[M]] = Future {
    val preResult = preGetByCondition(condition, request)
    if (preResult) {
      val result = doGetByCondition(condition, request)
      if (result) {
        if (result.body != null) {
          val cvtResult = convertToView(result.body, request)
          if (cvtResult) {
            postGetByCondition(cvtResult.body, preResult.body, request)
          } else {
            Response.fail(cvtResult.code, cvtResult.message)
          }
        } else {
          postGetByCondition(result.body, preResult.body, request)
        }
      } else {
        Response.fail(result.code, result.message)
      }
    } else {
      Response.fail(preResult.code, preResult.message)
    }
  }

  protected def doGetByCondition(condition: String, request: RequestProtocol): ResponseDTO[M] = {
    Response.success(executeGetByCondition(condition, request))
  }

  protected def executeGetByCondition(condition: String, request: RequestProtocol): M = ???

  //=========================FindAll=========================

  protected def preFindAll(request: RequestProtocol): ResponseDTO[Any] = {
    Response.success(null)
  }

  protected def postFindAll(result: List[M], preResult: Any, request: RequestProtocol): ResponseDTO[List[M]] = {
    Response.success(result)
  }

  def findAll(request: RequestProtocol): Future[ResponseDTO[List[M]]] = Future {
    val preResult = preFindAll(request)
    if (preResult) {
      val result = doFindAll(request)
      if (result) {
        if (result.body != null) {
          val cvtResult = convertToViews(result.body, request)
          if (cvtResult) {
            postFindAll(cvtResult.body, preResult.body, request)
          } else {
            Response.fail(cvtResult.code, cvtResult.message)
          }
        } else {
          postFindAll(result.body, preResult.body, request)
        }
      } else {
        Response.fail(result.code, result.message)
      }
    } else {
      Response.fail(preResult.code, preResult.message)
    }
  }

  protected def doFindAll(request: RequestProtocol): ResponseDTO[List[M]] = {
    Response.success(executeFindAll(request))
  }

  protected def executeFindAll(request: RequestProtocol): List[M] = ???

  //=========================FindByCondition=========================

  protected def preFindByCondition(condition: String, request: RequestProtocol): ResponseDTO[Any] = {
    Response.success(null)
  }

  protected def postFindByCondition(result: List[M], preResult: Any, request: RequestProtocol): ResponseDTO[List[M]] = {
    Response.success(result)
  }

  def findByCondition(condition: String, request: RequestProtocol): Future[ResponseDTO[List[M]]] = Future {
    val preResult = preFindByCondition(condition, request)
    if (preResult) {
      val result = doFindByCondition(condition, request)
      if (result) {
        if (result.body != null) {
          val cvtResult = convertToViews(result.body, request)
          if (cvtResult) {
            postFindByCondition(cvtResult.body, preResult.body, request)
          } else {
            Response.fail(cvtResult.code, cvtResult.message)
          }
        } else {
          postFindByCondition(result.body, preResult.body, request)
        }
      } else {
        Response.fail(result.code, result.message)
      }
    } else {
      Response.fail(preResult.code, preResult.message)
    }
  }

  protected def doFindByCondition(condition: String, request: RequestProtocol): ResponseDTO[List[M]] = {
    Response.success(executeFindByCondition(condition, request))
  }


  protected def executeFindByCondition(condition: String, request: RequestProtocol): List[M] = ???

  //=========================PageAll=========================

  protected def prePageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): ResponseDTO[Any] = {
    Response.success(null)
  }

  protected def postPageAll(result: PageModel[M], preResult: Any, pageNumber: Long, pageSize: Long, request: RequestProtocol): ResponseDTO[PageModel[M]] = {
    Response.success(result)
  }

  def pageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): Future[ResponseDTO[PageModel[M]]] = Future {
    val preResult = prePageAll(pageNumber, pageSize, request)
    if (preResult) {
      val result = doPageAll(pageNumber, pageSize, request)
      if (result) {
        if (result.body != null) {
          val cvtResult = convertToViews(result.body.results, request)
          if (cvtResult) {
            postPageAll(result.body, preResult.body, pageNumber, pageSize, request)
          } else {
            Response.fail(cvtResult.code, cvtResult.message)
          }
        } else {
          postPageAll(result.body, preResult.body, pageNumber, pageSize, request)
        }
      } else {
        Response.fail(result.code, result.message)
      }
    } else {
      Response.fail(preResult.code, preResult.message)
    }
  }

  protected def doPageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): ResponseDTO[PageModel[M]] = {
    Response.success(executePageAll(pageNumber, pageSize, request))
  }

  protected def executePageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): PageModel[M] = ???

  //=========================PageByCondition=========================

  protected def prePageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): ResponseDTO[Any] = {
    Response.success(null)
  }

  protected def postPageByCondition(result: PageModel[M], preResult: Any, pageNumber: Long, pageSize: Long, request: RequestProtocol): ResponseDTO[PageModel[M]] = {
    Response.success(result)
  }

  def pageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): Future[ResponseDTO[PageModel[M]]] = Future {
    val preResult = prePageByCondition(condition, pageNumber, pageSize, request)
    if (preResult) {
      val result = doPageByCondition(condition, pageNumber, pageSize, request)
      if (result) {
        if (result.body != null) {
          val cvtResult = convertToViews(result.body.results, request)
          if (cvtResult) {
            postPageByCondition(result.body, preResult.body, pageNumber, pageSize, request)
          } else {
            Response.fail(cvtResult.code, cvtResult.message)
          }
        } else {
          postPageByCondition(result.body, preResult.body, pageNumber, pageSize, request)
        }
      } else {
        Response.fail(result.code, result.message)
      }
    } else {
      Response.fail(preResult.code, preResult.message)
    }
  }

  protected def doPageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): ResponseDTO[PageModel[M]] = {
    Response.success(executePageByCondition(condition, pageNumber, pageSize, request))
  }

  protected def executePageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): PageModel[M] = ???

  //=========================Save=========================

  protected def preSave(model: M, request: RequestProtocol): ResponseDTO[Any] = {
    Response.success(null)
  }

  protected def postSave(result: String, preResult: Any, request: RequestProtocol): ResponseDTO[String] = {
    Response.success(result)
  }

  def save(model: M, request: RequestProtocol): Future[ResponseDTO[String]] = Future {
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
    val preResult = preSave(model, request)
    if (preResult) {
      val result = doSave(model, request)
      if (result) {
        postSave(result.body, preResult.body, request)
      } else {
        Response.fail(result.code, result.message)
      }
    } else {
      Response.fail(preResult.code, preResult.message)
    }
  }

  protected def doSave(model: M, request: RequestProtocol): ResponseDTO[String] = {
    Response.success(executeSave(model, request))
  }

  protected def executeSave(model: M, request: RequestProtocol): String = ???

  //=========================Update=========================

  protected def preUpdate(id: String, model: M, request: RequestProtocol): ResponseDTO[Any] = {
    Response.success(null)
  }

  protected def postUpdate(result: String, preResult: Any, request: RequestProtocol): ResponseDTO[String] = {
    Response.success(result)
  }

  def update(id: String, model: M, request: RequestProtocol): Future[ResponseDTO[String]] = Future {
    val getResult = doGetById(id, request)
    if (getResult) {
      BeanHelper.copyProperties(getResult.body, model)
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
      val preResult = preUpdate(id, model, request)
      if (preResult) {
        val result = doUpdate(id, model, request)
        if (result) {
          postUpdate(result.body, preResult.body, request)
        } else {
          Response.fail(result.code, result.message)
        }
      } else {
        Response.fail(preResult.code, preResult.message)
      }
    } else {
      Response.fail(getResult.code, getResult.message)
    }
  }

  protected def doUpdate(id: String, model: M, request: RequestProtocol): ResponseDTO[String] = {
    Response.success(executeUpdate(id, model, request))
  }

  protected def executeUpdate(id: String, model: M, request: RequestProtocol): String = ???

  //=========================DeleteById=========================

  protected def preDeleteById(id: String, request: RequestProtocol): ResponseDTO[Any] = {
    Response.success(null)
  }

  protected def postDeleteById(result: String, preResult: Any, request: RequestProtocol): ResponseDTO[String] = {
    Response.success(result)
  }

  def deleteById(id: String, request: RequestProtocol): Future[ResponseDTO[String]] = Future {
    val preResult = preDeleteById(id, request)
    if (preResult) {
      val result = doDeleteById(id, request)
      if (result) {
        postDeleteById(result.body, preResult.body, request)
      } else {
        Response.fail(result.code, result.message)
      }
    } else {
      Response.fail(preResult.code, preResult.message)
    }
  }

  protected def doDeleteById(id: String, request: RequestProtocol): ResponseDTO[String] = {
    Response.success(executeDeleteById(id, request))
  }

  protected def executeDeleteById(id: String, request: RequestProtocol): String = ???

  //=========================DeleteByCondition=========================

  protected def preDeleteByCondition(condition: String, request: RequestProtocol): ResponseDTO[Any] = {
    Response.success(null)
  }

  protected def postDeleteByCondition(result: List[String], preResult: Any, request: RequestProtocol): ResponseDTO[List[String]] = {
    Response.success(result)
  }

  def deleteByCondition(condition: String, request: RequestProtocol): Future[ResponseDTO[List[String]]] = Future {
    val preResult = preDeleteByCondition(condition, request)
    if (preResult) {
      val result = doDeleteByCondition(condition, request)
      if (result) {
        postDeleteByCondition(result.body, preResult.body, request)
      } else {
        Response.fail(result.code, result.message)
      }
    } else {
      Response.fail(preResult.code, preResult.message)
    }
  }

  protected def doDeleteByCondition(condition: String, request: RequestProtocol): ResponseDTO[List[String]] = {
    Response.success(executeDeleteByCondition(condition, request))
  }

  protected def executeDeleteByCondition(condition: String, request: RequestProtocol): List[String] = ???

  //=========================DeleteAll=========================

  protected def preDeleteAll(request: RequestProtocol): ResponseDTO[Any] = {
    Response.success(null)
  }

  protected def postDeleteAll(result: List[String], preResult: Any, request: RequestProtocol): ResponseDTO[List[String]] = {
    Response.success(result)
  }

  def deleteAll(request: RequestProtocol): Future[ResponseDTO[List[String]]] = Future {
    val preResult = preDeleteAll(request)
    if (preResult) {
      val result = doDeleteAll(request)
      if (result) {
        postDeleteAll(result.body, preResult.body, request)
      } else {
        Response.fail(result.code, result.message)
      }
    } else {
      Response.fail(preResult.code, preResult.message)
    }
  }

  protected def doDeleteAll(request: RequestProtocol): ResponseDTO[List[String]] = {
    Response.success(executeDeleteAll(request))
  }

  protected def executeDeleteAll(request: RequestProtocol): List[String] = ???


}



