scalaVersion := "2.11.8"

scalacOptions ++= Seq("-Xexperimental")

mainClass in (Compile, run) := Option("general.Main")

javaSource in Compile := baseDirectory.value / "src"

scalaSource in Compile := baseDirectory.value / "src"

javaSource in Test := baseDirectory.value / "test" / "java"

scalaSource in Test := baseDirectory.value / "test" / "scala"

libraryDependencies  ++= Seq(
  // Last stable release
  "org.scalanlp" %% "breeze" % "0.13",

  // Native libraries are not included by default. add this if you want them (as of 0.7)
  // Native libraries greatly improve performance, but increase jar sizes.
  // It also packages various blas implementations, which have licenses that may or may not
  // be compatible with the Apache License. No GPL code, as best I know.
  "org.scalanlp" %% "breeze-natives" % "0.13",

  // The visualization library is distributed separately as well.
  // It depends on LGPL code
  "org.scalanlp" %% "breeze-viz" % "0.13"
)

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

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"





