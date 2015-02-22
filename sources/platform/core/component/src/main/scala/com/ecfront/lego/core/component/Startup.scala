package com.ecfront.lego.core.component

import java.util.concurrent.CountDownLatch

import com.ecfront.lego.core.CoreInfo
import com.typesafe.scalalogging.slf4j.LazyLogging
import io.vertx.core.{AsyncResult, Handler, Vertx, VertxOptions}
import io.vertx.spi.cluster.impl.hazelcast.HazelcastClusterManager

object Startup extends LazyLogging {

  def startup: Unit = {
    val latch = new CountDownLatch(1)
    Vertx.clusteredVertx(new VertxOptions().setClustered(true).setClusterManager(new HazelcastClusterManager()), new Handler[AsyncResult[Vertx]] {
      override def handle(res: AsyncResult[Vertx]): Unit = {
        if (res.failed()) {
          logger.error("Startup error.", res.cause())
        } else if (res.succeeded()) {
          CoreInfo.vertx = res.result()
          logger.info("Startup success .")
        }
        latch.countDown()
      }
    })
    latch.await()
  }

}
