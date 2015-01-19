package com.ecfront.lego.cluster.basic.node

import com.ecfront.lego.cluster.basic.ConfigContainer
import com.typesafe.scalalogging.slf4j.LazyLogging


object NodeManager extends LazyLogging {

  private[BasicClusterManager] val nodeId = System.nanoTime() + ""

  private[BasicClusterManager] val clusterNodeInfos = new collection.mutable.HashMap[String, NodeInfoProtocol]

  private[BasicClusterManager] def getNodeInfo: NodeInfoProtocol = {
    val componentId = (ConfigContainer.mainConfig \ "componentId").as[String]
    val componentVersion = (ConfigContainer.mainConfig \ "componentVersion").as[String]
    val ip = (ConfigContainer.mainConfig \ "ip").as[String]
    val port = (ConfigContainer.mainConfig \ "port").as[Int]
    val createTime = System.currentTimeMillis()
    NodeInfoProtocol(nodeId, componentId, componentVersion, ip, port, createTime)
  }

}
