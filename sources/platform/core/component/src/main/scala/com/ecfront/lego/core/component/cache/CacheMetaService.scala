package com.ecfront.lego.core.component.cache

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.CacheMetaModel

import scala.collection.mutable.ArrayBuffer

trait CacheMetaService extends JDBCService[CacheMetaModel] {

  def init(): Unit = {
    findAll(RequestProtocol.createBySystem, {
      results =>
        CacheMetaContainer.init(results)
    })
  }

  def add(cacheMeta: CacheMetaModel): Unit = {
    save(
    "INSERT INTO %s (id,expire,level,updateEvents) values ( ? , ? , ? , ?  )".format(tableName),
    ArrayBuffer[AnyRef](cacheMeta.id, cacheMeta.expire, cacheMeta.level, cacheMeta.updateEvents.mkString("|")),
    RequestProtocol.createBySystem, {
      res =>
        CacheMetaContainer.add(cacheMeta)
    })
  }

  def delete(id: String): Unit = {
    deleteById(
    id, RequestProtocol.createBySystem, {
      res =>
        CacheMetaContainer.delete(id)
    })
  }

  def get(id: String,level:String): Unit = {
    CacheMetaContainer.get(id,level)
  }

}
