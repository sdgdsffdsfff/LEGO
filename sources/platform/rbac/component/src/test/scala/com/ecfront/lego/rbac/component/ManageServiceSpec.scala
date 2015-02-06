package com.ecfront.lego.rbac.component

import java.util.concurrent.CountDownLatch

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.{JDBCService, VertxStorageService}
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.core.foundation.ModelConvertor._
import com.ecfront.lego.rbac.component.service.manage.{ResourceService, RoleService}
import com.ecfront.lego.rbac.foundation.{Resource, Role}
import io.vertx.core.Vertx
import org.scalatest._

import scala.collection.JavaConversions._

class JDBCServiceSpec extends FunSuite {
  var testPath = this.getClass.getResource("/").getPath
  if (System.getProperties.getProperty("os.name").toUpperCase.indexOf("WINDOWS") != -1) {
    testPath = testPath.substring(1)
  }

  VertxStorageService.init(Vertx.vertx(), testPath)

  val request = RequestProtocol("0000", "jzy", "test_app")

  test("Role管理服务测试") {

    val w1 = new CountDownLatch(1)
    val w2 = new CountDownLatch(1)

    val res = Resource()
    res.id = "res_001"
    res.name = "管理中心列表"
    res.address = "manage.list"
    ResourceService.save(res, request, {
      Unit =>
        res.id = "res_002"
        ResourceService.save(res, request, {
          Unit =>
            res.id = "res_003"
            ResourceService.save(res, request, {
              Unit =>
                w1.countDown()
            })
        })
    })
    w1.await()

    val role = Role()
    role.code = "admin"
    role.name = "管理员"
    RoleService.save(role, request, {
      id =>
        assert(id == "admin@test_app")
        role.name = "管理员2"
        RoleService.update("admin@test_app", role, request, {
          id =>
            RoleService.getById("admin@test_app", request, {
              role =>
                assert(role.name == "管理员2")
                role.code = "user"
                role.name = "普通用户"
                role.resourceIds = List("res_001", "res_002")
                RoleService.save(role, request, {
                  id =>
                    RoleService.getById(id, request, {
                      role =>
                        assert(role.resourceIds.size == 2)
                        role.resourceIds = "res_003" :: role.resourceIds
                        RoleService.update(role.id, role, request, {
                          id =>
                            RoleService.getById(id, request, {
                              role =>
                                assert(role.resourceIds.size == 3)
                                w2.countDown()
                            })
                        })
                    })
                })
            })
        })
    })
    w2.await()
  }

  test("Resource管理服务测试") {

    val w = new CountDownLatch(1)

    JDBCService.db.save(ResourceService.REL_ROLE_RESOURCE, Map(
      Role._name + "_" + IdModel.ID_FLAG -> "role_001",
      Resource._name + "_" + IdModel.ID_FLAG -> ("manage.list@" + request.appId)
    ))
    JDBCService.db.save(ResourceService.REL_ROLE_RESOURCE, Map(
      Role._name + "_" + IdModel.ID_FLAG -> "role_002",
      Resource._name + "_" + IdModel.ID_FLAG -> ("manage.list@" + request.appId)
    ))
    JDBCService.db.save(ResourceService.REL_ROLE_RESOURCE, Map(
      Role._name + "_" + IdModel.ID_FLAG -> "role_001",
      Resource._name + "_" + IdModel.ID_FLAG -> ("manage.create@" + request.appId)
    ))
    assert(JDBCService.db.find("SELECT * FROM " + ResourceService.REL_ROLE_RESOURCE + " WHERE " + Resource._name + "_" + IdModel.ID_FLAG + " = ?", Array("manage.list@" + request.appId)).size() == 2)

    //-------------------save--------------------------------------------
    val res = Resource()
    res.name = "管理中心列表"
    res.address = "manage.list"
    ResourceService.save(res, request, {
      Unit =>
        res.address = "manage.create"
        ResourceService.save(res, request, {
          Unit =>
            res.address = "manage.delete"
            ResourceService.save(res, request, {
              Unit =>
                //-------------------findResourceByRoleId--------------------------------------------
                ResourceService.findResourceByRoleId("role_001", request, {
                  result =>
                    assert(result.size == 2)
                    //-------------------findAll--------------------------------------------
                    ResourceService.findAll(request, {
                      result =>
                        assert(result.size == 3)
                        //-------------------deleteById--------------------------------------------
                        ResourceService.deleteById("manage.list@" + request.appId, request, {
                          Unit =>
                            assert(JDBCService.db.find("SELECT * FROM " + ResourceService.REL_ROLE_RESOURCE + " WHERE " + Resource._name + "_" + IdModel.ID_FLAG + " = ?", Array("manage.list@" + request.appId)).size() == 0)
                            ResourceService.findAll(request, {
                              result =>
                                assert(result.size == 2)
                                //-------------------deleteByCondition--------------------------------------------
                                ResourceService.deleteByCondition("id='manage.create@" + request.appId + "'", request, {
                                  Unit =>
                                    assert(JDBCService.db.find("SELECT * FROM " + ResourceService.REL_ROLE_RESOURCE + " WHERE " + Resource._name + "_" + IdModel.ID_FLAG + " = ?", Array("manage.create@" + request.appId)).size() == 0)
                                    ResourceService.findAll(request, {
                                      result =>
                                        assert(result.size == 1)
                                        //-------------------deleteAll--------------------------------------------
                                        ResourceService.deleteAll(request, {
                                          Unit =>
                                            ResourceService.findAll(request, {
                                              result =>
                                                assert(result.size == 0)
                                                w.countDown()
                                            })
                                        })
                                    })
                                })
                            })
                        })
                    })
                }, {
                  (code, message) =>
                })
            })
        })
    })

    w.await()
  }

}


