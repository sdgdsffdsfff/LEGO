package com.ecfront.lego.core.component

import java.util.concurrent.CountDownLatch

import com.ecfront.common.ConfigHelper
import com.ecfront.lego.core.component.communication.{Communication, VertxCommunication}
import com.ecfront.lego.core.component.storage.JDBCService
import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.slf4j.LazyLogging
import io.vertx.core.{AsyncResult, Handler, Vertx, VertxOptions}
import io.vertx.spi.cluster.impl.hazelcast.HazelcastClusterManager

trait BasicStartup extends App with LazyLogging {

  val id: String
  val version: String
  val name: String
  val dependency: Map[String, String]

  def registerServices(): Unit

  def startup(): Unit = {
    loadConfig()
    serviceInit()
    startupCluster()
    registerServices()
  }

  private def loadConfig(): Unit = {
    Global.id = id
    Global.version = version
    Global.name = name
    Global.dependency = dependency
    Global.config = ConfigHelper.init(this.getClass.getResource("/config.json").getPath).orNull
  }

  private def serviceInit(): Unit = {
    JDBCService.init
  }

  private def startupCluster(): Unit = {
    val latch = new CountDownLatch(1)
    Vertx.clusteredVertx(new VertxOptions().setClustered(true).setClusterManager(new HazelcastClusterManager()), new Handler[AsyncResult[Vertx]] {
      override def handle(res: AsyncResult[Vertx]): Unit = {
        if (res.failed()) {
          logger.error("Startup error.", res.cause())
        } else if (res.succeeded()) {
          Global.vertx = res.result()
          logger.info("Startup success .")
        }
        latch.countDown()
      }
    })
    latch.await()
  }


  startup()

}


object Global {
  var id: String = _
  var version: String = _
  var name: String = _
  var dependency: Map[String, String] = _
  var config: JsonNode = null
  val communication: Communication = new VertxCommunication
  var vertx: Vertx = null
}
