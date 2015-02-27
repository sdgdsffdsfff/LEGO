package com.ecfront.lego.client.jvm.scala

import com.ecfront.lego.client.jvm.{Exchange, InitConfig}
import com.ecfront.lego.core.foundation.protocol.Resp
import com.ecfront.lego.rbac.foundation._
import com.ecfront.storage.PageModel
import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * 所有的调用的统一入口
 */
object LEGO extends LazyLogging {

  /**
   * 设置自定义配置文件路径，默认在classpath下
   *
   * @param path 路径
   */
  def setConfig(path: String) {
    InitConfig.CUSTOM_CONFIG_PATH = path
  }

  /**
   * 获取当前的APPID
   *
   * @return appid
   */
  def getAppId: String = {
    Exchange.config.get("appId").asText()
  }

  /**
   * 获取LEGO Host
   *
   * @return host
   */
  def getHost: String = {
    Exchange.config.get("host").asText()
  }

  /**
   * 获取LEGO Port
   *
   * @return port
   */
  def getPort: Int = {
    Exchange.config.get("port").asInt()
  }

  /**
   * RBAC服务
   */
  object RBAC {

    /**
     * 登录
     *
     * @param loginId   用户ID
     * @param password 密码
     */
    def login(loginId: String, password: String): Resp[LoginInfo] = {
      Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.LOGIN, Map("loginId" -> loginId, "password" -> password), null, null, classOf[LoginInfo])
    }

    /**
     * 获取登录信息
     *
     * @param token token
     */
    def getLoginInfo(token: String): Resp[LoginInfo] = {
      Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.GET_LOGIN_INFO, null, null, token, classOf[LoginInfo])
    }

    /**
     * 注销
     *
     * @param token token
     */
    def logout(token: String): Resp[Void] = {
      Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.LOGOUT, null, null, token, classOf[Void])
    }

    /**
     * 授权
     *
     * @param action 请求（资源）地址
     * @param token token
     */
    def auth(action: String, token: String): Resp[Void] = {
      Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.AUTH, Map("action" -> action), null, token, classOf[Void])
    }

    /**
     * 更新自己的信息，可修改除userId,appId,roles,organization外的其它信息
     *
     * @param id       用户Id
     * @param name 用户显示名称
     * @param password 密码
     * @param email    电邮
     * @param token token
     */
    def updateSelfAccount(id: String, name: String, password: String, email: String, token: String): Resp[String] = {
      val account = Account()
      account.id = id
      account.name = name
      account.password = password
      account.email = email
      Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.ACCOUNT_SELF_UPDATE, null, account, token, classOf[String])
    }

    /**
     * 用户、角色、资源管理
     */
    object Manage {

      /**
       * 添加应用
       *
       * @param name 应用显示名称
       * @param token token
       */
      def saveApp(name: String, token: String): Resp[String] = {
        val app = App()
        app.name = name
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_APP_SAVE, null, app, token, classOf[String])
      }

      /**
       * 更新应用
       *
       * @param id           应用Id
       * @param name     应用显示名称
       * @param token token
       */
      def updateApp(id: String, name: String, token: String): Resp[String] = {
        val app = App()
        app.name = name
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_APP_UPDATE, Map("id" -> id), app, token, classOf[String])
      }


      /**
       * 删除应用
       *
       * @param id    应用Id
       * @param token token
       */
      def deleteApp(id: String, token: String): Resp[String] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_APP_DELETE, Map("id" -> id), null, token, classOf[String])
      }

      /**
       * 删除所有应用
       *
       * @param token token
       */
      def deleteAllApp(token: String): Resp[List[String]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_APP_DELETE, Map(), null, token, classOf[List[String]])
      }

      /**
       * 获取应用
       *
       * @param id    应用Id
       * @param token token
       */
      def getApp(id: String, token: String): Resp[App] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_APP_GET, Map("id" -> id), null, token, classOf[App])
      }

      /**
       * 查找应用
       *
       * @param token token
       */
      def findApp(token: String): Resp[List[App]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_APP_FIND, Map(), null, token, classOf[List[App]])
      }

      /**
       * 查找应用
       *
       * @param condition  查找条件
       * @param token token
       */
      def findApp(condition: String, token: String): Resp[List[App]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_APP_FIND, Map("condition" -> condition), null, token, classOf[List[App]])
      }

      /**
       * 查找应用
       *
       * @param pageNumber 当前页码，从1开始
       * @param pageSize   每页显示条数
       * @param token token
       */
      def findApp(pageNumber: Long, pageSize: Long, token: String): Resp[PageModel[App]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_APP_PAGE, Map(
          "pageNumber" -> pageNumber,
          "pageSize" -> pageSize
        ), null, token, classOf[PageModel[App]])
      }

      /**
       * 查找应用
       *
       * @param condition  查找条件
       * @param pageNumber 当前页码，从1开始
       * @param pageSize   每页显示条数
       * @param token token
       */
      def findApp(condition: String, pageNumber: Long, pageSize: Long, token: String): Resp[PageModel[App]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_APP_PAGE, Map(
          "condition" -> condition,
          "pageNumber" -> pageNumber,
          "pageSize" -> pageSize
        ), null, token, classOf[PageModel[App]])
      }

      /**
       * 添加组织
       *
       * @param name 组织显示名称
       * @param token token
       */
      def saveOrganization(name: String, token: String): Resp[String] = {
        val organization = Organization()
        organization.name = name
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ORGANIZATION_SAVE, null, organization, token, classOf[String])
      }

      /**
       * 更新组织
       *
       * @param id           组织Id
       * @param name     组织显示名称
       * @param token token
       */
      def updateOrganization(id: String, name: String, token: String): Resp[String] = {
        val organization = Organization()
        organization.name = name
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ORGANIZATION_UPDATE, Map("id" -> id), organization, token, classOf[String])
      }


      /**
       * 删除组织
       *
       * @param id    组织Id
       * @param token token
       */
      def deleteOrganization(id: String, token: String): Resp[String] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ORGANIZATION_DELETE, Map("id" -> id), null, token, classOf[String])
      }

      /**
       * 删除所有组织
       *
       * @param token token
       */
      def deleteAllOrganization(token: String): Resp[List[String]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ORGANIZATION_DELETE, Map(), null, token, classOf[List[String]])
      }

      /**
       * 获取组织
       *
       * @param id    组织Id
       * @param token token
       */
      def getOrganization(id: String, token: String): Resp[Organization] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ORGANIZATION_GET, Map("id" -> id), null, token, classOf[Organization])
      }

      /**
       * 查找组织
       *
       * @param token token
       */
      def findOrganization(token: String): Resp[List[Organization]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ORGANIZATION_FIND, Map(), null, token, classOf[List[Organization]])
      }

      /**
       * 查找组织
       *
       * @param condition  查找条件
       * @param token token
       */
      def findOrganization(condition: String, token: String): Resp[List[Organization]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ORGANIZATION_FIND, Map("condition" -> condition), null, token, classOf[List[Organization]])
      }

      /**
       * 查找组织
       *
       * @param pageNumber 当前页码，从1开始
       * @param pageSize   每页显示条数
       * @param token token
       */
      def findOrganization(pageNumber: Long, pageSize: Long, token: String): Resp[PageModel[Organization]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ORGANIZATION_PAGE, Map(
          "pageNumber" -> pageNumber,
          "pageSize" -> pageSize
        ), null, token, classOf[PageModel[Organization]])
      }

      /**
       * 查找组织
       *
       * @param condition  查找条件
       * @param pageNumber 当前页码，从1开始
       * @param pageSize   每页显示条数
       * @param token token
       */
      def findOrganization(condition: String, pageNumber: Long, pageSize: Long, token: String): Resp[PageModel[Organization]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ORGANIZATION_PAGE, Map(
          "condition" -> condition,
          "pageNumber" -> pageNumber,
          "pageSize" -> pageSize
        ), null, token, classOf[PageModel[Organization]])
      }

      /**
       * 添加用户
       *
       * @param loginId   用户Id
       * @param name 用户显示名称
       * @param password 密码
       * @param email    电邮
       * @param roleIds    对应的角色列表
       * @param organizationIds    对应的组织列表
       * @param token token
       */
      def saveAccount(loginId: String, name: String, password: String, email: String, roleIds: List[String], organizationIds: List[String], token: String): Resp[String] = {
        val account = Account()
        account.loginId = loginId
        account.name = name
        account.password = password
        account.email = email
        account.roleIds = roleIds
        account.organizationIds = organizationIds
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ACCOUNT_SAVE, null, account, token, classOf[String])
      }

      /**
       * 更新用户
       *
       * @param id           用户Id
       * @param name     用户显示名称
       * @param password     密码
       * @param email        电邮
       * @param roleIds        对应的角色列表
       * @param organizationIds 对应的组织列表
       * @param token token
       */
      def updateAccount(id: String, name: String, password: String, email: String, roleIds: List[String], organizationIds: List[String], token: String): Resp[String] = {
        val account = Account()
        account.name = name
        account.password = password
        account.email = email
        account.roleIds = roleIds
        account.organizationIds = organizationIds
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ACCOUNT_UPDATE, Map("id" -> id), account, token, classOf[String])
      }


      /**
       * 删除用户
       *
       * @param id    用户Id
       * @param token token
       */
      def deleteAccount(id: String, token: String): Resp[String] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ACCOUNT_DELETE, Map("id" -> id), null, token, classOf[String])
      }

      /**
       * 删除所有用户
       *
       * @param token token
       */
      def deleteAllAccount(token: String): Resp[List[String]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ACCOUNT_DELETE, Map(), null, token, classOf[List[String]])
      }

      /**
       * 获取用户
       *
       * @param id    用户Id
       * @param token token
       */
      def getAccount(id: String, token: String): Resp[Account] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ACCOUNT_GET, Map("id" -> id), null, token, classOf[Account])
      }

      /**
       * 查找用户
       *
       * @param token token
       */
      def findAccount(token: String): Resp[List[Account]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ACCOUNT_FIND, Map(), null, token, classOf[List[Account]])
      }

      /**
       * 查找用户
       *
       * @param condition  查找条件
       * @param token token
       */
      def findAccount(condition: String, token: String): Resp[List[Account]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ACCOUNT_FIND, Map("condition" -> condition), null, token, classOf[List[Account]])
      }

      /**
       * 查找用户
       *
       * @param pageNumber 当前页码，从1开始
       * @param pageSize   每页显示条数
       * @param token token
       */
      def findAccount(pageNumber: Long, pageSize: Long, token: String): Resp[PageModel[Account]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ACCOUNT_PAGE, Map(
          "pageNumber" -> pageNumber,
          "pageSize" -> pageSize
        ), null, token, classOf[PageModel[Account]])
      }

      /**
       * 查找用户
       *
       * @param condition  查找条件
       * @param pageNumber 当前页码，从1开始
       * @param pageSize   每页显示条数
       * @param token token
       */
      def findAccount(condition: String, pageNumber: Long, pageSize: Long, token: String): Resp[PageModel[Account]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ACCOUNT_PAGE, Map(
          "condition" -> condition,
          "pageNumber" -> pageNumber,
          "pageSize" -> pageSize
        ), null, token, classOf[PageModel[Account]])
      }

      /**
       * 添加角色
       *
       * @param code 角色编码
       * @param name 角色显示名称
       * @param resourceIds    对应的资源列表
       * @param token token
       */
      def saveRole(code: String, name: String, resourceIds: List[String], token: String): Resp[String] = {
        val role = Role()
        role.code = code
        role.name = name
        role.resourceIds = resourceIds
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ROLE_SAVE, null, role, token, classOf[String])
      }

      /**
       * 更新角色
       *
       * @param id           角色Id
       * @param name     角色显示名称
       * @param resourceIds        对应的角色列表
       * @param token token
       */
      def updateRole(id: String, name: String, resourceIds: List[String], token: String): Resp[String] = {
        val role = Role()
        role.name = name
        role.resourceIds = resourceIds
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ROLE_UPDATE, Map("id" -> id), role, token, classOf[String])
      }


      /**
       * 删除角色
       *
       * @param id    角色Id
       * @param token token
       */
      def deleteRole(id: String, token: String): Resp[String] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ROLE_DELETE, Map("id" -> id), null, token, classOf[String])
      }

      /**
       * 删除所有角色
       *
       * @param token token
       */
      def deleteAllRole(token: String): Resp[List[String]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ROLE_DELETE, Map(), null, token, classOf[List[String]])
      }

      /**
       * 获取角色
       *
       * @param id    角色Id
       * @param token token
       */
      def getRole(id: String, token: String): Resp[Role] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ROLE_GET, Map("id" -> id), null, token, classOf[Role])
      }

      /**
       * 查找角色
       *
       * @param token token
       */
      def findRole(token: String): Resp[List[Role]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ROLE_FIND, Map(), null, token, classOf[List[Role]])
      }

      /**
       * 查找角色
       *
       * @param condition  查找条件
       * @param token token
       */
      def findRole(condition: String, token: String): Resp[List[Role]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ROLE_FIND, Map("condition" -> condition), null, token, classOf[List[Role]])
      }

      /**
       * 查找角色
       *
       * @param pageNumber 当前页码，从1开始
       * @param pageSize   每页显示条数
       * @param token token
       */
      def findRole(pageNumber: Long, pageSize: Long, token: String): Resp[PageModel[Role]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ROLE_PAGE, Map(
          "pageNumber" -> pageNumber,
          "pageSize" -> pageSize
        ), null, token, classOf[PageModel[Role]])
      }

      /**
       * 查找角色
       *
       * @param condition  查找条件
       * @param pageNumber 当前页码，从1开始
       * @param pageSize   每页显示条数
       * @param token token
       */
      def findRole(condition: String, pageNumber: Long, pageSize: Long, token: String): Resp[PageModel[Role]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_ROLE_PAGE, Map(
          "condition" -> condition,
          "pageNumber" -> pageNumber,
          "pageSize" -> pageSize
        ), null, token, classOf[PageModel[Role]])
      }


      /**
       * 添加资源
       *
       * @param name 资源显示名称
       * @param address    地址
       * @param token token
       */
      def saveResource(name: String, address: String, token: String): Resp[String] = {
        val resource = Resource()
        resource.name = name
        resource.address = address
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_RESOURCE_SAVE, null, resource, token, classOf[String])
      }

      /**
       * 更新资源
       *
       * @param id           资源Id
       * @param name     资源显示名称
       * @param token token
       */
      def updateResource(id: String, name: String, token: String): Resp[String] = {
        val resource = Resource()
        resource.name = name
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_RESOURCE_UPDATE, Map("id" -> id), resource, token, classOf[String])
      }


      /**
       * 删除资源
       *
       * @param id    资源Id
       * @param token token
       */
      def deleteResource(id: String, token: String): Resp[String] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_RESOURCE_DELETE, Map("id" -> id), null, token, classOf[String])
      }

      /**
       * 删除所有资源
       *
       * @param token token
       */
      def deleteAllResource(token: String): Resp[List[String]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_RESOURCE_DELETE, Map(), null, token, classOf[List[String]])
      }

      /**
       * 获取资源
       *
       * @param id    资源Id
       * @param token token
       */
      def getResource(id: String, token: String): Resp[Resource] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_RESOURCE_GET, Map("id" -> id), null, token, classOf[Resource])
      }

      /**
       * 查找资源
       *
       * @param token token
       */
      def findResource(token: String): Resp[List[Resource]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_RESOURCE_FIND, Map(), null, token, classOf[List[Resource]])
      }

      /**
       * 查找资源
       *
       * @param condition  查找条件
       * @param token token
       */
      def findResource(condition: String, token: String): Resp[List[Resource]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_RESOURCE_FIND, Map("condition" -> condition), null, token, classOf[List[Resource]])
      }

      /**
       * 查找资源
       *
       * @param pageNumber 当前页码，从1开始
       * @param pageSize   每页显示条数
       * @param token token
       */
      def findResource(pageNumber: Long, pageSize: Long, token: String): Resp[PageModel[Resource]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_RESOURCE_PAGE, Map(
          "pageNumber" -> pageNumber,
          "pageSize" -> pageSize
        ), null, token, classOf[PageModel[Resource]])
      }

      /**
       * 查找资源
       *
       * @param condition  查找条件
       * @param pageNumber 当前页码，从1开始
       * @param pageSize   每页显示条数
       * @param token token
       */
      def findResource(condition: String, pageNumber: Long, pageSize: Long, token: String): Resp[PageModel[Resource]] = {
        Exchange.execute(com.ecfront.lego.rbac.foundation.ServiceInfo.MANAGE_RESOURCE_PAGE, Map(
          "condition" -> condition,
          "pageNumber" -> pageNumber,
          "pageSize" -> pageSize
        ), null, token, classOf[PageModel[Resource]])
      }
    }

  }

}

