package com.ecfront.lego.core.component

import java.lang.reflect.ParameterizedType
import java.util.UUID

import com.ecfront.common.BeanHelper
import com.ecfront.lego.core.component.protocol.{Req, Resp}
import com.ecfront.lego.core.foundation.{AppSecureModel, IdModel, SecureModel, StandardCode}
import com.ecfront.storage.PageModel
import com.typesafe.scalalogging.slf4j.LazyLogging

trait BasicService[M <: AnyRef] extends LazyLogging {

  protected val modelClazz = this.getClass.getGenericInterfaces()(0).asInstanceOf[ParameterizedType].getActualTypeArguments()(0).asInstanceOf[Class[M]]

  logger.info( """Create Service: model: %s""".format(modelClazz.getSimpleName))

  //=========================Common=========================

  protected def convertToView(model: M, request: Req): M = {
    model
  }

  protected def convertToViews(models: List[M], request: Req): List[M] = {
    models
  }

  protected def isSystem(request: Req): Boolean = {
    AppSecureModel.LEGO_APP_FLAG == request.appId
  }

  //=========================GetByID=========================

  protected def preGetById(id: String, request: Req): Resp[Any] = {
    Resp.success(null)
  }

  protected def postGetById(result: M, preResult: Any, request: Req): Resp[M] = {
    Resp.success(result)
  }

  protected def executeGetById(id: String, request: Req): Resp[M] = {
    val preResult = preGetById(id, request)
    if (preResult) {
      val result = doGetById(id, request)
      if (result) {
        postGetById(convertToView(result.body, request), preResult.body, request)
      } else {
        Resp.fail(result.code, result.message)
      }
    } else {
      Resp.fail(preResult.code, preResult.message)
    }
  }

  protected def doGetById(id: String, request: Req): Resp[M]

  //=========================GetByCondition=========================

  protected def preGetByCondition(condition: String, request: Req): Resp[Any] = {
    Resp.success(null)
  }

  protected def postGetByCondition(result: M, preResult: Any, request: Req): Resp[M] = {
    Resp.success(result)
  }

  protected def executeGetByCondition(condition: String, request: Req): Resp[M] = {
    val preResult = preGetByCondition(condition, request)
    if (preResult) {
      val result = doGetByCondition(condition, request)
      if (result) {
        postGetByCondition(convertToView(result.body, request), preResult.body, request)
      } else {
        Resp.fail(result.code, result.message)
      }
    } else {
      Resp.fail(preResult.code, preResult.message)
    }
  }

  protected def doGetByCondition(condition: String, request: Req): Resp[M]

  //=========================FindAll=========================

  protected def preFindAll(request: Req): Resp[Any] = {
    Resp.success(null)
  }

  protected def postFindAll(result: List[M], preResult: Any, request: Req): Resp[List[M]] = {
    Resp.success(result)
  }

  protected def executeFindAll(request: Req): Resp[List[M]] = {
    val preResult = preFindAll(request)
    if (preResult) {
      val result = doFindAll(request)
      if (result) {
        postFindAll(convertToViews(result.body, request), preResult.body, request)
      } else {
        Resp.fail(result.code, result.message)
      }
    } else {
      Resp.fail(preResult.code, preResult.message)
    }
  }

  protected def doFindAll(request: Req): Resp[List[M]]

  //=========================FindByCondition=========================

  protected def preFindByCondition(condition: String, request: Req): Resp[Any] = {
    Resp.success(null)
  }

  protected def postFindByCondition(result: List[M], preResult: Any, request: Req): Resp[List[M]] = {
    Resp.success(result)
  }

  protected def executeFindByCondition(condition: String, request: Req): Resp[List[M]] = {
    val preResult = preFindByCondition(condition, request)
    if (preResult) {
      val result = doFindByCondition(condition, request)
      if (result) {
        postFindByCondition(convertToViews(result.body, request), preResult.body, request)
      } else {
        Resp.fail(result.code, result.message)
      }
    } else {
      Resp.fail(preResult.code, preResult.message)
    }
  }

  protected def doFindByCondition(condition: String, request: Req): Resp[List[M]]

  //=========================PageAll=========================

  protected def prePageAll(pageNumber: Long, pageSize: Long, request: Req): Resp[Any] = {
    Resp.success(null)
  }

  protected def postPageAll(result: PageModel[M], preResult: Any, pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = {
    Resp.success(result)
  }

  protected def executePageAll(pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = {
    val preResult = prePageAll(pageNumber, pageSize, request)
    if (preResult) {
      val result = doPageAll(pageNumber, pageSize, request)
      if (result) {
        result.body.setResults(convertToViews(result.body.results, request))
        postPageAll(result.body, preResult.body, pageNumber, pageSize, request)
      } else {
        Resp.fail(result.code, result.message)
      }
    } else {
      Resp.fail(preResult.code, preResult.message)
    }
  }

  protected def doPageAll(pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]]

  //=========================PageByCondition=========================

  protected def prePageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: Req): Resp[Any] = {
    Resp.success(null)
  }

  protected def postPageByCondition(result: PageModel[M], preResult: Any, pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = {
    Resp.success(result)
  }

  protected def executePageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = {
    val preResult = prePageByCondition(condition, pageNumber, pageSize, request)
    if (preResult) {
      val result = doPageByCondition(condition, pageNumber, pageSize, request)
      if (result) {
        result.body.setResults(convertToViews(result.body.results, request))
        postPageByCondition(result.body, preResult.body, pageNumber, pageSize, request)
      } else {
        Resp.fail(result.code, result.message)
      }
    } else {
      Resp.fail(preResult.code, preResult.message)
    }
  }

  protected def doPageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]]

  //=========================Save=========================

  protected def preSave(model: M, request: Req): Resp[Any] = {
    Resp.success(null)
  }

  protected def postSave(result: String, preResult: Any, request: Req): Resp[String] = {
    Resp.success(result)
  }

  protected def executeSave(model: M, request: Req): Resp[String] = {
    model match {
      case idModel: IdModel =>
        if (idModel.id == null) {
          idModel.id = UUID.randomUUID().toString
        } else {
          if (doGetById(idModel.id, request).body != null) {
            return Resp.fail(StandardCode.BAD_REQUEST_CODE, "Id exist :" + idModel.id)
          }
        }
        idModel match {
          case secureModel: SecureModel =>
            secureModel.createTime = System.currentTimeMillis()
            secureModel.createUser = request.accountId
            secureModel.updateTime = System.currentTimeMillis()
            secureModel.updateUser = request.accountId
            secureModel match {
              case appSecureModel: AppSecureModel =>
                appSecureModel.appId = request.appId
              case _ =>
            }
          case _ =>
        }
      case _ =>
    }
    val preResult = preSave(model, request)
    if (preResult) {
      val result = doSave(model, request)
      if (result) {
        postSave(result.body, preResult.body, request)
      } else {
        Resp.fail(result.code, result.message)
      }
    } else {
      Resp.fail(preResult.code, preResult.message)
    }
  }

  protected def doSave(model: M, request: Req): Resp[String]

  //=========================Update=========================

  protected def preUpdate(id: String, model: M, request: Req): Resp[Any] = {
    Resp.success(null)
  }

  protected def postUpdate(result: String, preResult: Any, request: Req): Resp[String] = {
    Resp.success(result)
  }

  protected def executeUpdate(id: String, model: M, request: Req): Resp[String] = {
    val getResult = doGetById(id, request)
    if (getResult) {
      BeanHelper.copyProperties(getResult.body, model)
      model match {
        case idModel: IdModel =>
          idModel.id=id
          if(!isSystem(request)){
            idModel match {
              case secureModel: SecureModel=>
                secureModel.updateTime = System.currentTimeMillis()
                secureModel.updateUser = request.accountId
                secureModel match {
                  case appSecureModel: AppSecureModel if !isSystem(request) =>
                    appSecureModel.appId = request.appId
                  case _ =>
                }
              case _ =>
            }
          }
        case _ =>
      }
      val preResult = preUpdate(id, model, request)
      if (preResult) {
        val result = doUpdate(id, model, request)
        if (result) {
          postUpdate(result.body, preResult.body, request)
        } else {
          Resp.fail(result.code, result.message)
        }
      } else {
        Resp.fail(preResult.code, preResult.message)
      }
    } else {
      Resp.fail(getResult.code, getResult.message)
    }
  }

  protected def doUpdate(id: String, model: M, request: Req): Resp[String]

  //=========================DeleteById=========================

  protected def preDeleteById(id: String, request: Req): Resp[Any] = {
    Resp.success(null)
  }

  protected def postDeleteById(result: String, preResult: Any, request: Req): Resp[String] = {
    Resp.success(result)
  }

  protected def executeDeleteById(id: String, request: Req): Resp[String] = {
    val preResult = preDeleteById(id, request)
    if (preResult) {
      val result = doDeleteById(id, request)
      if (result) {
        postDeleteById(result.body, preResult.body, request)
      } else {
        Resp.fail(result.code, result.message)
      }
    } else {
      Resp.fail(preResult.code, preResult.message)
    }
  }

  protected def doDeleteById(id: String, request: Req): Resp[String]

  //=========================DeleteByCondition=========================

  protected def preDeleteByCondition(condition: String, request: Req): Resp[Any] = {
    Resp.success(null)
  }

  protected def postDeleteByCondition(result: List[String], preResult: Any, request: Req): Resp[List[String]] = {
    Resp.success(result)
  }

  protected def executeDeleteByCondition(condition: String, request: Req): Resp[List[String]] = {
    val preResult = preDeleteByCondition(condition, request)
    if (preResult) {
      val result = doDeleteByCondition(condition, request)
      if (result) {
        postDeleteByCondition(result.body, preResult.body, request)
      } else {
        Resp.fail(result.code, result.message)
      }
    } else {
      Resp.fail(preResult.code, preResult.message)
    }
  }

  protected def doDeleteByCondition(condition: String, request: Req): Resp[List[String]]

  //=========================DeleteAll=========================

  protected def preDeleteAll(request: Req): Resp[Any] = {
    Resp.success(null)
  }

  protected def postDeleteAll(result: List[String], preResult: Any, request: Req): Resp[List[String]] = {
    Resp.success(result)
  }

  protected def executeDeleteAll(request: Req): Resp[List[String]] = {
    val preResult = preDeleteAll(request)
    if (preResult) {
      val result = doDeleteAll(request)
      if (result) {
        postDeleteAll(result.body, preResult.body, request)
      } else {
        Resp.fail(result.code, result.message)
      }
    } else {
      Resp.fail(preResult.code, preResult.message)
    }
  }

  protected def doDeleteAll(request: Req): Resp[List[String]]

}



