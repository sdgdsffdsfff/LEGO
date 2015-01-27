package com.ecfront.lego.core.component

import com.ecfront.common.JsonHelper
import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.VertxStorageService
import com.ecfront.lego.core.foundation.SecureModel
import io.vertx.core.Vertx
import org.scalatest.FunSuite


class CoreServiceSpec extends FunSuite {

  test("JDBC服务测试") {
    VertxStorageService.init(Vertx.vertx(), "config.properties")

    val request = RequestProtocol("0000", "jzy", "test_app")

    val model = TestModel("张三")
    model.id = "id001"
    TestService.save("(id,createUser,createTime,updateUser) values ( ? , ? , ? , ?  )","", request, {
      id =>
        assert(id == "id001")
        //---------------------------------------------------------------
        TestService.getById("id001", request, {
          model =>
            assert(model.name == "张三")
            //---------------------------------------------------------------
            model.name = "haha"
            TestService.update("id001", model, request, {
              id =>
                assert(model.name == "haha")
                //---------------------------------------------------------------
                TestService.getByCondition("select * from ", request, {
                  model =>
                    assert(model.name == "haha")
                    //---------------------------------------------------------------
                    TestService.deleteById("id001", request, {
                      () =>
                        assert(true)
                    })
                })
            })
        })
    })
  }
}

object TestService extends VertxStorageService[TestModel]

case class TestModel(var name: String) extends SecureModel

