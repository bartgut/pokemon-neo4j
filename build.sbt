name := "Neo4jPokemon"

version := "0.1"

scalaVersion := "3.1.3"

libraryDependencies ++= Seq(
  "com.softwaremill.sttp.client3" %% "core" % "3.7.1",
  "com.softwaremill.sttp.client3" %% "circe" % "3.7.1",
  "io.circe" %% "circe-generic" % "0.14.2",
  "org.typelevel" %% "cats-core" % "2.7.0",
  "org.typelevel" %% "cats-effect" % "3.3.12",
  "co.fs2" %% "fs2-core" % "3.2.10",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "org.neo4j.driver" % "neo4j-java-driver" % "4.4.6",
)

