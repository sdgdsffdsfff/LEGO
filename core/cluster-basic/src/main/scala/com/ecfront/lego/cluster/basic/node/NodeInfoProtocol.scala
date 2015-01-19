package com.ecfront.lego.cluster.basic.node

/**
 * Created by sunisle on 2014/11/14.
 */
case class NodeInfoProtocol(
                     val id: String,
                     val componentId: String,
                     val componentVersion: String,
                     val ip: String,
                     val port: Int,
                     val createTime: Long
                     )
