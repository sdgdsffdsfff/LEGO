package com.ecfront.lego.core.component

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.protocol.Response.isSuccess
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.SecureModel
import org.scalatest._

import scala.beans.BeanProperty
import scala.concurrent.Await
import scala.concurrent.duration.Duration


class JDBCServiceSpec extends FunSuite {

  var testPath = this.getClass.getResource("/").getPath
  if (System.getProperties.getProperty("os.name").toUpperCase.indexOf("WINDOWS") != -1) {
    testPath = testPath.substring(1)
  }

  test("JDBC服务测试") {

    JDBCService.init(testPath)

    val request = RequestProtocol("0000", "jzy", "test_app")

    //-------------------save--------------------------------------------
    val model = TestModel()
    model.name = "张三"
    model.bool = true
    model.age = 14
    model.id = "id001"
    Await.result(TestService.save(model, request), Duration.Inf)
    var resultSingle = Await.result(TestService.getById("id001", request), Duration.Inf)
    if (resultSingle) {
      assert(resultSingle.body.name == "张三")
      assert(resultSingle.body.bool)
      assert(resultSingle.body.createUser == "jzy")
      assert(resultSingle.body.updateUser == "jzy")
      assert(resultSingle.body.createTime != null)
      assert(resultSingle.body.updateTime != null)
    } else {
      assert(1 != 1)
    }
    model.name = "haha"
    model.bool = false
    Await.result(TestService.update("id001", model, request), Duration.Inf)
    resultSingle = Await.result(TestService.getById("id001", request), Duration.Inf)
    if (resultSingle) {
      assert(resultSingle.body.name == "haha")
      assert(resultSingle.body.bool == false)
    } else {
      assert(1 != 1)
    }
    resultSingle = Await.result(TestService.getByCondition("id='%s' AND name='%s'".format("id001", "haha"), request), Duration.Inf)
    if (resultSingle) {
      assert(resultSingle.body.name == "haha")
    } else {
      assert(1 != 1)
    }
    var resultList = Await.result(TestService.findAll(request), Duration.Inf)
    if (resultList) {
      assert(resultList.body.size == 1)
      assert(resultList.body(0).name == "haha")
    } else {
      assert(1 != 1)
    }
    model.id = null
    Await.result(TestService.save(model, request), Duration.Inf)
    model.id = null
    Await.result(TestService.save(model, request), Duration.Inf)
    model.id = null
    Await.result(TestService.save(model, request), Duration.Inf)
    model.id = null
    model.name = "last"
    Await.result(TestService.save(model, request), Duration.Inf)
    var resultPage = Await.result(TestService.pageAll(2, 2, request), Duration.Inf)
    if (resultPage) {
      assert(resultPage.body.getPageNumber == 2)
      assert(resultPage.body.getPageSize == 2)
      assert(resultPage.body.getPageTotal == 3)
      assert(resultPage.body.getRecordTotal == 5)
    } else {
      assert(1 != 1)
    }
    resultPage = Await.result(TestService.pageByCondition("name = '%s' ORDER BY createTime desc".format("haha"), 1, 3, request), Duration.Inf)
    if (resultPage) {
      assert(resultPage.body.getPageNumber == 1)
      assert(resultPage.body.getPageSize == 3)
      assert(resultPage.body.getPageTotal == 2)
      assert(resultPage.body.getRecordTotal == 4)
    } else {
      assert(1 != 1)
    }
    Await.result(TestService.deleteById(resultPage.body.results.last.id, request), Duration.Inf)
    resultList = Await.result(TestService.findByCondition("id='%s'".format(resultPage.body.results.head.id), request), Duration.Inf)
    if (resultList) {
      assert(resultList.body.size == 1)
    } else {
      assert(1 != 1)
    }
    resultList = Await.result(TestService.findByCondition("id='%s'".format(resultPage.body.results.last.id), request), Duration.Inf)
    if (resultList) {
      assert(resultList.body.size == 0)
    } else {
      assert(1 != 1)
    }
    Await.result(TestService.deleteAll(request), Duration.Inf)
    resultList = Await.result(TestService.findAll(request), Duration.Inf)
    if (resultList) {
      assert(resultList.body.size == 0)
    } else {
      assert(1 != 1)
    }
  }
}

object TestService extends JDBCService[TestModel]

case class TestModel() extends SecureModel {
  @BeanProperty var name: String = _
  @BeanProperty var bool: Boolean = _
  @BeanProperty
  @com.ecfront.common.Ignore var age: Int = _
}

