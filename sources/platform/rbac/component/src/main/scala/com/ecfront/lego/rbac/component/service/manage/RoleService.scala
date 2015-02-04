package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.storage.VertxStorageService
import com.ecfront.lego.rbac.foundation.Role


object RoleService extends VertxStorageService[Role] with ManageService{

}
