package com.ecfront.lego.rbac.component

import com.ecfront.lego.core.component.{BasicComponentInfo, BasicStartup}

object Startup extends BasicStartup[ComponentInfo] {
  override def registerServices(): Unit = {

  }
}

class ComponentInfo extends BasicComponentInfo {
  override def id: String = "rbac"

  override def version: String = "1.0"

  override def name: String = "用户权限管理"

  override def dependency: Map[String, String] = Map[String, String]()


}
