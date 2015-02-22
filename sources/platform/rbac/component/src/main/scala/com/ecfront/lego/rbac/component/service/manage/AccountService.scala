package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.SyncBasicService
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.rbac.foundation.Account

object AccountService extends JDBCService[Account] with SyncBasicService[Account] {

  override protected def preSave(model: Account, request: RequestProtocol): (Boolean, Any) = {
    model.id = model.loginId + IdModel.SPLIT_FLAG + (if (!isSystem(request) || model.appId == null) request.appId else model.appId)
    (true, model)
  }

}