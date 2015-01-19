package com.ecfront.lego.cluster.basic.communication

/**
 * Created by sunisle on 2014/11/14.
 */
case class RequestProtocol(
                             val id:String,
                            val appId:String,
                            val userId:String,
                            val body:Any)
