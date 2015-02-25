package com.ecfront.lego.core

import com.fasterxml.jackson.databind.JsonNode
import io.vertx.core.Vertx

object ComponentInfo {

  var id: String = _
  var version: String = _
  var name: String = _

  //TODO
  val dependency = collection.mutable.Map[String, String]()

  var config:JsonNode=_

  var vertx: Vertx = null
}
