package com.ecfront.lego.core.component.cache

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.{VertxStorageService, JDBCService}
import com.ecfront.lego.core.foundation.CacheMetaModel

import scala.collection.mutable.ArrayBuffer

@deprecated
object CacheMetaService extends VertxStorageService[CacheMetaModel] {

  /**
   * 初始化缓存元数据
   *
   */
  def init(): Unit = {
    findAll(RequestProtocol.createBySystem, {
      metas =>
        CACHE_METAS.clear()
        UPDATE_EVENTS.clear()
        if (metas != null && metas.nonEmpty) {
          metas.foreach {
            meta =>
              addMeta(meta)
          }
        }
    })
  }

  def add(meta: CacheMetaModel): Unit = {
    save(
    "INSERT INTO %s (id,expire,level,updateEvents) values ( ? , ? , ? , ?  )".format(tableName),
    ArrayBuffer[AnyRef](meta.id, meta.expire, meta.level, meta.updateEvents.mkString("|")),
    RequestProtocol.createBySystem, {
      res =>
        addMeta(meta)
    })
  }


  def delete(address: String, action: String): Unit = {
    val id = CacheMetaModel.packageId(address, action)
    deleteById(
    id, RequestProtocol.createBySystem, {
      res =>
        val updateEvents = CACHE_METAS(id).updateEvents
        if (updateEvents != null && updateEvents.nonEmpty) {
          updateEvents.foreach {
            UPDATE_EVENTS(_) -= id
          }
        }
        CACHE_METAS -= id
    })
  }

  /**
   * 获取缓存元数据
   * @param address
   * @param action
   * @return
   */
  def get(address: String, action: String): CacheMetaModel = {
    val id = CacheMetaModel.packageId(address, action)
    if (CACHE_METAS.contains(id)) {
      CACHE_METAS(CacheMetaModel.packageId(address, action))
    } else {
      null
    }
  }

  /**
   * 是否存在缓存元数据
   * @param address
   * @param action
   * @param level
   * @return
   */
  def exist(address: String, action: String, level: String): Boolean = {
    val meta = get(address, action)
    meta != null && meta.level == level
  }

  /**
   * 是否存在更新触发缓存元数据
   *
   * @param event 更新触发事件
   * @return 要更新的缓存类别， null:此事件不用更新缓存
   */
  def getUpdateEvent(event: String): ArrayBuffer[String] = {
    if (UPDATE_EVENTS.contains(event)) UPDATE_EVENTS(event) else null
  }

  /**
   * 缓存元数据对象，key为CacheMeta.id，value为CacheMeta
   */
  private val CACHE_METAS = collection.mutable.Map[String, CacheMetaModel]()
  /**
   * 更新触发缓存元数据对象，key为CacheMeta.updateEvents，value为id
   */
  private val UPDATE_EVENTS = collection.mutable.Map[String, ArrayBuffer[String]]()

  private def addMeta(meta: CacheMetaModel) {
    CACHE_METAS.put(meta.id, meta)
    for (str <- meta.updateEvents) {
      if (!UPDATE_EVENTS.contains(str)) {
        UPDATE_EVENTS += (str -> new ArrayBuffer[String]())
      }
      UPDATE_EVENTS(str) += meta.id
    }
  }

}
