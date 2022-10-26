ThisBuild / version      := "1.0"
ThisBuild / scalaVersion := "2.13.8"
ThisBuild / organization := "org.example"

val spinalVersion = "1.7.3"
val spinalCore = "com.github.spinalhdl" %% "spinalhdl-core" % spinalVersion
val spinalLib = "com.github.spinalhdl" %% "spinalhdl-lib" % spinalVersion
val spinalIdslPlugin = compilerPlugin("com.github.spinalhdl" %% "spinalhdl-idsl-plugin" % spinalVersion)

lazy val BUF2PE = (project in file("."))
  .settings(
    name := "BUF2PE",
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.2.13",
      "org.scalatest" %% "scalatest" % "3.2.13" % "test",
      spinalCore,
      spinalLib,
      spinalIdslPlugin
    )
  )

fork := true