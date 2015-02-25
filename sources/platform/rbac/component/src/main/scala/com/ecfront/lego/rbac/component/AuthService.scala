package com.ecfront.lego.rbac.component

import java.util.UUID

import com.ecfront.lego.core.component.keylog.KeyLogService
import com.ecfront.lego.core.component.protocol.{Req, Resp}
import com.ecfront.lego.core.foundation.StandardCode
import com.ecfront.lego.rbac.component.manage.{AccountService, ResourceService}
import com.ecfront.lego.rbac.foundation.LoginInfo

object AuthService {

  /**
   * 登录
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
          TokenService.save(loginInfo, request)
          KeyLogService.success("Login success by " + loginId, request)
          Resp.success(loginInfo)
        } else {
          KeyLogService.unAuthorized("LoginId or Password error by " + loginId, request)
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
    val loginInfo = TokenService.getById(token, request)
    if (loginInfo) {
      KeyLogService.success("Logout success by " + loginInfo.body.account.loginId, request)
    }
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
   * <li>获取登录用户信息</li>
   * <li>比对登录用户的角色与action可访问的角色是否有交集，有则通过反之不通过</li>
   * </ul>
   */
  def authorization(token: String, request: Req): Resp[Void] = {
    val res = ResourceService.getByRequest(request)
    if (res != null && res.roleIds.nonEmpty) {
      //请求资源（action）需要认证
      val loginInfo = getLoginInfo(token, request)
      if (loginInfo && loginInfo.body != null) {
        if ((res.roleIds.toSet & loginInfo.body.account.roleIds.toSet).nonEmpty) {
          Resp.success(null)
        } else {
          Resp.fail(StandardCode.UNAUTHORIZED_CODE, "The action [%s] allowed role not in request!".format(request.action))
        }
      } else {
        Resp.fail(StandardCode.UNAUTHORIZED_CODE, "The action [%s] must authorization!".format(request.action))
      }
    } else {
      //可匿名访问
      Resp.success(null)
    }

  }
}
