package com.ecfront.lego.rbac.foundation

import com.ecfront.lego.core.foundation.{AppSecureModel, SecureModel}
import com.ecfront.storage.ManyToMany

import scala.beans.BeanProperty

/**
 * APP实体
 */
case class App() extends SecureModel {
  @BeanProperty var name: String = _
}

/**
 * 组织实体
 */
case class Organization() extends AppSecureModel {
  @BeanProperty var name: String = _
}

/**
 * 角色实体，id=code@appId
 */
case class Role() extends AppSecureModel {
  @BeanProperty var code: String = _
  @BeanProperty var name: String = _
  @ManyToMany(master = true, fetch = false, mapping = "Resource") var resourceIds: List[String] = List()
}

/**
 * 资源实体，id=address@appId
 */
case class Resource() extends AppSecureModel {
  @BeanProperty var name: String = _
  @BeanProperty var address: String = _
  @ManyToMany(master = false, fetch = false, mapping = "Role") var roleIds: List[String] = List()
}

/**
 * 账号实体，id=userId@appId
 */
case class Account() extends AppSecureModel {
  @BeanProperty var loginId: String = _
  @BeanProperty var name: String = _
  @BeanProperty var password: String = _
  @BeanProperty var email: String = _
  @BeanProperty var extId: String = _
  @BeanProperty var extInfo: String = _
  @ManyToMany(master = true, fetch = true, mapping = "Organization") var organizationIds: List[String] = List()
  @ManyToMany(master = true, fetch = true, mapping = "Role") var roleIds: List[String] = List()

}

/**
 * 认证类型
 */
object AuthType extends Enumeration {
  type AuthType = Value
  val LOGIN, LOGOUT, AUTHENTICATION = Value
}
