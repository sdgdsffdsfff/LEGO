package com.ecfront.lego.core.component

import com.ecfront.lego.core.component.keylog.KeyLogService
import com.ecfront.lego.core.foundation.protocol.Req
import com.ecfront.lego.core.foundation.StandardCode
import org.scalatest._


class KeyLogSpec extends FunSuite {

  MockStartup.startup()

  test("关键日志测试") {
    val request = Req("0000", "jzy", "test_app")
    request.action = "test key log"
    KeyLogService.success("ok", request)
    KeyLogService.badRequest("request error", request)
    val logs = KeyLogService.findAll(request).body
    assert(logs.size == 2)
    assert(logs(0).code == StandardCode.BAD_REQUEST_CODE)
    assert(logs(0).message == "request error")
    assert(logs(0).trackId == "0000")
    assert(logs(0).componentId == "test")
    assert(logs(0).action == "test key log")
  }
}





