package com.ecfront.lego.core.component

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.DCacheService
import org.scalatest._


class DCacheServiceSpec extends FunSuite {

  Startup.startup

  test("分布式缓存服务测试") {

    val request = RequestProtocol("0000", "jzy", "test_app")

    val model = TestModel()
    model.name = "张三"
    model.bool = true
    model.age = 14
    model.id = "id001"
    TestDCacheService.save(model, request)
    assert(TestDCacheService.getById("id001", request).get.name == "张三")
    model.name = "李四"
    TestDCacheService.update("id001", model, request)
    assert(TestDCacheService.getById("id001", request).get.name == "李四")
    TestDCacheService.deleteById("id001", request)
    assert(TestDCacheService.getById("id001", request).get == null)
  }
}

object TestDCacheService extends DCacheService[TestModel] with SyncBasicService[TestModel]


