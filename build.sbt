scalaVersion := "2.11.8"

scalacOptions ++= Seq("-Xexperimental")

mainClass in (Compile, run) := Option("general.Main")

javaSource in Compile := baseDirectory.value / "src"

scalaSource in Compile := baseDirectory.value / "src"

javaSource in Test := baseDirectory.value / "test" / "java"

scalaSource in Test := baseDirectory.value / "test" / "scala"

// General dependencies
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.7",
  "org.json4s" %% "json4s-ast" % "3.3.0",
  "org.json4s" %% "json4s-core" % "3.3.0",
  "org.json4s" %% "json4s-native" % "3.3.0"
)

// Testing frameworks
libraryDependencies ++= Seq(
  // Java general unittest framework
  "junit" % "junit" % "4.12",
  // Scala testing frameworks and utils
  "org.scalatest" %% "scalatest" % "2.2.6",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2"
)

// Don't know what is in your 'lib' folder, so
unmanagedJars := Seq.empty






