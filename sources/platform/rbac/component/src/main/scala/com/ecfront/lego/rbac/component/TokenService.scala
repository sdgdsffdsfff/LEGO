package com.ecfront.lego.rbac.component

import com.ecfront.lego.core.component.SyncBasicService
import com.ecfront.lego.core.component.protocol.{Req, Resp}
import com.ecfront.lego.core.component.storage.DCacheService
import com.ecfront.lego.core.foundation.StandardCode
import com.ecfront.lego.rbac.foundation.LoginInfo

object TokenService extends DCacheService[LoginInfo] with SyncBasicService[LoginInfo] {

  private val maxIndate = 2592000000L //30天

  override protected def postGetById(result: LoginInfo, preResult: Any, request: Req): Resp[LoginInfo] = {
      if ((result.lastLoginTime + maxIndate) < System.currentTimeMillis()) {
        //过期
        deleteById(result.id, request)
        Resp.fail(StandardCode.NOT_FOUND_CODE,"Login expired.")
      } else {
        Resp.success(result)
      }
  }
}
