organization := "io.github.drexin"

name := "akka-persistence-mapdb"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.3"

parallelExecution in Test := false

libraryDependencies += "com.typesafe.akka" %% "akka-persistence-experimental" % "2.3.0-RC1" % "compile"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.0-RC1" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.0" % "test"

libraryDependencies += "org.mapdb" % "mapdb" % "0.9.8"

libraryDependencies += "commons-io" % "commons-io" % "2.4" % "test"