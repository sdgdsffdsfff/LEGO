package com.ecfront.lego.core.component

import java.util.concurrent.CountDownLatch

import com.ecfront.common.ConfigHelper
import com.ecfront.lego.core.component.storage.JDBCService
import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.slf4j.LazyLogging
import io.vertx.core.{AsyncResult, Handler, Vertx, VertxOptions}
import io.vertx.spi.cluster.impl.hazelcast.HazelcastClusterManager

object Startup extends App with LazyLogging {

  def startup: Unit = {
    loadConfig
    serviceInit
    startupCluster
  }

  private def loadConfig: Unit = {
    val config: JsonNode = ConfigHelper.init(this.getClass.getResource("/config.json").getPath).get
    ComponentInfo.id = config.get("id").asText()
    ComponentInfo.name = config.get("name").asText()
    ComponentInfo.version = config.get("version").asText()
    //TODO add dependency
    ComponentInfo.config = config.get("config")
  }

  private def serviceInit: Unit = {
    JDBCService.init
  }

  private def startupCluster: Unit = {
    val latch = new CountDownLatch(1)
    Vertx.clusteredVertx(new VertxOptions().setClustered(true).setClusterManager(new HazelcastClusterManager()), new Handler[AsyncResult[Vertx]] {
      override def handle(res: AsyncResult[Vertx]): Unit = {
        if (res.failed()) {
          logger.error("Startup error.", res.cause())
        } else if (res.succeeded()) {
          ComponentInfo.vertx = res.result()
          logger.info("Startup success .")
        }
        latch.countDown()
      }
    })
    latch.await()
  }

  startup

}
