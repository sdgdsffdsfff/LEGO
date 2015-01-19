package com.ecfront.lego.cluster.basic.node

import scala.concurrent.duration._
import akka.actor.{ActorRef, Cancellable, Props, Actor}
import com.ecfront.lego.cluster.basic.{ConfigContainer, Manager}
import com.typesafe.scalalogging.slf4j.LazyLogging
import scala.math.random

/**
 * Created by sunisle on 2014/11/14.
 */
abstract class BasicClusterManager extends Actor with LazyLogging {

  def registerNode(nodeInfo: NodeInfoProtocol): Boolean

  def unRegisterNode(nodeId: String): Unit

  def reportLoad(loadInfo: LoadInfoProtocol): Unit

  def checkAlive(nodeId: String): Boolean

  override def receive: Receive = {
    case BasicClusterManager.REPORT_LOAD_FLAG => {
      reportLoad(LoadManager.getLoadInfo)
      BasicClusterManager.updateAvailableComponents(LoadManager.loadBalance)
    }
    case BasicClusterManager.CHECK_ALIVE_FLAG => checkAlive(NodeManager.nodeId)
  }

}

object BasicClusterManager extends LazyLogging {

  private val REPORT_LOAD_FLAG = "report_load"
  private val CHECK_ALIVE_FLAG = "check_alive"
  private val VERSION_SEPARATOR = "~~"

  private val clusterAvailableComponents = new collection.mutable.HashMap[String, Array[String]]

  private val clusterManager = new HazelcastClusterManager

  private var reportLoadSchedule: Cancellable = _
  private var checkAliveSchedule: Cancellable = _

  def init: Unit = {
    if (clusterManager.registerNode(NodeManager.getNodeInfo)) {
      logger.error("Register Node error!")
    } else {
      val nodeActor = Manager.actorSystem.actorOf(Props(clusterManager.getClass, this))
      reportLoad(nodeActor)
      checkAlive(nodeActor)
    }
  }

  def destroy: Unit = {
    reportLoadSchedule.cancel()
    checkAliveSchedule.cancel()
    clusterManager.unRegisterNode(NodeManager.nodeId)
  }

  def getComponent(componentId: String, componentVersion: String): String = {
    if (clusterAvailableComponents.contains(componentId + VERSION_SEPARATOR + componentVersion)) {
      val address = clusterAvailableComponents.get(componentId + VERSION_SEPARATOR + componentVersion).get
      address((random * address.length).toInt)
    } else {
      logger.error("Not Found available component @ ComponentId:" + componentId + " ,Version:" + componentVersion)
      null
    }
  }

  def updateAvailableComponents(components: Map[(String, String), Array[String]]): Unit = {
    clusterAvailableComponents.empty
    components.foreach {
      item =>
        clusterAvailableComponents.+=(item._1._1 + item._1._2 -> item._2)
    }
  }

  private def reportLoad(actor: ActorRef): Unit = {
    reportLoadSchedule = Manager.actorSystem.scheduler.schedule(0 milliseconds, 5 minute, actor, REPORT_LOAD_FLAG)
  }

  private def checkAlive(actor: ActorRef): Unit = {
    checkAliveSchedule = Manager.actorSystem.scheduler.schedule(0 milliseconds, 5 second, actor, CHECK_ALIVE_FLAG)
  }


}
