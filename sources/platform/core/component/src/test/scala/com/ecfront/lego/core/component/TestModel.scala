package com.ecfront.lego.core.component

import com.ecfront.lego.core.foundation.SecureModel
import com.ecfront.storage.Entity

import scala.beans.BeanProperty

@Entity(idField = "id")
case class TestModel() extends SecureModel {
  @BeanProperty var name: String = _
  @BeanProperty var bool: Boolean = _
  @BeanProperty
  @com.ecfront.common.Ignore var age: Int = _
}
