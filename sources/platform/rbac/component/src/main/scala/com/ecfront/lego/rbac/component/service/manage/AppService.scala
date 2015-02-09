package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.rbac.foundation.App

object AppService extends JDBCService[App] with ManageService
