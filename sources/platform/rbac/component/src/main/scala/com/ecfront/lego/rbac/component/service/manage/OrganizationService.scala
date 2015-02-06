package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.component.storage.VertxStorageService
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.core.foundation.ModelConvertor._
import com.ecfront.lego.rbac.foundation.{Account, Organization}


object OrganizationService extends VertxStorageService[Organization] with ManageService {

  def findOrganizationByAccountId(accountId: String, request: RequestProtocol, success: => List[Organization] => Unit, fail: => (String, String) => Unit): Unit = {
    findByCondition(
      s"${IdModel.ID_FLAG} in" +
        s" (SELECT ${Organization._name + "_" + IdModel.ID_FLAG} FROM $REL_ORGANIZATION_ACCOUNT " +
        s"WHERE ${Account._name + "_" + IdModel.ID_FLAG} = '$accountId')" +
        s" ${appendAuth(request)}",
      request, success, fail)
  }

}
