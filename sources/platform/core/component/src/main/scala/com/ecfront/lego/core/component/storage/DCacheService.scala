package com.ecfront.lego.core.component.storage

import com.ecfront.lego.core.component.BasicService
import com.ecfront.lego.core.component.cache.DCacheProcessor
import com.ecfront.lego.core.foundation.protocol.{Req, Resp}
import com.ecfront.lego.core.foundation.{IdModel, StandardCode}
import com.ecfront.storage.PageModel

trait DCacheService[M <: IdModel] extends BasicService[M] {

  private val processor = DCacheProcessor[M](modelClazz)

  override protected def doFindAll(request: Req): Resp[List[M]] = ???

  override protected def doGetByCondition(condition: String, request: Req): Resp[M] = ???

  override protected def doPageByCondition(condition: String, pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = ???

  override protected def doSave(model: M, request: Req): Resp[String] = {
    processor.save(model.id, model)
    Resp.success(model.id)
  }

  override protected def doFindByCondition(condition: String, request: Req): Resp[List[M]] = ???

  override protected def doPageAll(pageNumber: Long, pageSize: Long, request: Req): Resp[PageModel[M]] = ???

  override protected def doGetById(id: String, request: Req): Resp[M] = {
    val res = processor.get(id)
    if (res != null) {
      Resp.success(res.get)
    } else {
      Resp.fail(StandardCode.NOT_FOUND_CODE, "")
    }
  }

  override protected def doUpdate(id: String, model: M, request: Req): Resp[String] = {
    processor.update(id, model)
    Resp.success(id)
  }

  override protected def doDeleteById(id: String, request: Req): Resp[String] = {
    processor.delete(id)
    Resp.success(id)
  }

  override protected def doDeleteAll(request: Req): Resp[List[String]] = {
    processor.deleteAll()
    Resp.success(null)
  }

  override protected def doDeleteByCondition(condition: String, request: Req): Resp[List[String]] = ???

}

