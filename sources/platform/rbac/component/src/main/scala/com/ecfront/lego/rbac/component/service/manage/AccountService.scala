package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.rbac.foundation.Account

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AccountService extends JDBCService[Account] with ManageService {

  override protected def preSave(model: Account, request: RequestProtocol): (Boolean, Any) = {
    model.id = model.userId + IdModel.SPLIT_FLAG + (if (!isSystem(request) || model.appId == null) request.appId else model.appId)
    (true, model)
  }

  override protected def convertToView(model: Account, request: RequestProtocol): Option[Account] = {
    model.roleIds = Await.result(RoleService.findRoleByAccountId(model.id, request), Duration.Inf).get.map(_.id)
    model.organizationIds = Await.result(OrganizationService.findOrganizationByAccountId(model.id, request), Duration.Inf).get.map(_.id)
    Some(model)
  }

  override protected def doSave(model: Account, request: RequestProtocol): Option[String] = {
    JDBCService.db.open()
    doSaveManyToManyRel(model.id, model.roleIds, REL_ROLE_ACCOUNT, request)
    doSaveManyToManyRel(model.id, model.organizationIds, REL_ORGANIZATION_ACCOUNT, request)
    val result = doSaveWithoutTransaction(model, request)
    JDBCService.db.commit()
    result
  }

  override protected def doUpdate(id: String, model: Account, request: RequestProtocol): Option[String] = {
    JDBCService.db.open()
    doUpdateManyToManyRel(id, model.roleIds, REL_ROLE_ACCOUNT, request)
    doUpdateManyToManyRel(id, model.organizationIds, REL_ORGANIZATION_ACCOUNT, request)
    val result = doUpdateWithoutTransaction(id, model, request)
    JDBCService.db.commit()
    result
  }

  override protected def doDeleteByCondition(condition: String, request: RequestProtocol): Option[List[String]] = {
    JDBCService.db.open()
    doDeleteManyToManyRel(REL_ROLE_ACCOUNT, condition, request)
    doDeleteManyToManyRel(REL_ORGANIZATION_ACCOUNT, condition, request)
    val result = doDeleteByConditionWithoutTransaction(condition, request)
    JDBCService.db.commit()
    result
  }
}