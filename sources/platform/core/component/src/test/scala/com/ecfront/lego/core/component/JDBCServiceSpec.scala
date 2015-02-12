package com.ecfront.lego.core.component

import com.ecfront.lego.core.component.protocol.RequestProtocol
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
    var resultSingle = Await.result(TestService.getById("id001", request), Duration.Inf).get
    assert(resultSingle.name == "张三")
    assert(resultSingle.bool)
    assert(resultSingle.createUser == "jzy")
    assert(resultSingle.updateUser == "jzy")
    assert(resultSingle.createTime != null)
    assert(resultSingle.updateTime != null)
    //-------------------update--------------------------------------------
    model.name = "haha"
    model.bool = false
    Await.result(TestService.update("id001", model, request), Duration.Inf)
    resultSingle = Await.result(TestService.getById("id001", request), Duration.Inf).get
    assert(resultSingle.name == "haha")
    assert(resultSingle.bool == false)
    //-------------------getByCondition--------------------------------------------
    resultSingle = Await.result(TestService.getByCondition("id='%s' AND name='%s'".format("id001", "haha"), request), Duration.Inf).get
    assert(resultSingle.name == "haha")
    //-------------------findAll--------------------------------------------
    var resultList = Await.result(TestService.findAll(request), Duration.Inf).get
    assert(resultList.size == 1)
    assert(resultList(0).name == "haha")
    //-------------------pageAll--------------------------------------------
    model.id = null
    Await.result(TestService.save(model, request), Duration.Inf)
    model.id = null
    Await.result(TestService.save(model, request), Duration.Inf)
    model.id = null
    Await.result(TestService.save(model, request), Duration.Inf)
    model.id = null
    model.name = "last"
    Await.result(TestService.save(model, request), Duration.Inf)
    var resultPage = Await.result(TestService.pageAll(2, 2, request), Duration.Inf).get
    assert(resultPage.getPageNumber == 2)
    assert(resultPage.getPageSize == 2)
    assert(resultPage.getPageTotal == 3)
    assert(resultPage.getRecordTotal == 5)
    //-------------------pageByCondition--------------------------------------------
    resultPage = Await.result(TestService.pageByCondition("name = '%s' ORDER BY createTime desc".format("haha"), 1, 3, request), Duration.Inf).get
    assert(resultPage.getPageNumber == 1)
    assert(resultPage.getPageSize == 3)
    assert(resultPage.getPageTotal == 2)
    assert(resultPage.getRecordTotal == 4)
    //-------------------deleteById--------------------------------------------
    Await.result(TestService.deleteById(resultPage.results.last.id, request), Duration.Inf)
    resultList = Await.result(TestService.findByCondition("id='%s'".format(resultPage.results.head.id), request), Duration.Inf).get
    assert(resultList.size == 1)
    resultList = Await.result(TestService.findByCondition("id='%s'".format(resultPage.results.last.id), request), Duration.Inf).get
    assert(resultList.size == 0)
    //-------------------deleteAll--------------------------------------------
    Await.result(TestService.deleteAll(request), Duration.Inf)
    resultList = Await.result(TestService.findAll(request), Duration.Inf).get
    assert(resultList.size == 0)
  }
}

object TestService extends JDBCService[TestModel]

case class TestModel() extends SecureModel {
  @BeanProperty var name: String = _
  @BeanProperty var bool: Boolean = _
  @BeanProperty
  @com.ecfront.common.Ignore var age: Int = _
}

