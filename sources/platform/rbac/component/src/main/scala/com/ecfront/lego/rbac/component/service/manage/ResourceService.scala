package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.{JDBCService, VertxStorageService}
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.rbac.foundation.Resource

object ResourceService extends VertxStorageService[Resource] with ManageService {

  override protected def executeDeleteAll(request: RequestProtocol): Unit = {
    JDBCService.db.open()
    JDBCService.db.deleteAll(tableName)
    JDBCService.db.deleteAll(REL_ROLE_RESOURCE)
    JDBCService.db.commit()
  }

  override protected def executeDeleteByCondition(condition: String, request: RequestProtocol): Unit = {
    JDBCService.db.open()
    JDBCService.db.update(s"DELETE FROM ${REL_ROLE_RESOURCE} WHERE ${classOf[Resource].getSimpleName + "_" + IdModel.ID_FLAG} in" +
      s" (SELECT ${IdModel.ID_FLAG} FROM ${classOf[Resource].getSimpleName} WHERE " + condition + ")")
    JDBCService.db.update(s"DELETE FROM " + tableName + " WHERE " + condition)
    JDBCService.db.commit()
  }

  override protected def executeDeleteById(id: String, request: RequestProtocol): Unit = {
    JDBCService.db.open()
    JDBCService.db.update(s"DELETE FROM ${REL_ROLE_RESOURCE} WHERE" +
      s" ${classOf[Resource].getSimpleName + "_" + IdModel.ID_FLAG} =" +
      s" (SELECT ${IdModel.ID_FLAG} FROM ${classOf[Resource].getSimpleName} WHERE ${IdModel.ID_FLAG}=?)"
      , Array(id))
    JDBCService.db.deleteByPk(tableName, id)
    JDBCService.db.commit()
  }

}
