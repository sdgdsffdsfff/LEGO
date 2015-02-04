package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.storage.VertxStorageService
import com.ecfront.lego.rbac.foundation.Account

object AccountService extends VertxStorageService[Account] with ManageService {

}
