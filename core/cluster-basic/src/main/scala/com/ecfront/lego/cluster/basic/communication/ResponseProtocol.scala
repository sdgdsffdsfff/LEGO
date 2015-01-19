package com.ecfront.lego.cluster.basic.communication

/**
 * Created by sunisle on 2014/11/14.
 */
case class ResponseProtocol(
                             val code:String,
                            val message:String,
                            val body:Any)
