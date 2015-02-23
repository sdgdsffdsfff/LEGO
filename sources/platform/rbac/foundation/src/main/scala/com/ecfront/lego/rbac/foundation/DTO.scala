package com.ecfront.lego.rbac.foundation

import com.ecfront.lego.core.foundation.IdModel

/**
 * Login Info Instances ,id = token
 */
case class LoginInfo(account:Account,lastLoginTime:Long) extends IdModel
