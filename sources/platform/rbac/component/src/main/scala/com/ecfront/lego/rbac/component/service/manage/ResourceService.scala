package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.SyncBasicService
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.rbac.foundation.Resource

object ResourceService extends JDBCService[Resource] with SyncBasicService[Resource] {

  override protected def preSave(model: Resource, request: RequestProtocol): (Boolean, Any) = {
    model.id = model.address + IdModel.SPLIT_FLAG + (if (!isSystem(request) || model.appId == null) request.appId else model.appId)
    (true, model)
  }

}
