import java.util.concurrent.CountDownLatch

import org.scalatest.FunSuite

import scala.beans.BeanProperty
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, _}
import scala.util.{Failure, Success}
import scala.concurrent.duration.Duration
class FutureSpec extends FunSuite {


  test("Future测试") {
     val start = System.currentTimeMillis()
     Await.result(FutureService.init, Duration.Inf)
     val a = FutureService.save(FutureModel("1", "aa"))
     val b = FutureService.save(FutureModel("2", "bb"))
     Await.result(a, Duration.Inf)
     Await.result(b, Duration.Inf)
     Await.result(FutureService.find(), Duration.Inf)
     printf((System.currentTimeMillis() - start) / 1000 + "")

    /* val w = new CountDownLatch(1)
     async {
       val start = System.currentTimeMillis()
       await(FutureService.init)
       val a = FutureService.save(FutureModel("1", "aa"))
       val b = FutureService.save(FutureModel("2", "bb"))
       await(a)
       await(b)
       val res = await(FutureService.find())
      // assert(res.size == 4)
       printf((System.currentTimeMillis() - start) / 1000 + "")
       w.countDown()
     }
     w.await()*/

   /* val w = new CountDownLatch(1)
    FutureService.init.onComplete {
      case Success(res) => FutureService.save(FutureModel("1", "aa")).onComplete {
        case Success(res) => FutureService.save(FutureModel("2", "bb")).onComplete {
          case Success(res) =>
            FutureService.find().onComplete {
              case Success(res) =>
                println(res)
                w.countDown()
              case Failure(ex) => ex.printStackTrace()
            }
        }
        case Failure(ex) => ex.printStackTrace()
      }
      case Failure(ex) => ex.printStackTrace()
    }
    w.await()*/
  }

}

object FutureService {


  def init: Future[String] =Future {
    Thread.sleep(1000)
    println(Thread.currentThread().getName + "init")
   ""
  }

  def save(m: FutureModel): Future[String] =Future {
    Thread.sleep(10000)
    println(Thread.currentThread().getName + "save")
    ""
  }

  def find(): Future[List[Map[String, AnyRef]]] = Future {
    Thread.sleep(1)
    val res = collection.mutable.ArrayBuffer[Map[String, AnyRef]]()
    println(Thread.currentThread().getName + "find")
    //result.failure(new Exception("aaaaa"))
    res.toList
  }

}

case class FutureModel(@BeanProperty id: String, @BeanProperty name: String)