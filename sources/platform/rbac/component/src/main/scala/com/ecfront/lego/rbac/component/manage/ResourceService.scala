package com.ecfront.lego.rbac.component.manage

import com.ecfront.lego.core.component.SyncBasicService
import com.ecfront.lego.core.component.protocol.{Resp, Req}
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.rbac.foundation.Resource

object ResourceService extends JDBCService[Resource] with SyncBasicService[Resource] {

  override protected def preSave(model: Resource, request: Req): Resp[Any] = {
    model.id = model.address + IdModel.SPLIT_FLAG + (if (!isSystem(request) || model.appId == null) request.appId else model.appId)
    Resp.success(model)
  }

}
