package com.ecfront.lego.core.component

import com.ecfront.common.JsonHelper
import com.ecfront.lego.core.component.communication.Communication
import com.ecfront.lego.core.foundation.protocol._
import com.ecfront.lego.core.foundation._
import com.ecfront.storage.PageModel
import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}

trait CoreFacade[M <: AnyRef] extends BasicService[M] {

  protected val address: String

  logger.info( """Register address: %s""".format(address))

  override protected def doGetById(id: String, request: Req): Resp[M] = {
    CoreFacade.execute(address, "GET", Map[String, Any](IdModel.ID_FLAG -> id), null, modelClazz, request)
  }

  override protected def doGetByCondition(condition: String, request: Req): Resp[M] = {
    CoreFacade.execute(address, "GET", Map[String, Any](), condition, modelClazz, request)
  }

  override protected def doFindAll(request: Req): Resp[List[M]] = {
    CoreFacade.executeArray(address, "FIND", Map[String, Any](), null, modelClazz, request)
  }

  override protected def doFindByCondition(condition: String, request: Req): Resp[List[M]] = {
    CoreFacade.executeArray(address, "FIND", Map[String, Any](), condition, modelClazz, request)
  }

  override protected def doPageAll(pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = {
    CoreFacade.executePage(address, "FIND", Map[String, Any](), null, pageNumber, pageSize, modelClazz, request)
  }

  override protected def doPageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = {
    CoreFacade.executePage(address, "FIND", Map[String, Any](), condition, pageNumber, pageSize, modelClazz, request)
  }

  override protected def doSave(model: M, request: Req): Resp[String] = {
    CoreFacade.execute(address, "SAVE", Map[String, Any](), model, classOf[String], request)
  }

  override protected def doUpdate(id: String, model: M, request: Req): Resp[String] = {
    CoreFacade.execute(address, "UPDATE", Map[String, Any](IdModel.ID_FLAG -> id), model, classOf[String], request)
  }

  override protected def doDeleteById(id: String, request: Req): Resp[String] = {
    CoreFacade.execute(address, "DELETE", Map[String, String]("id" -> id), null, classOf[String], request)
  }

  override protected def doDeleteByCondition(condition: String, request: Req): Resp[List[String]] = {
    CoreFacade.execute(address, "DELETE", Map[String, Any](), condition, classOf[List[String]], request)
  }

  override protected def doDeleteAll(request: Req): Resp[List[String]] = {
    CoreFacade.execute(address, "DELETE", Map[String, Any](), null, classOf[List[String]], request)
  }

}


object CoreFacade extends LazyLogging {

  var communication: Communication = _

  def init(com: Communication): Unit = {
    communication = com
  }

  private def execute[M](address: String, action: String, parameters: Map[String, Any], body: Any, modelClazz: Class[M], request: Req): Resp[M] = {
    Await.result(doExecute[M, M](address, action, "N", parameters, body, modelClazz, request), Duration.Inf)
  }

  private def executePage[M](address: String, action: String, parameters: Map[String, Any], body: Any, pageNumber: Long, pageSize: Long, modelClazz: Class[M], request: Req): Resp[PageModel[M]] = {
    Await.result(doExecute[M, PageModel[M]](address, action, "P", parameters + (PageModel.PAGE_NUMBER_FLAG -> pageNumber) + (PageModel.PAGE_SIZE_FLAG -> pageSize), body, modelClazz, request), Duration.Inf)
  }

  private def executeArray[M](address: String, action: String, parameters: Map[String, Any], body: Any, modelClazz: Class[M], request: Req): Resp[List[M]] = {
    Await.result(doExecute[M, List[M]](address, action, "A", parameters, body, modelClazz, request), Duration.Inf)
  }

  private[this] def doExecute[M, C](address: String, action: String, returnType: String, parameters: Map[String, Any], body: Any, modelClazz: Class[M], request: Req): Future[Resp[C]] = {
    logger.debug("Execute cid: %s , address: %s , action: %s , userId: %s , appId: %s ".format(request.cId, address, action, request.accountId, request.appId))
    //TODO rbac
    //TODO cache
    val p = Promise[Resp[C]]()
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
              p.success(Resp.success(JsonHelper.toObject(response.body, modelClazz).asInstanceOf[C]))
            case "P" =>
              p.success(Resp.success(PageModel.toPage(response.body, modelClazz).asInstanceOf[C]))
            case "A" =>
              val result = ArrayBuffer[M]()
              val res = JsonHelper.toJson(response.body).elements()
              while (res.hasNext) {
                result += JsonHelper.toObject(res.next(), modelClazz)
              }
              p.success(Resp.success(result.toList.asInstanceOf[C]))
          }
        } else {
          p.success(Resp.fail(response.code, response.message))
        }
    }, {
      (code, message) =>
        p.success(Resp.fail(code, message))
    })
    p.future
  }

}
