lazy val scala213 = "2.13.11"
lazy val scala3 = "3.3.0"
lazy val supportedScalaVersions = List(scala213, scala3)

ThisBuild / organization := "org.sqids"
ThisBuild / organizationName := "Sqids"
ThisBuild / startYear := Some(2023)
ThisBuild / licenses := Seq(License.MIT)
ThisBuild / developers ++= List(
  tlGitHubDev("jesperoman", "Jesper Öman")
)
ThisBuild / tlBaseVersion := "0.1"
ThisBuild / crossScalaVersions := supportedScalaVersions
ThisBuild / scalaVersion := scala213
ThisBuild / tlSonatypeUseLegacyHost := false

// Remove when first release is done
ThisBuild / mimaFailOnNoPrevious := false

lazy val sqids = project
  .in(file("."))
  .settings(
    name := "sqids",
    headerLicenseStyle := HeaderLicenseStyle.SpdxSyntax,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test
    )
  )

def addCommandsAlias(name: String, values: List[String]) =
  addCommandAlias(name, values.mkString(";", ";", ""))

addCommandsAlias(
  "validate",
  List(
    "+clean",
    "+test",
    "+mimaReportBinaryIssues",
    "scalafmtCheckAll",
    "scalafmtSbtCheck"
  )
)
