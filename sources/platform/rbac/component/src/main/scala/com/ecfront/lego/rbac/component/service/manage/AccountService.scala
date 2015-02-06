package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.{JDBCService, VertxStorageService}
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.rbac.foundation.Account

object AccountService extends VertxStorageService[Account] with ManageService {

  override protected def preSave(model: Account, request: RequestProtocol, success: => (Any) => Unit, fail: => (String, String) => Unit): Unit = {
    model.id = model.userId + IdModel.SPLIT_FLAG + (if (!isSystem(request) || model.appId == null) request.appId else model.appId)
    success(model)
  }

  override protected def convertToView(model: Account, request: RequestProtocol, success: => (Account) => Unit, fail: => (String, String) => Unit): Unit = {
    RoleService.findRoleByAccountId(model.id, request, {
      roles =>
        model.roleIds = roles.map(_.id)
        OrganizationService.findOrganizationByAccountId(model.id, request, {
          organizations =>
            model.organizationIds = organizations.map(_.id)
            success(model)
        }, fail)
    }, fail)
  }

  override protected def executeSave(model: Account, request: RequestProtocol): String = {
    JDBCService.db.open()
    executeSaveManyToManyRel(model.id, model.roleIds, REL_ROLE_ACCOUNT, request)
    executeSaveManyToManyRel(model.id, model.organizationIds, REL_ORGANIZATION_ACCOUNT, request)
    executeSaveWithoutTransaction(model, request)
    JDBCService.db.commit()
    model.id
  }

  override protected def executeUpdate(id: String, model: Account, request: RequestProtocol): String = {
    JDBCService.db.open()
    executeUpdateManyToManyRel(id, model.roleIds, REL_ROLE_ACCOUNT, request)
    executeUpdateManyToManyRel(id, model.organizationIds, REL_ORGANIZATION_ACCOUNT, request)
    executeUpdateWithoutTransaction(id, model, request)
    JDBCService.db.commit()
    model.id
  }

  override protected def executeDeleteByCondition(condition: String, request: RequestProtocol): Unit = {
    JDBCService.db.open()
    executeDeleteManyToManyRel(REL_ROLE_ACCOUNT, condition, request)
    executeDeleteManyToManyRel(REL_ORGANIZATION_ACCOUNT, condition, request)
    executeDeleteByConditionWithoutTransaction(condition, request)
    JDBCService.db.commit()
  }
}