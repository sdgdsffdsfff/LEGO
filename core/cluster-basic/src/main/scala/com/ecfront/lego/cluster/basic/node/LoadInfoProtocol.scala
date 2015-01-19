package com.ecfront.lego.cluster.basic.node

/**
 * Created by sunisle on 2014/11/14.
 */
case class LoadInfoProtocol() {
  var cpuUsedRate: Int=_
  var memUsedRate: Int=_
  var memTotalMB: Int=_
  var memFreeMB: Int=_
  var storageUsedRate: Int=_
  var storageTotalGB: Int=_
  var storageFreeGB: Int=_
  var ioWaitRate: Int=_
}
