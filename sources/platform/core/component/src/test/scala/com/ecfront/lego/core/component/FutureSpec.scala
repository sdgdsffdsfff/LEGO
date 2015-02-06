package com.ecfront.lego.core.component

import java.util.concurrent.CountDownLatch

import com.ecfront.common.BeanHelper
import com.ecfront.lego.core.component.storage.JDBCService
import org.scalatest.FunSuite
import scala.async.Async.{async, await}
import scala.beans.BeanProperty
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Future, _}

class FutureSpec extends FunSuite {

  var testPath = this.getClass.getResource("/").getPath
  if (System.getProperties.getProperty("os.name").toUpperCase.indexOf("WINDOWS") != -1) {
    testPath = testPath.substring(1)
  }

  JDBCService.init(testPath)

  test("Future测试") {
    val start = System.currentTimeMillis()
    Await.result(FutureService.init,Duration.Inf)
    val a = FutureService.save(FutureModel("1", "aa"))
    val b = FutureService.save(FutureModel("2", "bb"))
    val c = FutureService.save(FutureModel("3", "cc"))
    val d = FutureService.save(FutureModel("4", "dd"))
    Await.result(a,Duration.Inf)
    Await.result(b,Duration.Inf)
    Await.result(c,Duration.Inf)
    Await.result(d,Duration.Inf)
    Await.result(FutureService.find(),Duration.Inf)
    printf((System.currentTimeMillis() - start) / 1000 + "")

    /*val w = new CountDownLatch(1)
    async {
      val start = System.currentTimeMillis()
      await(FutureService.init)
      val a = FutureService.save(FutureModel("1", "aa"))
      val b = FutureService.save(FutureModel("2", "bb"))
      val c = FutureService.save(FutureModel("3", "cc"))
      val d = FutureService.save(FutureModel("4", "dd"))
      await(a)
      await(b)
      await(c)
      await(d)
      val res = await(FutureService.find())
     // assert(res.size == 4)
      printf((System.currentTimeMillis() - start) / 1000 + "")
      w.countDown()
    }
    w.await()*/
  }

}

object FutureService {


  def init: Future[String] =  {
    val result = Promise[String]
    Thread.sleep(1000)
    //JDBCService.db.createTableIfNotExist(classOf[FutureModel].getSimpleName, BeanHelper.getFields(classOf[FutureModel]), "id")
    println(Thread.currentThread().getName + "init")
    result.success("")
    result.future
  }

  def save(m: FutureModel): Future[String] =  {
    val result = Promise[String]
    Thread.sleep(10000)
  /*  JDBCService.db.open()
    JDBCService.db.save(classOf[FutureModel].getSimpleName, BeanHelper.getValues(m).asInstanceOf[Map[String, AnyRef]])
    JDBCService.db.commit()*/
    println(Thread.currentThread().getName + "save")
    result.success("")
    result.future
  }

  def find(): Future[List[Map[String, AnyRef]]] = {
    val result = Promise[List[Map[String, AnyRef]]]
    Thread.sleep(1)
    val res = collection.mutable.ArrayBuffer[Map[String, AnyRef]]()
    /*JDBCService.db.find("SELECT * FROM " + classOf[FutureModel].getSimpleName).foreach {
      res += _.toMap
    }*/
    println(Thread.currentThread().getName + "find")
    //result.failure(new Exception("aaaaa"))
    result.success(res.toList)
    result.future
  }

}

case class FutureModel(@BeanProperty id: String, @BeanProperty name: String)