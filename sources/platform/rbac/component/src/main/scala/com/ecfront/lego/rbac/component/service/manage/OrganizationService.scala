package com.ecfront.lego.rbac.component.service.manage

import com.ecfront.lego.core.component.protocol.{RequestProtocol, ResponseDTO}
import com.ecfront.lego.core.component.storage.JDBCService
import com.ecfront.lego.core.foundation.IdModel
import com.ecfront.lego.core.foundation.ModelConvertor._
import com.ecfront.lego.rbac.foundation.{Account, Organization}

import scala.concurrent.Future


object OrganizationService extends JDBCService[Organization] with ManageService {

  def findOrganizationByAccountId(accountId: String, request: RequestProtocol): Future[ResponseDTO[List[Organization]]] = {
    findByCondition(
      s"${IdModel.ID_FLAG} in" +
        s" (SELECT ${Organization._name + "_" + IdModel.ID_FLAG} FROM $REL_ORGANIZATION_ACCOUNT " +
        s"WHERE ${Account._name + "_" + IdModel.ID_FLAG} = '$accountId')" +
        s" ${appendAuth(request)}",
      request)
  }

}
