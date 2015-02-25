package com.ecfront.lego.core.component.storage

import java.util.concurrent.CountDownLatch

import com.ecfront.lego.core.ComponentInfo
import com.ecfront.lego.core.component.BasicService
import com.ecfront.lego.core.component.protocol.{Req, Resp}
import com.ecfront.lego.core.foundation.{StandardCode, IdModel}
import com.ecfront.storage.PageModel
import io.vertx.core.shareddata.AsyncMap
import io.vertx.core.{AsyncResult, Handler}

trait DCacheService[M <: IdModel] extends BasicService[M] {

  private val cacheName = "lego_cache_" + modelClazz.getSimpleName
  private var CACHE: AsyncMap[String, M] = _

  init

  private def init: Unit = {
    val latch = new CountDownLatch(1)
    ComponentInfo.vertx.sharedData().getClusterWideMap(cacheName, new Handler[AsyncResult[AsyncMap[String, M]]] {
      override def handle(res: AsyncResult[AsyncMap[String, M]]): Unit = {
        if (res.succeeded()) {
          CACHE = res.result()
        } else {
          logger.error("Init cache[%s] fail.".format(cacheName), res.cause())
        }
        latch.countDown()
      }
    })
    latch.await()
  }

  override protected def doFindAll(request: Req): Resp[List[M]] = ???

  override protected def doGetByCondition(condition: String, request: Req): Resp[M] = ???

  override protected def doPageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = ???

  override protected def doSave(model: M, request: Req): Resp[String] = {
    val latch = new CountDownLatch(1)
    CACHE.put(model.id, model, new Handler[AsyncResult[Void]] {
      override def handle(res: AsyncResult[Void]): Unit = {
        if (!res.succeeded()) {
          logger.error("Save cache[%s] fail : %s".format(cacheName, model.id), res.cause())
        }
        latch.countDown()
      }
    })
    latch.await()
    Resp.success(model.id)
  }

  override protected def doFindByCondition(condition: String, request: Req): Resp[List[M]] = ???

  override protected def doPageAll(pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = ???

  override protected def doGetById(id: String, request: Req): Resp[M] = {
    val latch = new CountDownLatch(1)
    var result: Resp[M] = Resp.fail(StandardCode.SERVICE_UNAVAILABLE_CODE,"")
    CACHE.get(id, new Handler[AsyncResult[M]] {
      override def handle(res: AsyncResult[M]): Unit = {
        if (res.succeeded()) {
          result = Resp.success(res.result())
        } else {
          result = Resp.fail(StandardCode.INTERNAL_SERVER_CODE,"Get cache[%s] fail : %s".format(cacheName, id))
          logger.error("Get cache[%s] fail : %s".format(cacheName, id), res.cause())
        }
        latch.countDown()
      }
    })
    latch.await()
    result
  }

  override protected def doUpdate(id: String, model: M, request: Req): Resp[String] = {
    val latch = new CountDownLatch(1)
    CACHE.replace(id, model, new Handler[AsyncResult[M]] {
      override def handle(res: AsyncResult[M]): Unit = {
        if (!res.succeeded()) {
          logger.error("Update cache[%s] fail : %s".format(cacheName, id), res.cause())
        }
        latch.countDown()
      }
    })
    latch.await()
    Resp.success(id)
  }

  override protected def doDeleteById(id: String, request: Req): Resp[String] = {
    val latch = new CountDownLatch(1)
    CACHE.remove(id, new Handler[AsyncResult[M]] {
      override def handle(res: AsyncResult[M]): Unit = {
        if (!res.succeeded()) {
          logger.error("Delete cache[%s] fail : %s".format(cacheName, id), res.cause())
        }
        latch.countDown()
      }
    })
    latch.await()
    Resp.success(id)
  }

  override protected def doDeleteAll(request: Req): Resp[List[String]] = {
    val latch = new CountDownLatch(1)
    CACHE.clear(new Handler[AsyncResult[Void]] {
      override def handle(res: AsyncResult[Void]): Unit = {
        if (!res.succeeded()) {
          logger.error("DeleteAll cache[%s] fail .".format(cacheName), res.cause())
        }
        latch.countDown()
      }
    })
    latch.await()
    Resp.success(null)
  }

  override protected def doDeleteByCondition(condition: String, request: Req): Resp[List[String]] = ???

}

