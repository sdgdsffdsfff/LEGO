package com.ecfront.lego.core.component.cache

import io.vertx.core.shareddata.AsyncMap
import io.vertx.core.{AsyncResult, Handler, Vertx}

object VertxCacheService extends CacheService {

  private var mainMap: AsyncMap[String, Any] = _
  private var appMap: AsyncMap[String, List[String]] = _
  private var userMap: AsyncMap[String, List[String]] = _

  def init(vertx: Vertx): Unit = {
    vertx.sharedData().getClusterWideMap("lego_cache_main", new Handler[AsyncResult[AsyncMap[String, Any]]] {
      override def handle(res: AsyncResult[AsyncMap[String, Any]]): Unit = {
        if (res.succeeded()) {
          mainMap = res.result()
        } else {
          logger.error("Init main cache fail.", res.cause())
        }
      }
    })
    vertx.sharedData().getClusterWideMap("lego_cache_app", new Handler[AsyncResult[AsyncMap[String, List[String]]]] {
      override def handle(res: AsyncResult[AsyncMap[String, List[String]]]): Unit = {
        if (res.succeeded()) {
          appMap = res.result()
        } else {
          logger.error("Init app cache fail.", res.cause())
        }
      }
    })
    vertx.sharedData().getClusterWideMap("lego_cache_user", new Handler[AsyncResult[AsyncMap[String, List[String]]]] {
      override def handle(res: AsyncResult[AsyncMap[String, List[String]]]): Unit = {
        if (res.succeeded()) {
          userMap = res.result()
        } else {
          logger.error("Init user cache fail.", res.cause())
        }
      }
    })
  }

  override def put(body: Any, address: String, resourceId: String = "", appId: String = "", userId: String = ""): Unit = {
    val key = packageMainKey(address, resourceId, appId, userId)
    mainMap.put(key, body, new Handler[AsyncResult[Void]] {
      override def handle(res: AsyncResult[Void]): Unit = {
        if (!res.succeeded()) {
          logger.warn("Put main cluster cache fail : %s".format(key), res.cause())
        }
      }
    })
    if (appId != "") {
      appMap.get(appId, new Handler[AsyncResult[List[String]]] {
        override def handle(res: AsyncResult[List[String]]): Unit = {
          if (res.succeeded()) {
            appMap.put(appId, res.result().::(key), new Handler[AsyncResult[Void]] {
              override def handle(res: AsyncResult[Void]): Unit = {
                if (!res.succeeded()) {
                  logger.warn("Put app cluster cache fail : %s".format(appId), res.cause())
                }
              }
            })
          } else {
            logger.warn("Get app cluster cache fail : %s".format(appId), res.cause())
          }
        }
      })
    }
    if (userId != "") {
      userMap.get(userId, new Handler[AsyncResult[List[String]]] {
        override def handle(res: AsyncResult[List[String]]): Unit = {
          if (res.succeeded()) {
            userMap.put(userId, res.result().::(key), new Handler[AsyncResult[Void]] {
              override def handle(res: AsyncResult[Void]): Unit = {
                if (!res.succeeded()) {
                  logger.warn("Put user cluster cache fail : %s".format(userId), res.cause())
                }
              }
            })
          } else {
            logger.warn("Get user cluster cache fail : %s".format(userId), res.cause())
          }
        }
      })
    }
  }

  override def remove(address: String, resourceId: String = "", appId: String = "", userId: String = ""): Unit = {
    val key = packageMainKey(address, resourceId, appId, userId)
    mainMap.remove(key, new Handler[AsyncResult[Any]] {
      override def handle(res: AsyncResult[Any]): Unit = {
        if (!res.succeeded()) {
          logger.warn("Remove main cluster cache fail : %s".format(key), res.cause())
        }
      }
    })
  }

  override def removeByAppId(appId: String): Unit = {
    appMap.remove(appId, new Handler[AsyncResult[List[String]]] {
      override def handle(res: AsyncResult[List[String]]): Unit = {
        if (res.succeeded()) {
          val keys = res.result()
          if (keys != null && keys.nonEmpty) {
            keys.foreach {
              key =>
                mainMap.remove(key, new Handler[AsyncResult[Any]] {
                  override def handle(res: AsyncResult[Any]): Unit = {
                    if (!res.succeeded()) {
                      logger.warn("Remove main cluster cache fail : %s".format(key), res.cause())
                    }
                  }
                })
            }
          }
        } else {
          logger.warn("Remove app cluster cache fail : %s".format(appId), res.cause())
        }
      }
    })
  }

  override def removeByUserId(userId: String): Unit = {
    userMap.remove(userId, new Handler[AsyncResult[List[String]]] {
      override def handle(res: AsyncResult[List[String]]): Unit = {
        if (res.succeeded()) {
          val keys = res.result()
          if (keys != null && keys.nonEmpty) {
            keys.foreach {
              key =>
                mainMap.remove(key, new Handler[AsyncResult[Any]] {
                  override def handle(res: AsyncResult[Any]): Unit = {
                    if (!res.succeeded()) {
                      logger.warn("Remove main cluster cache fail : %s".format(key), res.cause())
                    }
                  }
                })
            }
          }
        } else {
          logger.warn("Remove user cluster cache fail : %s".format(userId), res.cause())
        }
      }
    })
  }

  override def get(address: String, callback: => Any => Unit, resourceId: String = "", appId: String = "", userId: String = ""): Unit = {
    val key = packageMainKey(address, resourceId, appId, userId)
    mainMap.get(key, new Handler[AsyncResult[Any]] {
      override def handle(res: AsyncResult[Any]): Unit = {
        if (res.succeeded()) {
          callback(res.result())
        } else {
          logger.warn("Get cluster cache fail : %s".format(key), res.cause())
        }
      }
    })
  }

}
