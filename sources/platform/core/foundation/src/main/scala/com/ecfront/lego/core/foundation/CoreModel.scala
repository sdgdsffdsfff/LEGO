package com.ecfront.lego.core.foundation

import com.ecfront.common.JsonHelper

import scala.beans.BeanProperty
import scala.collection.mutable.ArrayBuffer

abstract class IdModel {
  @BeanProperty var id: String = _
}

object IdModel {
  val ID_FLAG = "id"
  val SPLIT_FLAG = "@"
}

abstract class SecureModel extends IdModel {
  @BeanProperty var createUser: String = _
  @BeanProperty var createTime: Long = _
  @BeanProperty var updateUser: String = _
  @BeanProperty var updateTime: Long = _
}

object SecureModel {
  val SYSTEM_USER_FLAG = "system"
}

abstract class AppSecureModel extends SecureModel {
  @BeanProperty var appId: String = _
}

object AppSecureModel {
  val APP_ID_FLAG = "appId"
  val LEGO_APP_FLAG = "LEGO"
}

case class PageModel[M](
                         @BeanProperty var pageNumber: Long,
                         @BeanProperty var pageSize: Long,
                         @BeanProperty var pageTotal: Long,
                         @BeanProperty var recordTotal: Long,
                         @BeanProperty var results: List[M]
                         )

object PageModel {

  val PAGE_NUMBER_FLAG = "pageNumber"
  val PAGE_SIZE_FLAG = "pageSize"

  def toPage[M](str: String, modelClazz: Class[M]): PageModel[M] = {
    val tmp = JsonHelper.toJson(str)
    val res = tmp.get("results").elements()
    val results = ArrayBuffer[M]()
    while (res.hasNext) {
      results += JsonHelper.toObject(res.next(), modelClazz)
    }
    PageModel(
      tmp.get("pageNumber").asLong(),
      tmp.get("pageSize").asLong(),
      tmp.get("pageTotal").asLong(),
      tmp.get("recordTotal").asLong(),
      results.toList
    )
  }
}

object ModelConvertor {

  implicit def getModelName[E](model: E) = new {
    def _name =
      model.getClass.getSimpleName.substring(0, model.getClass.getSimpleName.length - 1)
  }
}
