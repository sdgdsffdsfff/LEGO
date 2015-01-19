package com.ecfront.lego.core.component

import com.ecfront.lego.core.foundation.IdModel

trait CoreService[M <: IdModel] extends BasicService[M] {
  override protected val address: String = _
}

