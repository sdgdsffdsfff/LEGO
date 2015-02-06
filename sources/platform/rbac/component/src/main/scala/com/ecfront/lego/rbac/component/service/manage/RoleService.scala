package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.{JDBCService, VertxStorageService}
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.core.foundation.ModelConvertor._
import com.ecfront.lego.rbac.foundation.{Account, Role}

object RoleService extends VertxStorageService[Role] with ManageService {

  override protected def preSave(model: Role, request: RequestProtocol, success: => (Any) => Unit, fail: => (String, String) => Unit): Unit = {
    model.id = model.code + IdModel.SPLIT_FLAG + (if (!isSystem(request) || model.appId == null) request.appId else model.appId)
    success(model)
  }

  override protected def convertToView(model: Role, request: RequestProtocol, success: => (Role) => Unit, fail: => (String, String) => Unit): Unit = {
    ResourceService.findResourceByRoleId(model.id, request, {
      resources =>
        model.resourceIds = resources.map(_.id)
        success(model)
    }, fail)
  }

  def findRoleByAccountId(accountId: String, request: RequestProtocol, success: => List[Role] => Unit, fail: => (String, String) => Unit): Unit = {
    findByCondition(
      s"${IdModel.ID_FLAG} in" +
        s" (SELECT ${Role._name + "_" + IdModel.ID_FLAG} FROM $REL_ROLE_ACCOUNT " +
        s"WHERE ${Account._name + "_" + IdModel.ID_FLAG} = '$accountId')" +
        s" ${appendAuth(request)}",
      request, success, fail)
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
