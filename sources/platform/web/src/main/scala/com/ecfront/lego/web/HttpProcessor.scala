package com.ecfront.lego.web

import com.ecfront.common.JsonHelper
import com.ecfront.lego.core.component.protocol.ResponseDTO
import com.typesafe.scalalogging.slf4j.LazyLogging
import io.vertx.core._
import io.vertx.core.http.{HttpMethod, HttpServerResponse}
import io.vertx.ext.apex.core.{Router, RoutingContext}

object HttpProcessor extends LazyLogging {

  var router: Router = _
  var vertx: Vertx = _

  /**
   * 注册获取方法
   *
   * @param uri         地址
   * @param fun 处理方法
   */
  def get(uri: String, fun: => MultiMap => ResponseDTO[Any]) {
    logger.info("Add method [GET] url :" + uri)
    router.route(HttpMethod.GET, uri).handler(normalProcess(fun))
  }

  /**
   * 注册获取方法
   *
   * @param uri         地址
   * @param fun 处理方法
   */
  def delete(uri: String, fun: => MultiMap => ResponseDTO[Any]) {
    logger.info("Add method [DELETE] url :" + uri)
    router.route(HttpMethod.GET, uri).handler(normalProcess(fun))
  }

  private def normalProcess(fun: => (MultiMap) => ResponseDTO[Any]): Handler[RoutingContext] with Object {} = {
    new Handler[RoutingContext] {
      override def handle(event: RoutingContext): Unit = {
        vertx.executeBlocking(new Handler[Future[ResponseDTO[Any]]] {
          override def handle(future: Future[ResponseDTO[Any]]): Unit = {
            future.complete(fun(event.request().params()))
          }
        }, new Handler[AsyncResult[ResponseDTO[Any]]] {
          override def handle(result: AsyncResult[ResponseDTO[Any]]): Unit = {
            returnJson(result.result(), event.response())
          }
        })
      }
    }
  }

  private def returnJson(result: AnyRef, response: HttpServerResponse) {
    response.setStatusCode(200).putHeader("Content-Type", "text/json;charset=UTF-8")
      .putHeader("Cache-Control", "no-cache")
      .putHeader("Access-Control-Allow-Origin", "*")
      .putHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
      .putHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With, X-authentication, X-client")
      .end(JsonHelper.toJsonString(result))
  }
}
