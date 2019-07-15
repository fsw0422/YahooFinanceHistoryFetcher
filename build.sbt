name := "YahooFinanceHistoryFetcher"

organization := "com.github.fsw0422"

version := "0.2.0"

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

homepage := Some(url("http://github.com/fsw0422"))

publishTo := Some(if (isSnapshot.value) {
  Opts.resolver.sonatypeSnapshots
} else {
  Opts.resolver.sonatypeStaging
})

publishMavenStyle := true

scmInfo := Some(
  ScmInfo(
    url("https://github.com/fsw0422/yahoo-finance-history-fetcher"),
    "scm:git@github.com:fsw0422/yahoo-finance-history-fetcher.git"
  )
)

developers := List(
  Developer(
    id = "fsw0422",
    name = "Kevin Kwon",
    email = "fsw0422@gmail.com",
    url = url("http://github.com/fsw0422")
  )
)

scalaVersion := "2.12.3"

crossScalaVersions := Seq("2.11.11")

lazy val slf4jVersion = "1.7.26"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "1.3.1",
  "com.typesafe.akka" %% "akka-http"   % "10.1.8",
  "com.typesafe.akka" %% "akka-stream" % "2.5.19",
  "org.scalatest" %% "scalatest" % "3.1.0-RC1" % Test,
  "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
)

publishArtifact in Test := false

lazy val YahooFinanceHistoryFetcher = project in file(".")
