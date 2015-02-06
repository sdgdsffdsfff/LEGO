package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.rbac.foundation._


trait ManageService {

  val REL_ORGANIZATION_ACCOUNT = JDBCService.createManyToManyRelTable(classOf[Organization], classOf[Account])
  val REL_ROLE_ACCOUNT = JDBCService.createManyToManyRelTable(classOf[Role], classOf[Account])
  val REL_ROLE_RESOURCE = JDBCService.createManyToManyRelTable(classOf[Role], classOf[Resource])

}
