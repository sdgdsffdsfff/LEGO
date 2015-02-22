package com.ecfront.lego.rbac.component

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.rbac.foundation.{LoginInfo, loginInfo}

object AuthService {

  /**
   * 登录
   *
   * @param password        密码
   * @param request
   */
  def login(password: String, request: RequestProtocol):LoginInfo={

  }

  /**
   * 注销
   *
   * @param token
   * @param basic
   * @param callbackHandler return success:null
   */
  def logout(token: String, request: RequestProtocol)

  /**
   * 获取登录信息
   *
   * @param token
   * @param basic
   * @param callbackHandler return success:loginSB
   */
  def getLoginInfo(token: Nothing, basic: Nothing, callbackHandler: Nothing)

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
