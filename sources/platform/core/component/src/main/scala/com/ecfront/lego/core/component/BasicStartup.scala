package com.ecfront.lego.core.component

import java.lang.reflect.ParameterizedType
import java.util.concurrent.CountDownLatch

import com.ecfront.common.ConfigHelper
import com.ecfront.lego.core.component.storage.JDBCService
import com.fasterxml.jackson.databind.JsonNode
import com.typesafe.scalalogging.slf4j.LazyLogging
import io.vertx.core.{AsyncResult, Handler, Vertx, VertxOptions}
import io.vertx.spi.cluster.impl.hazelcast.HazelcastClusterManager

trait BasicStartup[C <: BasicComponentInfo] extends App with LazyLogging {

  private val modelClazz = this.getClass.getGenericInterfaces()(0).asInstanceOf[ParameterizedType].getActualTypeArguments()(0).asInstanceOf[Class[C]]
  private val componentInfo: BasicComponentInfo = modelClazz.newInstance()

  def getComponentInfo = {
    componentInfo
  }

  def startup(): Unit = {
    loadConfig()
    serviceInit()
    startupCluster()
    registerServices()
  }

  private def loadConfig(): Unit = {
    componentInfo.config = ConfigHelper.init(this.getClass.getResource("/config.json").getPath).orNull
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
          componentInfo.vertx = res.result()
          logger.info("Startup success .")
        }
        latch.countDown()
      }
    })
    latch.await()
  }

  def registerServices(): Unit

  startup()

}

trait BasicComponentInfo {
  def id: String

  def version: String

  def name: String

  def dependency: Map[String, String]

  var config: JsonNode = _

  var vertx: Vertx = null
}
