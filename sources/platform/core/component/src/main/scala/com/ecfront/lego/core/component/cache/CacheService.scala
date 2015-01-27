package com.ecfront.lego.core.component.cache

import com.typesafe.scalalogging.slf4j.LazyLogging

trait CacheService extends LazyLogging {

  def put(body: Any, address: String, resourceId: String = "", appId: String = "", userId: String = ""): Unit

  def remove(address: String, resourceId: String = "", appId: String = "", userId: String = ""): Unit

  def removeByAppId(appId: String): Unit

  def removeByUserId(userId: String): Unit

  def get(address: String, callback: => Any => Unit, resourceId: String = "", appId: String = "", userId: String = ""): Unit

  protected def packageMainKey(address: String, resourceId: String = "", appId: String = "", userId: String = ""): String = {
    address + "-" + resourceId + "-" + appId + "-" + userId
  }
}
