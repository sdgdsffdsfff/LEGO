package com.ecfront.lego.rbac.component.manage

import com.ecfront.lego.core.component.SyncBasicService
import com.ecfront.lego.core.component.cache.Cacheable
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.rbac.foundation.Organization


object OrganizationService extends JDBCService[Organization] with SyncBasicService[Organization] with Cacheable
