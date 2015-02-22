package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.SyncBasicService
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.rbac.foundation.Organization


object OrganizationService extends JDBCService[Organization] with SyncBasicService
