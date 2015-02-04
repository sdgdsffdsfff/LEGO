package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.rbac.foundation.{Account, Organization, Resource, Role}

import scala.collection.JavaConversions._

trait ManageService {

  val REL_ORGANIZATION_ACCOUNT = "rel_" + classOf[Organization].getSimpleName + "_" + classOf[Account].getSimpleName
  val REL_ROLE_ACCOUNT = "rel_" + classOf[Role].getSimpleName + "_" + classOf[Account].getSimpleName
  val REL_ROLE_RESOURCE = "rel_" + classOf[Role].getSimpleName + "_" + classOf[Resource].getSimpleName

  def init(): Unit = {
    JDBCService.db.createTableIfNotExist(
      REL_ORGANIZATION_ACCOUNT,
      Map[String, String](
        classOf[Organization].getSimpleName + "_" + IdModel.ID_FLAG -> "String",
        classOf[Account].getSimpleName + "_" + IdModel.ID_FLAG -> "String"
      ),
      null)
    JDBCService.db.createTableIfNotExist(
      REL_ROLE_ACCOUNT,
      Map[String, String](
        classOf[Role].getSimpleName + "_" + IdModel.ID_FLAG -> "String",
        classOf[Account].getSimpleName + "_" + IdModel.ID_FLAG -> "String"
      ),
      null)
    JDBCService.db.createTableIfNotExist(
      REL_ROLE_RESOURCE,
      Map[String, String](
        classOf[Role].getSimpleName + "_" + IdModel.ID_FLAG -> "String",
        classOf[Resource].getSimpleName + "_" + IdModel.ID_FLAG -> "String"
      ),
      null)
  }

  init()
}
