package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.protocol.{ResponseDTO, RequestProtocol,Response}
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.core.foundation.ModelConvertor._
import com.ecfront.lego.rbac.foundation.{Account, Role}

import scala.concurrent.Future

object RoleService extends JDBCService[Role] with ManageService {

  override protected def preSave(model: Role, request: RequestProtocol): ResponseDTO[Any] = {
    model.id = model.code + IdModel.SPLIT_FLAG + (if (!isSystem(request) || model.appId == null) request.appId else model.appId)
    Response.success(model)
  }

  override protected def convertToView(model: Role, request: RequestProtocol): ResponseDTO[Role] = {
    ResourceService.findResourceByRoleId(model.id, request)
    ResourceService.findResourceByRoleId(model.id, request, {
      resources =>
        model.resourceIds = resources.map(_.id)
        success(model)
    }, fail)
  }

  def findRoleByAccountId(accountId: String, request: RequestProtocol): Future[ResponseDTO[List[Role]]] = {
    findByCondition(
      s"${IdModel.ID_FLAG} in" +
        s" (SELECT ${Role._name + "_" + IdModel.ID_FLAG} FROM $REL_ROLE_ACCOUNT " +
        s"WHERE ${Account._name + "_" + IdModel.ID_FLAG} = '$accountId')" +
        s" ${appendAuth(request)}",
      request)
  }

  override protected def executeSave(model: Role, request: RequestProtocol): String = {
    JDBCService.db.open()
    executeSaveManyToManyRel(model.id, model.resourceIds, REL_ROLE_RESOURCE, request)
    executeSaveWithoutTransaction(model, request)
    JDBCService.db.commit()
    model.id
  }

  override protected def executeUpdate(id: String, model: Role, request: RequestProtocol): String = {
    JDBCService.db.open()
    executeUpdateManyToManyRel(id, model.resourceIds, REL_ROLE_RESOURCE, request)
    executeUpdateWithoutTransaction(id, model, request)
    JDBCService.db.commit()
    model.id
  }

  override protected def executeDeleteByCondition(condition: String, request: RequestProtocol): Unit = {
    JDBCService.db.open()
    executeDeleteManyToManyRel(REL_ROLE_RESOURCE, condition, request)
    executeDeleteByConditionWithoutTransaction(condition, request)
    JDBCService.db.commit()
  }

}
