package com.ecfront.lego.core.foundation

case class PKEntity(pk: String)

case class SecureEntity(
                         override val pk: String,
                         createUser: String,
                         createTime: Long,
                         updateUser: String,
                         updateTime: Long
                         ) extends PKEntity(pk)
