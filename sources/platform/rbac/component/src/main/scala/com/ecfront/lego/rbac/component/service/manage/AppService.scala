package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.VertxStorageService
import com.ecfront.lego.core.foundation.PageModel
import com.ecfront.lego.rbac.foundation.App

object AppService extends VertxStorageService[App] with ManageService{
  override protected def executeDeleteAll(request: RequestProtocol): Unit = super.executeDeleteAll(request)

  override protected def executeDeleteByCondition(condition: String, request: RequestProtocol): Unit = super.executeDeleteByCondition(condition, request)

  override protected def executeDeleteById(id: String, request: RequestProtocol): Unit = super.executeDeleteById(id, request)

  override protected def executeFindAll(request: RequestProtocol): List[App] = super.executeFindAll(request)

  override protected def executeFindByCondition(condition: String, request: RequestProtocol): List[App] = super.executeFindByCondition(condition, request)

  override protected def executeGetByCondition(condition: String, request: RequestProtocol): App = super.executeGetByCondition(condition, request)

  override protected def executeGetById(id: String, request: RequestProtocol): App = super.executeGetById(id, request)

  override protected def executePageAll(pageNumber: Long, pageSize: Long, request: RequestProtocol): PageModel[App] = super.executePageAll(pageNumber, pageSize, request)

  override protected def executePageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol): PageModel[App] = super.executePageByCondition(condition, pageNumber, pageSize, request)

  override protected def executeSave(model: App, request: RequestProtocol): String = super.executeSave(model, request)

  override protected def executeUpdate(id: String, model: App, request: RequestProtocol): String = super.executeUpdate(id, model, request)
}
