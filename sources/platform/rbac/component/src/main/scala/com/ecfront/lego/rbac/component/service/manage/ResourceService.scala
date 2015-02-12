package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.core.foundation.ModelConvertor._
import com.ecfront.lego.rbac.foundation.{Resource, Role}

import scala.concurrent.Future

object ResourceService extends JDBCService[Resource] with ManageService {

  override protected def preSave(model: Resource, request: RequestProtocol): (Boolean, Any) = {
    model.id = model.address + IdModel.SPLIT_FLAG + (if (!isSystem(request) || model.appId == null) request.appId else model.appId)
    (true, model)
  }

  def findResourceByRoleId(roleId: String, request: RequestProtocol): Future[Option[List[Resource]]] = Future {
    executeFindByCondition(
      s"${IdModel.ID_FLAG} in" +
        s" (SELECT ${Resource._name + "_" + IdModel.ID_FLAG} FROM $REL_ROLE_RESOURCE " +
        s"WHERE ${Role._name + "_" + IdModel.ID_FLAG} = '$roleId')" +
        s" ${appendAuth(request)}",
      request)
  }

  override protected def doDeleteByCondition(condition: String, request: RequestProtocol): Option[List[String]] = {
    JDBCService.db.open()
    doDeleteManyToManyRel(REL_ROLE_RESOURCE, condition, request)
    val result = doDeleteByConditionWithoutTransaction(condition, request)
    JDBCService.db.commit()
    result
  }

}
