package com.ecfront.lego.core.component

import java.util.UUID

import com.ecfront.common.{BeanHelper, JsonHelper}
import com.ecfront.lego.core.component.communication.Communication
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.foundation._
import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.collection.mutable.ArrayBuffer

trait CoreFacade[M <: AnyRef] extends BasicService[M] {

  protected val address: String

  logger.info( """Register address: %s""".format(address))

  override protected def doGetById(id: String, request: RequestProtocol, success: => (M) => Unit, fail: => (String, String) => Unit = null): Unit = {
    CoreFacade.execute(address, "GET", Map[String, Any](IdModel.ID_FLAG -> id), null, modelClazz, request, success, fail)
  }

  override protected def doGetByCondition(condition: String, request: RequestProtocol, success: => (M) => Unit, fail: => (String, String) => Unit = null): Unit = {
    CoreFacade.execute(address, "GET", Map[String, Any](), condition, modelClazz, request, success, fail)
  }

  override protected def doFindAll(request: RequestProtocol, success: => (List[M]) => Unit, fail: => (String, String) => Unit = null): Unit = {
    CoreFacade.executeArray(address, "FIND", Map[String, Any](), null, modelClazz, request, success, fail)
  }

  override protected def doFindAll(pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => (PageModel[M]) => Unit, fail: => (String, String) => Unit = null): Unit = {
    CoreFacade.executePage(address, "FIND", Map[String, Any](), null, pageNumber, pageSize, modelClazz, request, success, fail)
  }

  override protected def doFindByCondition(condition: String, request: RequestProtocol, success: => (List[M]) => Unit, fail: => (String, String) => Unit = null): Unit = {
    CoreFacade.executeArray(address, "FIND", Map[String, Any](), condition, modelClazz, request, success, fail)
  }

  override protected def doFindByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => (PageModel[M]) => Unit, fail: => (String, String) => Unit = null): Unit = {
    CoreFacade.executePage(address, "FIND", Map[String, Any](), condition, pageNumber, pageSize, modelClazz, request, success, fail)
  }

  override protected def doSave(model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit = null): Unit = {
    model match {
      case tModel: IdModel =>
        tModel.id = UUID.randomUUID().toString
        CoreFacade.execute(address, "SAVE", Map[String, Any](), model, classOf[String], request, success, fail)
      case tModel: SecureModel =>
        tModel.id = UUID.randomUUID().toString
        if (tModel.createTime == 0) {
          tModel.createTime = System.currentTimeMillis()
        }
        if (tModel.createUser == null) {
          tModel.createUser = request.userId
        }
        CoreFacade.execute(address, "SAVE", Map[String, Any](), tModel, classOf[String], request, success, fail)
      case _ =>
        CoreFacade.execute(address, "SAVE", Map[String, Any](), model, classOf[String], request, success, fail)
    }
  }

  override protected def doUpdate(id: String, model: M, request: RequestProtocol, success: => (String) => Unit, fail: => (String, String) => Unit = null): Unit = {
    getById(id, request, {
      oldModel =>
        BeanHelper.copyProperties(model, oldModel)
        model match {
          case tModel: IdModel =>
            tModel.id = UUID.randomUUID().toString
            CoreFacade.execute(address, "UPDATE", Map[String, Any](IdModel.ID_FLAG -> id), model, classOf[String], request, success, fail)
          case tModel: SecureModel =>
            tModel.id = UUID.randomUUID().toString
            if (tModel.updateTime == 0) {
              tModel.updateTime = System.currentTimeMillis()
            }
            if (tModel.updateUser == null) {
              tModel.updateUser = request.userId
            }
            CoreFacade.execute(address, "UPDATE", Map[String, Any](IdModel.ID_FLAG -> id), model, classOf[String], request, success, fail)
          case _ =>
            CoreFacade.execute(address, "UPDATE", Map[String, Any](IdModel.ID_FLAG -> id), model, classOf[String], request, success, fail)
        }
    }, fail)
  }


  override protected def doDeleteById(id: String, request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit = {
    CoreFacade.execute(address, "DELETE", Map[String, String]("id" -> id), null, classOf[Unit], request, success, fail)
  }

  override protected def doDeleteByCondition(condition: String, request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit = {
    CoreFacade.execute(address, "DELETE", Map[String, Any](), condition, classOf[Unit], request, success, fail)
  }

  override protected def doDeleteAll(request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit = {
    CoreFacade.execute(address, "DELETE", Map[String, Any](), null, classOf[Unit], request, success, fail)
  }

}


object CoreFacade extends LazyLogging {

  var communication: Communication = _

  def init(com: Communication): Unit = {
    communication = com
  }

  private def execute[M](address: String, action: String, parameters: Map[String, Any], body: Any, modelClazz: Class[M], request: RequestProtocol, success: => M => Unit, fail: => (String, String) => Unit = null) {
    doExecute(address, action, "N", parameters, body, modelClazz, request, {
      result => success(result.asInstanceOf[M])
    }, fail)
  }

  private def executePage[M](address: String, action: String, parameters: Map[String, Any], body: Any, pageNumber: Long, pageSize: Long, modelClazz: Class[M], request: RequestProtocol, success: => PageModel[M] => Unit, fail: => (String, String) => Unit = null) {
    doExecute(address, action, "P", parameters + (PageModel.PAGE_NUMBER_FLAG -> pageNumber) + (PageModel.PAGE_SIZE_FLAG -> pageSize), body, modelClazz, request, {
      result => success(result.asInstanceOf[PageModel[M]])
    }, fail)
  }

  private def executeArray[M, C](address: String, action: String, parameters: Map[String, Any], body: Any, modelClazz: Class[M], request: RequestProtocol, success: => List[M] => Unit, fail: => (String, String) => Unit = null) {
    doExecute(address, action, "A", parameters, body, modelClazz, request, {
      result => success(result.asInstanceOf[List[M]])
    }, fail)
  }

  private[this] def doExecute[M, C](address: String, action: String, returnType: String, parameters: Map[String, Any], body: Any, modelClazz: Class[M], request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null) {
    logger.debug("Execute cid: %s , address: %s , action: %s , userId: %s , appId: %s ".format(request.cId, address, action, request.userId, request.appId))
    //TODO rbac
    //TODO cache
    request.action = action
    request.parameters = parameters
    if(action =="SAVE" || action =="UPDATE"){
      request.body=JsonHelper.toJsonString(body)
    }else{
      request.body=body.asInstanceOf[String]
    }

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
          if (fail != null) {}
          fail(response.code, response.message)
        }
    }, fail)
  }

}
