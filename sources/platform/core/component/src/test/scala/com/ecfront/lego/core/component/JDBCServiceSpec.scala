package com.ecfront.lego.core.component

import com.ecfront.lego.core.component.protocol.Req
import com.ecfront.lego.core.component.storage.JDBCService
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration.Duration


class JDBCServiceSpec extends FunSuite {



  test("JDBC服务测试") {

    JDBCService.init

    val request = Req("0000", "jzy", "test_app")

    //-------------------save--------------------------------------------
    val model = TestModel()
    model.name = "张三"
    model.bool = true
    model.age = 14
    model.id = "id001"
    Await.result(TestJDBCService.save(model, request), Duration.Inf)
    assert(Await.result(TestJDBCService.save(model, request), Duration.Inf).message=="Id exist :id001")
    var resultSingle = Await.result(TestJDBCService.getById("id001", request), Duration.Inf).body
    assert(resultSingle.name == "张三")
    assert(resultSingle.bool)
    assert(resultSingle.createUser == "jzy")
    assert(resultSingle.updateUser == "jzy")
    assert(resultSingle.createTime != null)
    assert(resultSingle.updateTime != null)
    //-------------------update--------------------------------------------
    model.name = "haha"
    model.bool = false
    Await.result(TestJDBCService.update("id001", model, request), Duration.Inf)
    resultSingle = Await.result(TestJDBCService.getById("id001", request), Duration.Inf).body
    assert(resultSingle.name == "haha")
    assert(!resultSingle.bool)
    //-------------------getByCondition--------------------------------------------
    resultSingle = Await.result(TestJDBCService.getByCondition("id='%s' AND name='%s'".format("id001", "haha"), request), Duration.Inf).body
    assert(resultSingle.name == "haha")
    //-------------------findAll--------------------------------------------
    var resultList = Await.result(TestJDBCService.findAll(request), Duration.Inf).body
    assert(resultList.size == 1)
    assert(resultList(0).name == "haha")
    //-------------------pageAll--------------------------------------------
    model.id = null
    Await.result(TestJDBCService.save(model, request), Duration.Inf)
    model.id = null
    Await.result(TestJDBCService.save(model, request), Duration.Inf)
    model.id = null
    Await.result(TestJDBCService.save(model, request), Duration.Inf)
    model.id = null
    model.name = "last"
    Await.result(TestJDBCService.save(model, request), Duration.Inf)
    var resultPage = Await.result(TestJDBCService.pageAll(2, 2, request), Duration.Inf).body
    assert(resultPage.getPageNumber == 2)
    assert(resultPage.getPageSize == 2)
    assert(resultPage.getPageTotal == 3)
    assert(resultPage.getRecordTotal == 5)
    //-------------------pageByCondition--------------------------------------------
    resultPage = Await.result(TestJDBCService.pageByCondition("name = '%s' ORDER BY createTime desc".format("haha"), 1, 3, request), Duration.Inf).body
    assert(resultPage.getPageNumber == 1)
    assert(resultPage.getPageSize == 3)
    assert(resultPage.getPageTotal == 2)
    assert(resultPage.getRecordTotal == 4)
    //-------------------deleteById--------------------------------------------
    Await.result(TestJDBCService.deleteById(resultPage.results.last.id, request), Duration.Inf)
    resultList = Await.result(TestJDBCService.findByCondition("id='%s'".format(resultPage.results.head.id), request), Duration.Inf).body
    assert(resultList.size == 1)
    resultList = Await.result(TestJDBCService.findByCondition("id='%s'".format(resultPage.results.last.id), request), Duration.Inf).body
    assert(resultList.size == 0)
    //-------------------deleteAll--------------------------------------------
    Await.result(TestJDBCService.deleteAll(request), Duration.Inf)
    resultList = Await.result(TestJDBCService.findAll(request), Duration.Inf).body
    assert(resultList.size == 0)
  }
}

object TestJDBCService extends JDBCService[TestModel] with FutureBasicService[TestModel]



