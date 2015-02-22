package com.ecfront.lego.rbac.component.manage

import com.ecfront.lego.core.component.SyncBasicService
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.rbac.foundation.Role

object RoleService extends JDBCService[Role] with SyncBasicService[Role] {

  override protected def preSave(model: Role, request: RequestProtocol): (Boolean, Any) = {
    model.id = model.code + IdModel.SPLIT_FLAG + (if (!isSystem(request) || model.appId == null) request.appId else model.appId)
    (true, model)
  }

}
