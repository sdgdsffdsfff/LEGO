package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.{JDBCService, VertxStorageService}
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.core.foundation.ModelConvertor._
import com.ecfront.lego.rbac.foundation.{Resource, Role}

object ResourceService extends VertxStorageService[Resource] with ManageService {

  override protected def preSave(model: Resource, request: RequestProtocol, success: => (Any) => Unit, fail: => (String, String) => Unit): Unit = {
    model.id = model.address + IdModel.SPLIT_FLAG + (if (!isSystem(request) || model.appId == null) request.appId else model.appId)
    success(model)
  }

  def findResourceByRoleId(roleId: String, request: RequestProtocol, success: => List[Resource] => Unit, fail: => (String, String) => Unit): Unit = {
    findByCondition(
      s"${IdModel.ID_FLAG} in" +
        s" (SELECT ${Resource._name + "_" + IdModel.ID_FLAG} FROM $REL_ROLE_RESOURCE " +
        s"WHERE ${Role._name + "_" + IdModel.ID_FLAG} = '$roleId')" +
        s" ${appendAuth(request)}",
      request, success, fail)
  }

  override protected def executeDeleteByCondition(condition: String, request: RequestProtocol): Unit = {
    JDBCService.db.open()
    executeDeleteManyToManyRel(REL_ROLE_RESOURCE, condition, request)
    executeDeleteByConditionWithoutTransaction(condition, request)
    JDBCService.db.commit()
  }

}
