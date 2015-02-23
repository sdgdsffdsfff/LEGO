package com.ecfront.lego.rbac.component

import java.util.UUID

import com.ecfront.lego.core.component.protocol.{Req, Resp}
import com.ecfront.lego.core.foundation.StandardCode
import com.ecfront.lego.rbac.component.manage.AccountService
import com.ecfront.lego.rbac.foundation.LoginInfo

object AuthService {

  /**
   * 登录
   *
   * @param password        密码
   * @param request
   */
  def login(loginId: String, password: String, request: Req): Resp[LoginInfo] = {
    if (request.appId != null && loginId != null && password != null) {
      val appId = request.appId.trim
      if (appId.nonEmpty && loginId.trim.nonEmpty && password.trim.nonEmpty) {
        val accountResp = AccountService.getById(AccountService.packageId(loginId.trim, appId, request), request)
        if (accountResp.body != null && accountResp.body.password == AccountService.packageEnryptPwd(loginId.trim, password.trim)) {
          accountResp.body.password = null
          val loginInfo = LoginInfo(accountResp.body, System.currentTimeMillis())
          loginInfo.id = "token_" + UUID.randomUUID().toString
          TokenService.save(loginInfo,request)
          Resp.success(loginInfo)
        } else {
          Resp.fail(StandardCode.BAD_REQUEST_CODE, "LoginId or Password error..")
        }
      } else {
        Resp.fail(StandardCode.BAD_REQUEST_CODE, "Missing required field.")
      }
    } else {
      Resp.fail(StandardCode.BAD_REQUEST_CODE, "Missing required field.")
    }
  }

  /**
   * 注销
   *
   * @param token
   */
  def logout(token: String, request: Req): Resp[Void] = {
    TokenService.deleteById(token, request)
    Resp.success(null)
  }

  /**
   * 获取登录信息
   *
   * @param token
   */
  def getLoginInfo(token: String, request: Req): Resp[LoginInfo] = {
    TokenService.getById(token, request)
  }

  /**
   * 授权
   * <ul>核心流程：
   * <li>action在资源表中是否存在，不存在表示可匿名访问，通过</li>
   * <li>action在资源表存在但没有关联任务角色，表示可匿名访问，通过</li>
   * <li>appId及userId是否存在，如不存在则不通过</li>
   * <li>获取登录用户信息</li>
   * <li>比对登录用户的角色与action可访问的角色是否有交集，有则通过反之不通过</li>
   * </ul>
   *
   * @param action          请求地址
   * @param appId           应用ID
   * @param userId          用户ID
   * @param basic
   * @param callbackHandler  return success:null 表示授权通过，fail 表示不通过
   */
  def authorization(action: Nothing, appId: Nothing, userId: Nothing, basic: Nothing, callbackHandler: Nothing)
}
