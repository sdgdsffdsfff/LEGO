package com.ecfront.lego.core.component

object MockStartup extends BasicStartup[ComponentInfo] {
  override def registerServices: Unit = {

  }
}

class ComponentInfo extends BasicComponentInfo {
  override def id: String = "mock"

  override def version: String = "1.0"

  override def name: String = "模拟"

  override def dependency: Map[String, String] = Map[String, String]()
}


