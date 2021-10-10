package org.codecraftlabs.kauai

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import org.codecraftlabs.kauai.auth.AuthResponse
import org.codecraftlabs.kauai.service.AWSLambdaEnvVars.{AuthorizationHeader, AuthorizationHeaderDefault, MethodArnHeader, MethodArnHeaderDefault}
import org.codecraftlabs.kauai.service.AuthDynamoDB.isAuthorized

import scala.jdk.CollectionConverters.MapHasAsScala
import scala.util.Properties

class Main extends RequestHandler[java.util.Map[String, Object], AuthResponse] {

  override def handleRequest(event: java.util.Map[String, Object], context: Context): AuthResponse = {
    println("Starting authentication process")
    println(event.asScala)
    val headers = event.asScala("headers")
    val methodArn = event.asScala(Properties.envOrElse(MethodArnHeader, MethodArnHeaderDefault)).toString

    headers match {
      case x: java.util.Map[String, Object] => authenticate(x, methodArn)
      case _                                => invalidate
    }
  }

  private def authenticate(headers: java.util.Map[String, Object], methodArn: String): AuthResponse = {
    val authenticationHeader = headers.asScala(Properties.envOrElse(AuthorizationHeader, AuthorizationHeaderDefault)).toString
    val authResult = isAuthorized(authenticationHeader)
    if(authResult._2) {
      println("Authentication OK for token: " + authenticationHeader)
      new AuthResponse().setEffect("allow").setPrincipalId(authResult._1).setResource(methodArn)
    } else {
      println("Authentication failed for token: " + authenticationHeader)
      new AuthResponse().setEffect("deny").setPrincipalId(authResult._1).setResource(methodArn)
    }
  }

  private def invalidate = new AuthResponse().setEffect("deny").setPrincipalId("invalid").setResource("invalid")
}
