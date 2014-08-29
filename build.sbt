organization := "io.github.drexin"

name := "akka-persistence-mapdb"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.4"

parallelExecution in Test := false

libraryDependencies += "com.typesafe.akka" %% "akka-persistence-experimental" % "2.3.5" % "compile"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.5" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.0" % "test"

libraryDependencies += "org.mapdb" % "mapdb" % "1.0.6"

libraryDependencies += "commons-io" % "commons-io" % "2.4" % "test"
