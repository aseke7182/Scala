name := "Scala"

version := "0.1"

scalaVersion := "2.12.7"

lazy val akkaVersion = "2.5.25"
lazy val elastic4sVersion = "6.2.10"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-slf4j"   % akkaVersion,
  "ch.qos.logback"    % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-http"   % "10.1.10",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.10",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-json4s" % elastic4sVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,

  // AWS SDK
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.670",
  "com.github.pathikrit" %% "better-files" % "3.8.0",

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-slf4j"   % akkaVersion,
  "ch.qos.logback"    % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-http"   % "10.1.10",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.10",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  // AWS SDK
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.670",
  "org.scalatest" %% "scalatest" % "3.0.8" % Test
)