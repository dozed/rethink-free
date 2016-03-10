name := "rethink-free"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "RethinkScala Repository" at "http://kclay.github.io/releases"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.7",
  "org.scalaz.stream" %% "scalaz-stream" % "0.7.3",
  "com.rethinkscala" %% "core" % "0.4.9"
)

// https://github.com/non/kind-projector
// makes writing type signatures easier
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
