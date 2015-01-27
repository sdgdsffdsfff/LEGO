package com.ecfront.lego.core.component

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.VertxStorageService
import com.ecfront.lego.core.foundation.SecureModel
import io.vertx.core.Vertx
import org.scalatest.FunSuite

import scala.beans.BeanProperty


class ServiceSpec extends FunSuite {

  var testPath = this.getClass.getResource("/").getPath
  if (System.getProperties.getProperty("os.name").toUpperCase.indexOf("WINDOWS") != -1) {
    testPath = testPath.substring(1)
  }

  test("JDBC服务测试") {

    VertxStorageService.init(Vertx.vertx(), testPath+"config.properties")

    val request = RequestProtocol("0000", "jzy", "test_app")

    //-------------------save--------------------------------------------
    val model = TestModel("张三", true)
    model.id = "id001"
    TestService.save(model, request, {
      Unit =>
        //-------------------getById--------------------------------------------
        TestService.getById("id001", request, {
          model =>
            assert(model.name == "张三")
            assert(model.bool == true)
            assert(model.createUser =="jzy")
            assert(model.updateUser =="jzy")
            assert(model.createTime !=null)
            assert(model.updateTime !=null)
            //--------------------update-------------------------------------------
            model.name = "haha"
            model.bool = false
            TestService.update("id001", model, request, {
              id =>
                assert(model.name == "haha")
                assert(model.bool == false)
                //--------------------getByCondition-------------------------------------------
                TestService.getByCondition("id='%s' AND name='%s'".format("id001", "haha"), request, {
                  model =>
                    assert(model.name == "haha")
                    //---------------------findAll------------------------------------------
                    TestService.findAll(request, {
                      result =>
                        assert(result.size == 1)
                        assert(result(0).name == "haha")
                        //---------------------pageAll------------------------------------------
                        model.id = null
                        TestService.save(model, request, {
                          Unit =>
                            TestService.save(model, request, {
                              Unit =>
                                TestService.save(model, request, {
                                  Unit =>
                                    model.name = "last"
                                    TestService.save(model, request, {
                                      Unit =>
                                        //---------------------------------------------------------------
                                        TestService.pageAll(2, 2, request, {
                                          pages =>
                                            assert(pages.getPageNumber == 2)
                                            assert(pages.getPageSize == 2)
                                            assert(pages.getPageTotal == 3)
                                            assert(pages.getRecordTotal == 5)
                                            assert(pages.getResults.last.name == "last")
                                            //-----------------------pageByCondition----------------------------------------
                                            TestService.pageByCondition("name = '%s' ORDER BY id desc".format("haha"), 1, 3, request, {
                                              pages =>
                                                assert(pages.getPageNumber == 1)
                                                assert(pages.getPageSize == 3)
                                                assert(pages.getPageTotal == 2)
                                                assert(pages.getRecordTotal == 4)
                                                //------------------------deleteById---------------------------------------
                                                TestService.deleteById(pages.getResults.last.id, request, {
                                                  Unit =>
                                                    TestService.findByCondition("id='%s'".format(pages.getResults.head.id), request, {
                                                      result =>
                                                        assert(result.size == 1)
                                                        TestService.findByCondition("id='%s'".format(pages.getResults.last.id), request, {
                                                          result =>
                                                            assert(result.size == 0)
                                                            //------------------------deleteAll---------------------------------------
                                                            TestService.deleteAll(request, {
                                                              Unit =>
                                                                TestService.findAll(request, {
                                                                  Unit =>
                                                                    assert(result.size == 0)
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
                        })
                    })
                })
            })
        })
    })
  }
}

object TestService extends VertxStorageService[TestModel]

case class TestModel(
                      @BeanProperty var name: String,
                      @BeanProperty var bool: Boolean
                      ) extends SecureModel

