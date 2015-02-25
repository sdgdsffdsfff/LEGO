package com.ecfront.lego.rbac.component.manage

import com.ecfront.common.EncryptHelper
import com.ecfront.lego.core.component.SyncBasicService
import com.ecfront.lego.core.component.cache.Cacheable
import com.ecfront.lego.core.component.protocol.{Req, Resp}
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.{IdModel, StandardCode}
import com.ecfront.lego.rbac.foundation.Account

object AccountService extends JDBCService[Account] with SyncBasicService[Account] with Cacheable{

  /**
   * ID检查，是否非法
   * 设置主键、密码
   */
  override protected def preSave(model: Account, request: Req): Resp[Any] = {
    if (model.loginId == null || model.loginId.trim.isEmpty) {
      Resp.fail(StandardCode.BAD_REQUEST_CODE, "Require LoginId.")
    } else {
      if (model.loginId.indexOf(IdModel.SPLIT_FLAG) != -1) {
        Resp.fail(StandardCode.BAD_REQUEST_CODE, "LoginId can't contain @")
      } else {
        model.id = packageId(model.loginId,model.appId,request)
        model.password = packageEnryptPwd(model.loginId, model.password)
        Resp.success(model)
      }
    }
  }

  def packageId(loginId: String, appId: String, request: Req): String = {
    loginId + IdModel.SPLIT_FLAG + (if (!isSystem(request) || appId == null) request.appId else appId)
  }

  def packageEnryptPwd(loginId: String, password: String): String = {
    EncryptHelper.encrypt(loginId + password)
  }

}