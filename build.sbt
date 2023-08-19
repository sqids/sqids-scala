lazy val scala213 = "2.13.11"
lazy val scala3 = "3.3.0"
lazy val supportedScalaVersions = List(scala213, scala3)

ThisBuild / organization := "sqids"
ThisBuild / scalaVersion := scala213

lazy val sqids = (project in file("."))
  .settings(
    name := "sqids-scala",
    version := "0.1.1-SNAPSHOT",
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test
    ),
    scalacOptions ++= {
      val commonScalacOptions =
        Seq(
          "-deprecation",
          "-encoding",
          "UTF-8",
          "-feature",
          "-unchecked",
          "-Xfatal-warnings"
        )

      val scala2ScalacOptions =
        if (scalaVersion.value.startsWith("2."))
          Seq(
            "-language:higherKinds",
            "-Xlint",
            "-Ywarn-dead-code",
            "-Ywarn-numeric-widen",
            "-Ywarn-value-discard",
            "-Ywarn-unused"
          )
        else Seq()

      val scala3ScalacOptions =
        if (scalaVersion.value.startsWith("3"))
          Seq("-Ykind-projector", "-Yretain-trees", "-Wunused:all")
        else Seq()

      commonScalacOptions ++
        scala2ScalacOptions ++
        scala3ScalacOptions
    },
    crossScalaVersions := supportedScalaVersions
  )
