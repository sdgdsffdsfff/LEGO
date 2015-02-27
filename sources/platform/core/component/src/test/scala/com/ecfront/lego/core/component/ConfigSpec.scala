package com.ecfront.lego.core.component

import org.scalatest._


class ConfigSpec extends FunSuite {

  Startup.startup

  test("配置测试") {
    assert(ComponentInfo.id == "test")
  }
}





