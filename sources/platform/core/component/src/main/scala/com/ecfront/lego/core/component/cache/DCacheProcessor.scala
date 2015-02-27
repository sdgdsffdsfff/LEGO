package com.ecfront.lego.core.component.cache

import java.util.concurrent.CountDownLatch

import com.ecfront.common.JsonHelper
import com.ecfront.lego.core.component.ComponentInfo
import com.typesafe.scalalogging.slf4j.LazyLogging
import io.vertx.core.shareddata.AsyncMap
import io.vertx.core.{AsyncResult, Handler}

case class DCacheProcessor[M <: AnyRef](modelClazz: Class[M]) extends LazyLogging with CacheProcessor[M] {


  private val cacheName = "lego_cache_" + modelClazz.getName
  private var CACHE: AsyncMap[String, String] = _

  init()

  private def init(): Unit = {
    val latch = new CountDownLatch(1)
    ComponentInfo.vertx.sharedData().getClusterWideMap(cacheName, new Handler[AsyncResult[AsyncMap[String, String]]] {
      override def handle(res: AsyncResult[AsyncMap[String, String]]): Unit = {
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

  override def save(id: String, model: M) {
    val latch = new CountDownLatch(1)
    CACHE.put(id, JsonHelper.toJsonString(model), new Handler[AsyncResult[Void]] {
      override def handle(res: AsyncResult[Void]): Unit = {
        if (!res.succeeded()) {
          logger.error("Save cache[%s] fail : %s".format(cacheName, id), res.cause())
        }
        latch.countDown()
      }
    })
    latch.await()
  }

  override def get(id: String): Option[M] = {
    val latch = new CountDownLatch(1)
    var result: Option[M] = null
    CACHE.get(id, new Handler[AsyncResult[String]] {
      override def handle(res: AsyncResult[String]): Unit = {
        if (res.succeeded()) {
          result =if(res.result()==null) null else Some(JsonHelper.toObject(res.result(), modelClazz))
        } else {
          logger.error("Get cache[%s] fail : %s".format(cacheName, id), res.cause())
        }
        latch.countDown()
      }
    })
    latch.await()
    result
  }

  override def update(id: String, model: M) {
    val latch = new CountDownLatch(1)
    CACHE.replace(id, JsonHelper.toJsonString(model), new Handler[AsyncResult[String]] {
      override def handle(res: AsyncResult[String]): Unit = {
        if (!res.succeeded()) {
          logger.error("Update cache[%s] fail : %s".format(cacheName, id), res.cause())
        }
        latch.countDown()
      }
    })
    latch.await()
  }

  override def delete(id: String) {
    val latch = new CountDownLatch(1)
    CACHE.remove(id, new Handler[AsyncResult[String]] {
      override def handle(res: AsyncResult[String]): Unit = {
        if (!res.succeeded()) {
          logger.error("Delete cache[%s] fail : %s".format(cacheName, id), res.cause())
        }
        latch.countDown()
      }
    })
    latch.await()
  }

  override def deleteAll() {
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
  }

}

