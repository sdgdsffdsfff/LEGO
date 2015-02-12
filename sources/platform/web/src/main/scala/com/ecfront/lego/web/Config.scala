package com.ecfront.lego.web

import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * 核心配置类
 */
case class Config(port: Int)


object Config extends LazyLogging {

  private val config: Config = {
    logger.info("Load config.")
    com.ecfront.common.ConfigHelper.init(this.getClass.getResource("/config.json").getPath, classOf[Config]).get
  }

  def getPort = config.port

}

