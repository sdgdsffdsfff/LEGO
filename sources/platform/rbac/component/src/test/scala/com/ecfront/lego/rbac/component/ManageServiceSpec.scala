package com.ecfront.lego.rbac.component

import java.util.concurrent.CountDownLatch

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.{JDBCService, VertxStorageService}
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.rbac.component.service.manage.ResourceService
import com.ecfront.lego.rbac.foundation.{Resource, Role}
import io.vertx.core.Vertx
import org.scalatest._

import scala.collection.JavaConversions._

class JDBCServiceSpec extends FunSuite {
  var testPath = this.getClass.getResource("/").getPath
  if (System.getProperties.getProperty("os.name").toUpperCase.indexOf("WINDOWS") != -1) {
    testPath = testPath.substring(1)
  }

  test("RBAC管理服务测试") {

    val w = new CountDownLatch(1)

    VertxStorageService.init(Vertx.vertx(), testPath)

    val request = RequestProtocol("0000", "jzy", "test_app")

    JDBCService.db.save(ResourceService.REL_ROLE_RESOURCE, Map(
      classOf[Role].getSimpleName + "_" + IdModel.ID_FLAG -> "role_001",
      classOf[Resource].getSimpleName + "_" + IdModel.ID_FLAG -> "res_001"
    ))
    JDBCService.db.save(ResourceService.REL_ROLE_RESOURCE, Map(
      classOf[Role].getSimpleName + "_" + IdModel.ID_FLAG -> "role_002",
      classOf[Resource].getSimpleName + "_" + IdModel.ID_FLAG -> "res_001"
    ))
    JDBCService.db.save(ResourceService.REL_ROLE_RESOURCE, Map(
      classOf[Role].getSimpleName + "_" + IdModel.ID_FLAG -> "role_001",
      classOf[Resource].getSimpleName + "_" + IdModel.ID_FLAG -> "res_002"
    ))
    assert(JDBCService.db.find("SELECT * FROM " + ResourceService.REL_ROLE_RESOURCE + " WHERE " + classOf[Resource].getSimpleName + "_" + IdModel.ID_FLAG + " = ?", Array("res_001")).size() == 2)

    //-------------------save--------------------------------------------
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
                //-------------------findAll--------------------------------------------
                ResourceService.findAll(request, {
                  result =>
                    assert(result.size == 3)
                    //-------------------deleteById--------------------------------------------
                    ResourceService.deleteById("res_001", request, {
                      Unit =>
                        assert(JDBCService.db.find("SELECT * FROM " + ResourceService.REL_ROLE_RESOURCE + " WHERE " + classOf[Resource].getSimpleName + "_" + IdModel.ID_FLAG + " = ?", Array("res_001")).size() == 0)
                        ResourceService.findAll(request, {
                          result =>
                            assert(result.size == 2)
                            //-------------------deleteByCondition--------------------------------------------
                            ResourceService.deleteByCondition("id='res_002'", request, {
                              Unit =>
                                assert(JDBCService.db.find("SELECT * FROM " + ResourceService.REL_ROLE_RESOURCE + " WHERE " + classOf[Resource].getSimpleName + "_" + IdModel.ID_FLAG + " = ?", Array("res_002")).size() == 0)
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
            })
        })
    })

    w.await()
  }
}


