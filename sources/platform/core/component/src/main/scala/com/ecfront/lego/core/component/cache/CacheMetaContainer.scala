package com.ecfront.lego.core.component.cache

import com.ecfront.lego.core.foundation.CacheMetaModel


object CacheMetaContainer {

  /**
   * 缓存元数据对象，key为CacheMeta.pk，value为CacheMeta
   */
  private val CACHE_METAS = Map[String, CacheMetaModel]()
  /**
   * 更新触发缓存元数据对象，key为CacheMeta.updateEvents，value为pk
   */
  private val UPDATE_EVENTS= Map[String, List[String]]()

  /**
   * 初始化缓存元数据
   *
   * @param cacheMetas 来自缓存组件的元数据列表
   */
  def init(cacheMetas: JsonArray) {
    CACHE_METAS.clear
    UPDATE_EVENTS.clear
    if (cacheMetas != null) {
      var cacheMeta: CacheMeta = null
      import scala.collection.JavaConversions._
      for (meta <- cacheMetas) {
        cacheMeta = new CacheMeta(meta.asInstanceOf[JsonObject])
        CACHE_METAS.put(cacheMeta.getPk, cacheMeta)
        for (str <- cacheMeta.getUpdateEvents) {
          if (!UPDATE_EVENTS.containsKey(str)) {
            UPDATE_EVENTS.put(str, new ArrayList[String])
          }
          UPDATE_EVENTS.get(str).add(cacheMeta.getPk)
        }
      }
    }
  }

  /**
   * 是否为空
   *
   */
  def isEmpty: Boolean = {
    return CACHE_METAS.isEmpty
  }

  /**
   * 获取缓存级别
   *
   * @param category 类别
   * @return null:无缓存
   */
  def getCacheLevel(category: String): String = {
    return if (CACHE_METAS.containsKey(category)) CACHE_METAS.get(category).getLevel else null
  }

  /**
   * 是否存在缓存元数据
   *
   * @param category 类别
   * @param level    级别
   * @return false:无缓存
   */
  def isCacheMeta(category: String, level: String): Boolean = {
    return if (CACHE_METAS.containsKey(category)) CACHE_METAS.get(category).getLevel.equalsIgnoreCase(level) else false
  }

  /**
   * 获取缓存元数据
   *
   * @param category 类别
   * @return 缓存元数据
   */
  def get(category: String): CacheMeta = {
    return if (CACHE_METAS.containsKey(category)) CACHE_METAS.get(category) else null
  }

  /**
   * 是否存在更新触发缓存元数据
   *
   * @param event 更新触发事件
   * @return 要更新的缓存类别， null:此事件不用更新缓存
   */
  def getCacheCategoryByUpdateEvent(event: String): List[String] = {
    return if (UPDATE_EVENTS.containsKey(event)) UPDATE_EVENTS.get(event) else null
  }
}
