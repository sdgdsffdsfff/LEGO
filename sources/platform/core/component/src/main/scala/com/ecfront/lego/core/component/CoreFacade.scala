package com.ecfront.lego.core.component

import com.ecfront.common.JsonHelper
import com.ecfront.lego.core.component.communication.Communication
import com.ecfront.lego.core.component.protocol.ResponseProtocol.log
import com.ecfront.lego.core.component.protocol._
import com.ecfront.lego.core.foundation._
import com.ecfront.storage.PageModel
import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}

trait CoreFacade[M <: AnyRef] extends BasicService[M] {

  protected val address: String

  logger.info( """Register address: %s""".format(address))

  override protected def doGetById(id: String, request: RequestProtocol): Option[M] = {
    CoreFacade.execute(address, "GET", Map[String, Any](IdModel.ID_FLAG -> id), null, modelClazz, request)
  }

  override protected def doGetByCondition(condition: String, request: RequestProtocol): Option[M] = {
    CoreFacade.execute(address, "GET", Map[String, Any](), condition, modelClazz, request)
  }

  override protected def doFindAll(request: RequestProtocol): Option[List[M]] = {
    CoreFacade.executeArray(address, "FIND", Map[String, Any](), null, modelClazz, request)
  }

  override protected def doFindByCondition(condition: String, request: RequestProtocol): Option[List[M]] = {
    CoreFacade.executeArray(address, "FIND", Map[String, Any](), condition, modelClazz, request)
  }

  override protected def doPageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = {
    CoreFacade.executePage(address, "FIND", Map[String, Any](), null, pageNumber, pageSize, modelClazz, request)
  }

  override protected def doPageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): Option[PageModel[M]] = {
    CoreFacade.executePage(address, "FIND", Map[String, Any](), condition, pageNumber, pageSize, modelClazz, request)
  }

  override protected def doSave(model: M, request: RequestProtocol): Option[String] = {
    CoreFacade.execute(address, "SAVE", Map[String, Any](), model, classOf[String], request)
  }

  override protected def doUpdate(id: String, model: M, request: RequestProtocol): Option[String] = {
    CoreFacade.execute(address, "UPDATE", Map[String, Any](IdModel.ID_FLAG -> id), model, classOf[String], request)
  }

  override protected def doDeleteById(id: String, request: RequestProtocol): Option[String] = {
    CoreFacade.execute(address, "DELETE", Map[String, String]("id" -> id), null, classOf[String], request)
  }

  override protected def doDeleteByCondition(condition: String, request: RequestProtocol): Option[List[String]] = {
    CoreFacade.execute(address, "DELETE", Map[String, Any](), condition, classOf[List[String]], request)
  }

  override protected def doDeleteAll(request: RequestProtocol): Option[List[String]] = {
    CoreFacade.execute(address, "DELETE", Map[String, Any](), null, classOf[List[String]], request)
  }

}


object CoreFacade extends LazyLogging {

  var communication: Communication = _

  def init(com: Communication): Unit = {
    communication = com
  }

  private def execute[M](address: String, action: String, parameters: Map[String, Any], body: Any, modelClazz: Class[M], request: RequestProtocol): Option[M] = {
    Await.result(doExecute[M, M](address, action, "N", parameters, body, modelClazz, request), Duration.Inf)
  }

  private def executePage[M](address: String, action: String, parameters: Map[String, Any], body: Any, pageNumber: Long, pageSize: Long, modelClazz: Class[M], request: RequestProtocol): Option[PageModel[M]] = {
    Await.result(doExecute[M, PageModel[M]](address, action, "P", parameters + (PageModel.PAGE_NUMBER_FLAG -> pageNumber) + (PageModel.PAGE_SIZE_FLAG -> pageSize), body, modelClazz, request), Duration.Inf)
  }

  private def executeArray[M](address: String, action: String, parameters: Map[String, Any], body: Any, modelClazz: Class[M], request: RequestProtocol): Option[List[M]] = {
    Await.result(doExecute[M, List[M]](address, action, "A", parameters, body, modelClazz, request), Duration.Inf)
  }

  private[this] def doExecute[M, C](address: String, action: String, returnType: String, parameters: Map[String, Any], body: Any, modelClazz: Class[M], request: RequestProtocol): Future[Option[C]] = {
    logger.debug("Execute cid: %s , address: %s , action: %s , userId: %s , appId: %s ".format(request.cId, address, action, request.userId, request.appId))
    //TODO rbac
    //TODO cache
    val p = Promise[Option[C]]()
    request.action = action
    request.parameters = parameters
    if (action == "SAVE" || action == "UPDATE") {
      request.body = JsonHelper.toJsonString(body)
    } else {
      request.body = body.asInstanceOf[String]
    }
    communication.send(address, request, {
      response =>
        if (response.code == StandardCode.SUCCESS_CODE) {
          returnType match {
            case "N" =>
              p.success(Some(JsonHelper.toObject(response.body, modelClazz).asInstanceOf[C]))
            case "P" =>
              p.success(Some(PageModel.toPage(response.body, modelClazz).asInstanceOf[C]))
            case "A" =>
              val result = ArrayBuffer[M]()
              val res = JsonHelper.toJson(response.body).elements()
              while (res.hasNext) {
                result += JsonHelper.toObject(res.next(), modelClazz)
              }
              p.success(Some(result.toList.asInstanceOf[C]))
          }
        } else {
          logger.error(response.failLog)
          p.failure(ResponseException(response.code, response.message))
        }
    }, {
      (code, message) =>
        logger.error(ResponseProtocol(code, message).failLog)
        p.failure(ResponseException(code, message))
    })
    p.future
  }

}
