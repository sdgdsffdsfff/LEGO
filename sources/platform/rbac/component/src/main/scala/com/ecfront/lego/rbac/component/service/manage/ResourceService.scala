package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.protocol.{RequestProtocol, Response, ResponseDTO}
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.core.foundation.ModelConvertor._
import com.ecfront.lego.rbac.foundation.{Resource, Role}

import scala.concurrent.Future

object ResourceService extends JDBCService[Resource] with ManageService {

  override protected def preSave(model: Resource, request: RequestProtocol): ResponseDTO[Any] = {
    model.id = model.address + IdModel.SPLIT_FLAG + (if (!isSystem(request) || model.appId == null) request.appId else model.appId)
    Response.success(model)
  }

  def findResourceByRoleId(roleId: String, request: RequestProtocol): Future[ResponseDTO[List[Resource]]] = {
    findByCondition(
      s"${IdModel.ID_FLAG} in" +
        s" (SELECT ${Resource._name + "_" + IdModel.ID_FLAG} FROM $REL_ROLE_RESOURCE " +
        s"WHERE ${Role._name + "_" + IdModel.ID_FLAG} = '$roleId')" +
        s" ${appendAuth(request)}",
      request)
  }

  override protected def executeDeleteByCondition(condition: String, request: RequestProtocol): Unit = {
    JDBCService.db.open()
    executeDeleteManyToManyRel(REL_ROLE_RESOURCE, condition, request)
    executeDeleteByConditionWithoutTransaction(condition, request)
    JDBCService.db.commit()
  }

}
