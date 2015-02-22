package com.ecfront.lego.core.foundation

import scala.beans.BeanProperty

abstract class IdModel {
  @BeanProperty var id: String = _
}

object IdModel {
  val ID_FLAG = "id"
  val SPLIT_FLAG = "@"
}

abstract class SecureModel extends IdModel {
  @BeanProperty var createUser: String = _
  @BeanProperty var createTime: Long = _
  @BeanProperty var updateUser: String = _
  @BeanProperty var updateTime: Long = _
}

object SecureModel {
  val SYSTEM_USER_FLAG = "system"
}

abstract class AppSecureModel extends SecureModel {
  @BeanProperty var appId: String = _
}

object AppSecureModel {
  val APP_ID_FLAG = "appId"
  val LEGO_APP_FLAG = "LEGO"
}
