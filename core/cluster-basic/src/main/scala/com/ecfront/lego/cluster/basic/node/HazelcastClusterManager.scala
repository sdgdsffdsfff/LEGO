package com.ecfront.lego.cluster.basic.node

import com.ecfront.lego.cluster.basic.{Manager, ConfigContainer}
import com.hazelcast.core.{EntryEvent, MapEvent, EntryListener, IMap}
import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * Created by sunisle on 2014/11/14.
 */
class HazelcastClusterManager extends BasicClusterManager  with LazyLogging{

  override def registerNode(nodeInfo:NodeInfoProtocol): Boolean = {
    HazelcastClusterManager.hlNodeInfo.put(nodeInfo.id, nodeInfo)
    true
  }


  override def unRegisterNode(nodeId:String): Unit = {
    HazelcastClusterManager.hlNodeInfo.remove(nodeId)
  }

  override def reportLoad(loadInfo: LoadInfoProtocol): Unit = {
    HazelcastClusterManager.hlLoadInfo.put(NodeManager.nodeId,loadInfo)
  }

  override def checkAlive(nodeId: String): Boolean = {
    true
  }

}

object HazelcastClusterManager  extends LazyLogging{

  private val HL_NODE_INFO_FLAG = "node_info"
  private val HL_LOAD_INFO_FLAG = "load_info"

  private val hlNodeInfo: IMap[String, NodeInfoProtocol] = Manager.hazelcastInst.getMap(HL_NODE_INFO_FLAG)
  hlNodeInfo.addEntryListener(new NodeChangeListener, true)

  private val hlLoadInfo: IMap[String, LoadInfoProtocol] = Manager.hazelcastInst.getMap(HL_LOAD_INFO_FLAG)
  hlLoadInfo.addEntryListener(new LoadChangeListener, true)

}

class NodeChangeListener extends EntryListener[String, NodeInfoProtocol] with LazyLogging{
  override def entryAdded(entryEvent: EntryEvent[String, NodeInfoProtocol]): Unit = {
    logger.info("Add Node:"+entryEvent.getValue.toString)
    NodeManager.clusterNodeInfos.+=(entryEvent.getKey -> entryEvent.getValue)
  }

  override def entryUpdated(entryEvent: EntryEvent[String, NodeInfoProtocol]): Unit = {
    logger.info("Update Node:"+entryEvent.getValue.toString)
    NodeManager.clusterNodeInfos.+=(entryEvent.getKey -> entryEvent.getValue)
  }

  override def entryEvicted(entryEvent: EntryEvent[String, NodeInfoProtocol]): Unit = {
    logger.info("Evict Node:"+entryEvent.getValue.toString)
    NodeManager.clusterNodeInfos.-=(entryEvent.getKey)
  }

  override def mapEvicted(mapEvent: MapEvent): Unit = {
    logger.error("Node Evicted.")
    NodeManager.clusterNodeInfos.clear()
  }

  override def entryRemoved(entryEvent: EntryEvent[String, NodeInfoProtocol]): Unit = {
    logger.info("Remove Node:"+entryEvent.getValue.toString)
    NodeManager.clusterNodeInfos.-=(entryEvent.getKey)
  }

  override def mapCleared(mapEvent: MapEvent): Unit = {
    logger.error("Node Cleared.")
    NodeManager.clusterNodeInfos.clear()
  }
}

class LoadChangeListener extends EntryListener[String, LoadInfoProtocol] with LazyLogging{
  override def entryAdded(entryEvent: EntryEvent[String, LoadInfoProtocol]): Unit = {
    logger.info("Add Node:"+entryEvent.getValue.toString)
    LoadManager.clusterLoadInfos.+=(entryEvent.getKey -> entryEvent.getValue)
  }

  override def entryUpdated(entryEvent: EntryEvent[String, LoadInfoProtocol]): Unit = {
    logger.info("Update Node:"+entryEvent.getValue.toString)
    LoadManager.clusterLoadInfos.+=(entryEvent.getKey -> entryEvent.getValue)
  }

  override def entryEvicted(entryEvent: EntryEvent[String, LoadInfoProtocol]): Unit = {
    logger.info("Evict Node:"+entryEvent.getValue.toString)
    LoadManager.clusterLoadInfos.-=(entryEvent.getKey)
  }

  override def mapEvicted(mapEvent: MapEvent): Unit = {
    logger.error("Node Evicted.")
    LoadManager.clusterLoadInfos.clear()
  }

  override def entryRemoved(entryEvent: EntryEvent[String, LoadInfoProtocol]): Unit = {
    logger.info("Remove Node:"+entryEvent.getValue.toString)
    LoadManager.clusterLoadInfos.-=(entryEvent.getKey)
  }

  override def mapCleared(mapEvent: MapEvent): Unit = {
    logger.error("Node Cleared.")
    LoadManager.clusterLoadInfos.clear()
  }
}

