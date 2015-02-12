package com.ecfront.lego.rbac.foundation

import com.ecfront.common.Ignore
import com.ecfront.lego.core.foundation.{AppSecureModel, SecureModel}

import scala.beans.BeanProperty

/**
 * APP实体
 */
case class App(@BeanProperty name: String) extends SecureModel

/**
 * 组织实体
 */
case class Organization(@BeanProperty name: String) extends AppSecureModel

/**
 * 角色实体，id=code@appId
 */
case class Role(@BeanProperty code: String,
                @BeanProperty name: String,
                @Ignore @BeanProperty resourceIds: List[String] = null
                 ) extends AppSecureModel

/**
 * 资源实体，id=address@appId
 */
case class Resource(
                     @BeanProperty var name: String,
                     @BeanProperty var address: String
                     ) extends AppSecureModel

/**
 * 账号实体，id=userId@appId
 */
case class Account(
                    @BeanProperty var userId: String,
                    @BeanProperty var userName: String,
                    @BeanProperty var password: String,
                    @BeanProperty var email: String,
                    @BeanProperty var extId: String="",
                    @BeanProperty var extInfo: String="",
                    @Ignore @BeanProperty var organizationIds: List[String] = null,
                    @Ignore @BeanProperty var roleIds: List[String] = null
                    ) extends AppSecureModel

/**
 * 认证类型
 */
object AuthType extends Enumeration {
  type AuthType = Value
  val LOGIN, LOGOUT, AUTHENTICATION = Value
}
