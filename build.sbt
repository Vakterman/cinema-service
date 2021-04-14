name := "LunatechTestService"

version := "0.1"

scalaVersion := "2.13.5"
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"

libraryDependencies ++=
  Seq("org.postgresql" % "postgresql" % "42.2.19",
    "org.scalactic" %% "scalactic" % "3.2.7",
    "org.scalatest" %% "scalatest" % "3.2.7" % "test",
    "com.typesafe" % "config" % "1.4.1",
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
    "org.slf4j" % "slf4j-simple" % "1.7.30" % Test,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",
    "org.slf4j" % "slf4j-api" % "1.7.30",
    "ch.qos.logback" % "logback-classic" % "1.2.3" % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion)