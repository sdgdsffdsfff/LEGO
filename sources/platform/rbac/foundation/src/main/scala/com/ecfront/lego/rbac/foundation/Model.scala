package com.ecfront.lego.rbac.foundation

import com.ecfront.common.Ignore
import com.ecfront.lego.core.foundation.{AppSecureModel, SecureModel}

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
  @Ignore
  @BeanProperty var resourceIds: List[String] = _
}

/**
 * 资源实体，id=address@appId
 */
case class Resource() extends AppSecureModel {
  @BeanProperty var name: String = _
  @BeanProperty var address: String = _
}

/**
 * 账号实体，id=userId@appId
 */
case class Account() extends AppSecureModel {
  @BeanProperty var userId: String = _
  @BeanProperty var userName: String = _
  @BeanProperty var password: String = _
  @BeanProperty var email: String = _
  @Ignore
  @BeanProperty var organizationIds: List[String] = _
  @Ignore
  @BeanProperty var roleIds: List[String] = _
  @BeanProperty var extId: String = _
  @BeanProperty var extInfo: String = _
}

/**
 * 认证类型
 */
object AuthType extends Enumeration {
  type AuthType = Value
  val LOGIN, LOGOUT, AUTHENTICATION = Value
}
