package org.codecraftlabs.kauai

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import org.codecraftlabs.kauai.auth.AuthResponse
import org.codecraftlabs.kauai.service.AWSLambdaEnvVars.{AuthorizationHeader, AuthorizationHeaderDefault, MethodArnHeader, MethodArnHeaderDefault, PrincipalIdDefault, PrincipalIdHeader}
import org.codecraftlabs.kauai.service.AuthDynamoDB.isAuthorized

import scala.jdk.CollectionConverters.MapHasAsScala
import scala.util.Properties.envOrElse

class Main extends RequestHandler[java.util.Map[String, Object], AuthResponse] {
  private val Headers: String = "headers"
  private val Deny: String = "deny"
  private val Allow: String = "allow"
  private val Invalid: String = "invalid"

  override def handleRequest(event: java.util.Map[String, Object], context: Context): AuthResponse = {
    println("Starting authentication process")
    println(event.asScala)
    val headers = event.asScala(Headers)
    val methodArn = event.asScala(envOrElse(MethodArnHeader, MethodArnHeaderDefault)).toString

    headers match {
      case headerItems: java.util.Map[String, Object] => authenticate(headerItems, methodArn)
      case _                                => invalidate
    }
  }

  private def authenticate(headers: java.util.Map[String, Object], methodArn: String): AuthResponse = {
    if (!headers.containsKey(envOrElse(PrincipalIdHeader, PrincipalIdDefault))) {
      println("Authentication failed. Missing principalId")
      new AuthResponse().setEffect(Deny).setPrincipalId("None").setResource(methodArn)
    } else if (!headers.containsKey(envOrElse(AuthorizationHeader, AuthorizationHeaderDefault))) {
      println("Authentication failed. Missing secret key")
      new AuthResponse().setEffect(Deny).setPrincipalId("None").setResource(methodArn)
    } else {
      val principalId = headers.asScala(envOrElse(PrincipalIdHeader, PrincipalIdDefault)).toString
      val authenticationHeader = headers.asScala(envOrElse(AuthorizationHeader, AuthorizationHeaderDefault)).toString
      val authResult = isAuthorized(authenticationHeader)

      if(authResult._1.equals(principalId) && authResult._2) {
        println("Authentication OK for token: " + authenticationHeader)
        new AuthResponse().setEffect(Allow).setPrincipalId(authResult._1).setResource(methodArn)
      } else {
        println("Authentication failed for token: " + authenticationHeader)
        new AuthResponse().setEffect(Deny).setPrincipalId(authResult._1).setResource(methodArn)
      }
    }
  }

  private def invalidate = new AuthResponse()
                                .setEffect(Deny)
                                .setPrincipalId(Invalid)
                                .setResource(Invalid)
}
