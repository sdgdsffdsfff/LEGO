package com.ecfront.lego.rbac.component

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.rbac.foundation.{App, Organization, Resource, Role}
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class JDBCServiceSpec extends FunSuite {
  var testPath = this.getClass.getResource("/").getPath
  if (System.getProperties.getProperty("os.name").toUpperCase.indexOf("WINDOWS") != -1) {
    testPath = testPath.substring(1)
  }

  JDBCService.init(testPath)

  val request = RequestProtocol("0000", "jzy", "test_app")

  test("管理服务测试") {
    //-------------------save--------------------------------------------
    val appId = Await.result(AppService.save(App("测试应用"), request), Duration.Inf).get
    val orgId1 = Await.result(OrganizationService.save(Organization("A组"), request), Duration.Inf).get
    val orgId2 = Await.result(OrganizationService.save(Organization("2组"), request), Duration.Inf).get
    val role_admin = Await.result(RoleService.save(Role("admin", "管理员"), request), Duration.Inf).get
    val role_user = Await.result(RoleService.save(Role("user", "用户"), request), Duration.Inf).get
    assert(role_user == "user@" + appId)
    val res_sys = Await.result(ResourceService.save(Resource("系统管理", "sys.manage"), request), Duration.Inf).get
    val res_index = Await.result(ResourceService.save(Resource("首页", "index"), request), Duration.Inf).get
    val res_login = Await.result(ResourceService.save(Resource("登录", "login"), request), Duration.Inf).get
    assert(res_login == "login@" + appId)
    //-------------------get--------------------------------------------

    //-------------------update--------------------------------------------
    Await.result(OrganizationService.update(orgId1,Organization("1组"), request), Duration.Inf).get
  }

}


