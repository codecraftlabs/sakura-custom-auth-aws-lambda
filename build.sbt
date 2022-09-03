import Dependencies._

val appVersion = "1.0.0"

val appName = "sakura-custom-auth-aws-lambda"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "org.codecraftlabs",
      scalaVersion := "2.13.1",
      version      := appVersion
    )),
    name := appName,
    assembly / assemblyJarName := appName + "-" + appVersion + ".jar",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.2.1",
    libraryDependencies += "com.amazonaws" % "aws-lambda-java-events" % "3.11.0",
    libraryDependencies += "com.amazonaws" % "aws-java-sdk-core" % "1.12.290",
    libraryDependencies += "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.12.290",
    libraryDependencies += "org.json4s" %% "json4s-native" % "4.0.5",
    libraryDependencies += "org.json4s" %% "json4s-jackson" % "4.0.5",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.11",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
  )

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}