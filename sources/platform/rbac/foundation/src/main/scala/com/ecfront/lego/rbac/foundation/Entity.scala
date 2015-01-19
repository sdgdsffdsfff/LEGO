package com.ecfront.lego.rbac.foundation

import com.ecfront.lego.core.foundation.SecureEntity

case class AppEntity(
                      appPk: String,
                      appName: String,
                      override val createUser: String,
                      override val createTime: Long,
                      override val updateUser: String,
                      override val updateTime: Long
                      ) extends SecureEntity(appPk, createUser, createTime, updateUser, updateTime)
