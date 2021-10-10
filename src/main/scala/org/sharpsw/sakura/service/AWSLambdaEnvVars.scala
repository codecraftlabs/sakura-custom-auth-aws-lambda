package org.sharpsw.sakura.service

object AWSLambdaEnvVars {
  val AuthenticationDynamoDBTable = "AUTH_TABLE_NAME"
  val AuthenticationDynamoDBTableDefault = "InternalAuthentication"

  val AuthorizationHeader = "AUTHORIZATION_HEADER_NAME"
  val AuthorizationHeaderDefault = "x-secret-key"

  val MethodArnHeader = "METHOD_ARN"
  val MethodArnHeaderDefault = "methodArn"
}
