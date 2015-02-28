package com.ecfront.lego.core.component

import org.scalatest._


class ConfigSpec extends FunSuite {

  MockStartup.startup()

  test("配置测试") {
    assert(MockStartup.getComponentInfo.id == "test")
  }
}





