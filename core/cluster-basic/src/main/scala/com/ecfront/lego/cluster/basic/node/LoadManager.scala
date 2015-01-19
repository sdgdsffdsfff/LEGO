package com.ecfront.lego.cluster.basic.node

import akka.actor.{ActorRef, Cancellable, Props}
import com.ecfront.lego.cluster.basic.node.LoadInfoProtocol
import com.ecfront.lego.cluster.basic.{ConfigContainer, Manager}
import com.typesafe.scalalogging.slf4j.LazyLogging


object LoadManager extends LazyLogging {

  private[BasicClusterManager] val clusterLoadInfos=new collection.mutable.HashMap[String,LoadInfoProtocol]

  private[BasicClusterManager] def loadBalance:Map[(String,String),Array[String]] = {
    val idx=item._2.length
    val address=for(i <- idx){
      item._2(idx)
    }

  }

  private[BasicClusterManager] def getLoadInfo: LoadInfoProtocol = {
    /*    val cpuUsedRate=
        val memUsedRate=
        val memTotalMB=
        val memFreeMB=
        val storageUsedRate=
        val storageTotalGB=
        val storageFreeGB=
        val ioWaitRate=*/
    val loadInfo = new LoadInfoProtocol
    loadInfo
  }

}
