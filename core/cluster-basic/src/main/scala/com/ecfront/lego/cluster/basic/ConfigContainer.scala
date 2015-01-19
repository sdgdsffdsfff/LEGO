package com.ecfront.lego.cluster.basic

import java.io.File

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JValue

import scala.io.Source

/**
 * Created by sunisle on 2014/11/14.
 */
object ConfigContainer extends LazyLogging {

 private val SYS_PROP_CONFIG = "config"

   val mainConfig: JValue = {
    val configFile="config.json"
    val configPath = if (System.getProperties.containsKey(SYS_PROP_CONFIG)) System.getProperty(SYS_PROP_CONFIG)+File.pathSeparator+ configFile else getClass.getResource("/").getPath + configFile
    if (new File(configPath).exists()) {
      val config=parse(Source.fromFile(configPath).mkString)
      if(config==null){
        logger.error("The Main-Config [" + configPath + "] parse error!")
        null
      }else{
        config
      }
    } else {
      logger.error("The Main-Config [" + configPath + "] NOT found!")
      null
    }
  }

}
