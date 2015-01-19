package com.ecfront.lego.cluster.basic

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast

/**
 * Created by sunisle on 2014/11/14.
 */
object Manager {
  private val config = new Config()
  val hazelcastInst = Hazelcast.newHazelcastInstance(config)

  import akka.actor.ActorSystem
  val actorSystem = ActorSystem("EZ-Cluster")
}
