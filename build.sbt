name := "support"
scalaVersion := "3.9.0"

libraryDependencies += "com.anthropic" % "anthropic-java" % "3.6.0"
libraryDependencies += "com.evalship" % "tinyeval" % "0.11.0"

// sbt stage writes target/stage/bin/support, the eval worker's start script.
enablePlugins(JavaAppPackaging)
Universal / UniversalPlugin.autoImport.stagingDirectory := baseDirectory.value / "target" / "stage"
Compile / packageDoc / publishArtifact := false // keeps sbt stage from building the Scaladoc
