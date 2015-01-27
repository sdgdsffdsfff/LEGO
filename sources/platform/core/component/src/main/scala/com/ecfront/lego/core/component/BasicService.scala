package com.ecfront.lego.core.component

import java.lang.reflect.ParameterizedType

import com.ecfront.lego.core.component.protocol.RequestProtocol
import com.ecfront.lego.core.foundation.PageModel
import com.typesafe.scalalogging.slf4j.LazyLogging
import collection.JavaConversions._

trait BasicService[M <: AnyRef] extends LazyLogging {

  protected val modelClazz = this.getClass.getGenericInterfaces()(0).asInstanceOf[ParameterizedType].getActualTypeArguments()(0).asInstanceOf[Class[M]]

  logger.info( """Create Service: model: %s""".format(modelClazz.getSimpleName))

  protected def preGetById(id: String, request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(null)
  }

  protected def postGetById(result: M, preResult: Any, request: RequestProtocol, success: => M => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(result)
  }

  def getById(id: String, request: RequestProtocol, success: => M => Unit, fail: => (String, String) => Unit = null): Unit = {
    preGetById(id, request, {
      preResult =>
        doGetById(id, request, {
          result =>
            postGetById(result, preResult, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doGetById(id: String, request: RequestProtocol, success: => M => Unit, fail: => (String, String) => Unit = null): Unit


  protected def preGetByCondition(condition: String, request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(null)
  }

  protected def postGetByCondition(result: M, preResult: Any, request: RequestProtocol, success: => M => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(result)
  }

  def getByCondition(condition: String, request: RequestProtocol, success: => M => Unit, fail: => (String, String) => Unit = null): Unit = {
    preGetByCondition(condition, request, {
      preResult =>
        doGetByCondition(condition, request, {
          result =>
            postGetByCondition(result, preResult, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doGetByCondition(condition: String, request: RequestProtocol, success: => M => Unit, fail: => (String, String) => Unit = null): Unit


  protected def preFindAll(request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(null)
  }

  protected def postFindAll(result: List[M], preResult: Any, request: RequestProtocol, success: => List[M] => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(result)
  }

  def findAll(request: RequestProtocol, success: => List[M] => Unit, fail: => (String, String) => Unit = null): Unit = {
    preFindAll(request, {
      preResult =>
        doFindAll(request, {
          result =>
            postFindAll(result, preResult, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doFindAll(request: RequestProtocol, success: => List[M] => Unit, fail: => (String, String) => Unit = null): Unit

  protected def preFindAll(pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(null)
  }

  protected def postFindAll(result: PageModel[M], preResult: Any, pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => PageModel[M] => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(result)
  }

  def findAll(pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => PageModel[M] => Unit, fail: => (String, String) => Unit = null): Unit = {
    preFindAll(pageNumber, pageSize, request, {
      preResult =>
        doFindAll(pageNumber, pageSize, request, {
          result =>
            postFindAll(result, preResult, pageNumber, pageSize, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doFindAll(pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => PageModel[M] => Unit, fail: => (String, String) => Unit = null): Unit


  protected def preFindByCondition(condition: String, request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(null)
  }

  protected def postFindByCondition(result: List[M], preResult: Any, request: RequestProtocol, success: => List[M] => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(result)
  }

  def findByCondition(condition: String, request: RequestProtocol, success: => List[M] => Unit, fail: => (String, String) => Unit = null): Unit = {
    preFindByCondition(condition, request, {
      preResult =>
        doFindByCondition(condition, request, {
          result =>
            postFindByCondition(result, preResult, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doFindByCondition(condition: String, request: RequestProtocol, success: => List[M] => Unit, fail: => (String, String) => Unit = null): Unit

  protected def preFindByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(null)
  }

  protected def postFindByCondition(result: PageModel[M], preResult: Any, pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => PageModel[M] => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(result)
  }

  def findByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => PageModel[M] => Unit, fail: => (String, String) => Unit = null): Unit = {
    preFindByCondition(condition, pageNumber, pageSize, request, {
      preResult =>
        doFindByCondition(condition, pageNumber, pageSize, request, {
          result =>
            postFindByCondition(result, preResult, pageNumber, pageSize, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doFindByCondition(condition: String, pageNumber: Long, pageSize: Long, request: RequestProtocol, success: => PageModel[M] => Unit, fail: => (String, String) => Unit = null): Unit


  protected def preSave(model: M, request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(null)
  }

  protected def postSave(result: String, preResult: Any, request: RequestProtocol, success: => String => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(result)
  }

  def save(model: M, request: RequestProtocol, success: => String => Unit, fail: => (String, String) => Unit = null): Unit = {
    preSave(model, request, {
      preResult =>
        doSave(model, request, {
          result =>
            postSave(result, preResult, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doSave(model: M, request: RequestProtocol, success: => String => Unit, fail: => (String, String) => Unit = null): Unit

  protected def preUpdate(id: String, model: M, request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(null)
  }

  protected def postUpdate(result: String, preResult: Any, request: RequestProtocol, success: => String => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(result)
  }

  def update(id: String, model: M, request: RequestProtocol, success: => String => Unit, fail: => (String, String) => Unit = null): Unit = {
    preUpdate(id, model, request, {
      preResult =>
        doUpdate(id, model, request, {
          result =>
            postUpdate(result, preResult, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doUpdate(id: String, model: M, request: RequestProtocol, success: => String => Unit, fail: => (String, String) => Unit = null): Unit

  protected def preDeleteById(id: String, request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(null)
  }

  protected def postDeleteById(preResult: Any, request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit = {
    success()
  }

  def deleteById(id: String, request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit = {
    preDeleteById(id, request, {
      preResult =>
        doDeleteById(id, request, {
          result =>
            postDeleteById(preResult, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doDeleteById(id: String, request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit

  protected def preDeleteByCondition(condition: String, request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(null)
  }

  protected def postDeleteByCondition(preResult: Any, request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit = {
    success()
  }

  def deleteByCondition(condition: String, request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit = {
    preDeleteByCondition(condition, request, {
      preResult =>
        doDeleteByCondition(condition, request, {
          result =>
            postDeleteByCondition(preResult, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doDeleteByCondition(condition: String, request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit


  protected def preDeleteAll(request: RequestProtocol, success: => Any => Unit, fail: => (String, String) => Unit = null): Unit = {
    success(null)
  }

  protected def postDeleteAll(preResult: Any, request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit = {
    success()
  }

  def deleteAll(request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit = {
    preDeleteAll(request, {
      preResult =>
        doDeleteAll(request, {
          result =>
            postDeleteAll(preResult, request, success, fail)
        }, fail)
    }, {
      (code, message) =>
        fail(code, message)
    })
  }

  protected def doDeleteAll(request: RequestProtocol, success: => Unit => Unit, fail: => (String, String) => Unit = null): Unit

}



