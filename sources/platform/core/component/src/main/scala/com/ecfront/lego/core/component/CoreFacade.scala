package com.ecfront.lego.core.component

import java.util.UUID

import com.ecfront.common.{BeanHelper, JsonHelper}
import com.ecfront.lego.core.component.communication.Communication
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.foundation.{IdModel, PageModel, SecureModel, StandardCode}
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.collection.mutable.ArrayBuffer

trait CoreFacade[M <: IdModel] extends BasicService[M] {

  override protected def doGetById(id: String, request: RequestProtocol, success: => (M) => Unit, fail: => (String, String) => Unit =null): Unit = {
    CoreFacade.execute(address, "GET", JsonHelper.createObjectNode().put("id", id), modelClazz, request, success, fail)
  }

  override protected def doGetByCondition(condition: ObjectNode, request: RequestProtocol, success: => (M) => Unit, fail: => (String, String) => Unit =null): Unit = {
    CoreFacade.execute(address, "GET", condition, modelClazz, request, success, fail)
  }

  override protected def doFindAll(request: RequestProtocol, success: => (List[M]) => Unit, fail: => (String, String) => Unit =null): Unit = {
    CoreFacade.executeArray(address, "FIND", JsonHelper.createObjectNode(), modelClazz, request, success, fail)
  }

  override protected def doFindAll(pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => (PageModel[M]) => Unit, fail: => (String, String) => Unit =null): Unit = {
    CoreFacade.executePage(address, "FIND", JsonHelper.createObjectNode(), pageNumber, pageSize, modelClazz, request, success, fail)
  }

  override protected def doFindByCondition(condition: ObjectNode, request: RequestProtocol, success: => (List[M]) => Unit, fail: => (String, String) => Unit =null): Unit = {
    CoreFacade.executeArray(address, "FIND", condition, modelClazz, request, success, fail)
  }

  override protected def doFindByCondition(condition: ObjectNode, pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => (PageModel[M]) => Unit, fail: => (String, String) => Unit =null): Unit = {
    CoreFacade.executePage(address, "FIND", condition, pageNumber, pageSize, modelClazz, request, success, fail)
  }

  override protected def doSave(model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit =null): Unit = {
    if (model.id == null) {
      model.id = UUID.randomUUID().toString
    }
    model match {
      case tModel: SecureModel =>
        if (tModel.createTime == 0) {
          tModel.createTime = System.currentTimeMillis()
        }
        if (tModel.createUser == null) {
          tModel.createUser = request.userId
        }
        CoreFacade.execute(address, "SAVE", JsonHelper.toJson(tModel), classOf[String], request, success, fail)
      case _ =>
        CoreFacade.execute(address, "SAVE", JsonHelper.toJson(model), classOf[String], request, success, fail)
    }
  }

  override protected def doUpdate(id: String, model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit =null): Unit = {
    getById(id, request, {
      oldModel =>
        BeanHelper.copyProperties(model, oldModel)
        model.id = id
        model match {
          case tModel: SecureModel =>
            if (tModel.updateTime == 0) {
              tModel.updateTime = System.currentTimeMillis()
            }
            if (tModel.updateUser == null) {
              tModel.updateUser = request.userId
            }
            CoreFacade.execute(address, "UPDATE", JsonHelper.toJson(tModel), classOf[String], request, success, fail)
          case _ =>
            CoreFacade.execute(address, "UPDATE", JsonHelper.toJson(model), classOf[String], request, success, fail)
        }
    }, fail)
  }


  override protected def doDeleteById(id: String, request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit =null): Unit = {
    CoreFacade.execute(address, "DELETE", JsonHelper.createObjectNode().put("id", id), classOf[Unit], request, success, fail)
  }

  override protected def doDeleteByCondition(condition: ObjectNode, request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit =null): Unit = {
    CoreFacade.execute(address, "DELETE", condition, classOf[Unit], request, success, fail)
  }

  override protected def doDeleteAll(request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit =null): Unit = {
    CoreFacade.execute(address, "DELETE", JsonHelper.createObjectNode(), classOf[Unit], request, success, fail)
  }

}


object CoreFacade extends LazyLogging {

  var communication: Communication = _

  def init(com: Communication): Unit = {
    communication = com
  }

  private def execute[M](address: String, action: String, body: JsonNode, modelClazz: Class[M], request: RequestProtocol, success: => M => Unit, fail: => (String, String) => Unit =null) {
    doExecute(address, action, "N", body, modelClazz, request, {
      result => success(result.asInstanceOf[M])
    }, fail)
  }

  private def executePage[M](address: String, action: String, body: JsonNode, pageNumber: Long, pageSize: Long, modelClazz: Class[M], request: RequestProtocol, success: => PageModel[M] => Unit, fail: => (String, String) => Unit =null) {
    body.asInstanceOf[ObjectNode].put(PageModel.PAGE_NUMBER_FLAG, pageNumber).put(PageModel.PAGE_SIZE_FLAG, pageSize)
    doExecute(address, action, "P", body, modelClazz, request, {
      result => success(result.asInstanceOf[PageModel[M]])
    }, fail)
  }

  private def executeArray[M](address: String, action: String, body: JsonNode, modelClazz: Class[M], request: RequestProtocol, success: => List[M] => Unit, fail: => (String, String) => Unit =null) {
    doExecute(address, action, "A", body, modelClazz, request, {
      result => success(result.asInstanceOf[List[M]])
    }, fail)
  }

  private[this] def doExecute[M](address: String, action: String, returnType: String, body: JsonNode, modelClazz: Class[M], request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit =null) {
    logger.debug("Execute cid: %s , address: %s , action: %s , userId: %s , appId: %s ".format(request.cId, address, action, request.userId, request.appId))
    //TODO rbac
    //TODO cache
    request.action = action
    request.body = body
    communication.send(address, request, {
      response =>
        if (response.code == StandardCode.SUCCESS_CODE) {
          returnType match {
            case "N" =>
              success(JsonHelper.toObject(response.body, modelClazz))
            case "P" =>
              success(PageModel.toPage(response.body, modelClazz))
            case "A" =>
              val result = ArrayBuffer[M]()
              val res = JsonHelper.toJson(response.body).elements()
              while (res.hasNext) {
                result += JsonHelper.toObject(res.next(), modelClazz)
              }
              success(result.toList)
          }
        } else {
          if(fail!=null){}
          fail(response.code, response.message)
        }
    }, fail)
  }

}
