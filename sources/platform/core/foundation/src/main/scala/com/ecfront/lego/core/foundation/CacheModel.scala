package com.ecfront.lego.core.foundation

/**
 * 缓存实体
 * id  = address + ''.''+RequestProtocol.action
 * @param expire
 * @param level
 * @param updateEvents | 分隔
 */
case class CacheMetaModel(expire: Long, level: Long, updateEvents: Array[String]) extends IdModel

object CacheMetaModel{
  def packageId(address: String,action:String)=address+"."+action
}