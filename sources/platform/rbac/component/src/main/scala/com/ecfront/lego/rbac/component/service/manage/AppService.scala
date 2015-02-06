package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.storage.VertxStorageService
import com.ecfront.lego.rbac.foundation.App

object AppService extends VertxStorageService[App] with ManageService
