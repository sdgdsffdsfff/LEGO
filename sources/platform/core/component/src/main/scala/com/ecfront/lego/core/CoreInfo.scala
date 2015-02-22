package com.ecfront.lego.core

import io.vertx.core.Vertx

object CoreInfo {

  var code: String = _
  var version: String = _
  var name: String = _

  val dependency = collection.mutable.Map[String, String]()

  var vertx: Vertx = null
}
