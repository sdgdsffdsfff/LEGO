package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.core.foundation.ModelConvertor._
import com.ecfront.lego.rbac.foundation.{Account, Role}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object RoleService extends JDBCService[Role] with ManageService {

  override protected def preSave(model: Role, request: RequestProtocol): (Boolean, Any) = {
    model.id = model.code + IdModel.SPLIT_FLAG + (if (!isSystem(request) || model.appId == null) request.appId else model.appId)
    (true, model)
  }

  override protected def convertToView(model: Role, request: RequestProtocol): Option[Role] = {
    model.resourceIds = Await.result(ResourceService.findResourceByRoleId(model.id, request), Duration.Inf).get.map(_.id)
    Some(model)
  }

  def findRoleByAccountId(accountId: String, request: RequestProtocol): Future[Option[List[Role]]] = Future {
    executeFindByCondition(
      s"${IdModel.ID_FLAG} in" +
        s" (SELECT ${Role._name + "_" + IdModel.ID_FLAG} FROM $REL_ROLE_ACCOUNT " +
        s"WHERE ${Account._name + "_" + IdModel.ID_FLAG} = '$accountId')" +
        s" ${appendAuth(request)}",
      request)
  }

  override protected def doSave(model: Role, request: RequestProtocol): Option[String] = {
    JDBCService.db.open()
    doSaveManyToManyRel(model.id, model.resourceIds, REL_ROLE_RESOURCE, request)
    val result = doSaveWithoutTransaction(model, request)
    JDBCService.db.commit()
    result
  }

  override protected def doUpdate(id: String, model: Role, request: RequestProtocol): Option[String] = {
    JDBCService.db.open()
    doUpdateManyToManyRel(id, model.resourceIds, REL_ROLE_RESOURCE, request)
    val result = doUpdateWithoutTransaction(id, model, request)
    JDBCService.db.commit()
    result
  }

  override protected def doDeleteByCondition(condition: String, request: RequestProtocol): Option[List[String]] = {
    JDBCService.db.open()
    doDeleteManyToManyRel(REL_ROLE_RESOURCE, condition, request)
    val result = doDeleteByConditionWithoutTransaction(condition, request)
    JDBCService.db.commit()
    result
  }

}
