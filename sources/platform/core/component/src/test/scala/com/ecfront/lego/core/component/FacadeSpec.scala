package com.ecfront.lego.core.component

import com.ecfront.common.JsonHelper
import com.ecfront.lego.core.component.communication.Communication
import com.ecfront.lego.core.component.protocol.{RequestProtocol, ResponseProtocol}
import com.ecfront.lego.core.foundation.{PageModel, SecureModel, StandardCode}
import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer


class CoreServiceSpec extends FunSuite {

  /*test("基础服务测试") {
    CoreService.init(TestCommunication)
    val request = RequestProtocol("11", "jzy", "test")

    TestService.getById("id001", request, {
      model =>
        assert(model.name == "张三")
    }, {
      (code, message) =>
        assert(false)
    })

    val model=TestModel("haha")
    model.id="id001"
    TestService.save(TestModel("haha"), request, {
      id =>
        assert(id == "id001")
    }, {
      (code, message) =>
        assert(false)
    }
    )
    TestService.update("id001", TestModel("haha"), request, {
      id =>
        assert(id == "id001")
    }, {
      (code, message) =>
        assert(false)
    }
    )

    TestService.getByCondition(JsonHelper.createObjectNode().put("a", "sss"), request, {
      model =>
        assert(model.name == "张三")
    }, {
      (code, message) =>
        assert(false)
    })

    TestService.findByCondition(JsonHelper.createObjectNode().put("a", "sss"), request, {
      page =>
        assert(page.pageTotal == 1)
        assert(page.results(1).name == "李四")
    }, {
      (code, message) =>
        assert(false)
    })

    TestService.deleteById("id001", request, {
      () =>
        assert(true)
    }, {
      (code, message) =>
        assert(false)
    })
  }
}

object TestService extends CoreService[TestModel] {
  override val address: String = "test.address"

  override protected def preGetById(id: String, request: RequestProtocol, success: => (Any) => Unit, fail: => (String, String) => Unit): Unit = {
    success("Add")
  }

  override protected def postGetById(result: TestModel, preResult: Any, request: RequestProtocol, success: => (TestModel) => Unit, fail: => (String, String) => Unit): Unit = {
    assert(preResult.asInstanceOf[String] == "Add")
    assert(result.name == "张三")
    success(result)
  }
}

case class TestModel(name: String) extends SecureModel

object TestCommunication extends Communication {

  def send(address: String, request: RequestProtocol, complete: => ResponseProtocol => Unit, fail: => (String, String) => Unit): Unit = {
    request.action match {
      case "SAVE" =>
        complete(ResponseProtocol(StandardCode.SUCCESS_CODE, "", "id001"))
      case "UPDATE" =>
        complete(ResponseProtocol(StandardCode.SUCCESS_CODE, "", "id001"))
      case "GET" =>
        complete(ResponseProtocol(StandardCode.SUCCESS_CODE, "", JsonHelper.toJsonString(TestModel("张三"))))
      case "FIND" =>
        complete(ResponseProtocol(StandardCode.SUCCESS_CODE, "", JsonHelper.toJsonString(PageModel(1, 10, 1, 2, ArrayBuffer[TestModel](
          TestModel("张三"),
          TestModel("李四")
        )))))
      case "DELETE" =>
        complete(ResponseProtocol(StandardCode.SUCCESS_CODE, "", ""))
    }
  }
}*/
