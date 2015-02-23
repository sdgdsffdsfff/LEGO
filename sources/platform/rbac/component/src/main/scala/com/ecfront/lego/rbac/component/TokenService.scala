package com.ecfront.lego.rbac.component

import com.ecfront.lego.core.component.SyncBasicService
import com.ecfront.lego.core.component.storage.DCacheService
import com.ecfront.lego.rbac.foundation.LoginInfo

object TokenService extends DCacheService[LoginInfo] with SyncBasicService[LoginInfo]
