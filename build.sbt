import xerial.sbt.Sonatype.sonatypeCentralHost
import xerial.sbt.Sonatype.sonatype01


val projectVersion = "1.0.0"
val projectScalaVersion = "2.13.10"

scalaVersion := "2.13.10"

val scalaVersions = Seq("2.13.10")

val CompileOnly = config("compileonly").hide

ivyConfigurations += CompileOnly

Compile / unmanagedClasspath  ++= update.value.select(configurationFilter("compileonly"))


def testLibraries = Seq(
  "org.specs2" %% "specs2-core" % "4.6.0" % Test,
  "org.specs2" %% "specs2-mock" % "4.6.0" % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.6.14" % Test
)

val redisClientVersion = "2.4.3"

def redisLibrary = Seq(
  "com.github.scredis" %% "scredis" % redisClientVersion
)

val circeVersion = "0.12.3"

val circeLibraries = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

lazy val coreProtocol = (project in file("."))
  .settings(
    scalaVersion := projectScalaVersion,
    crossScalaVersions := scalaVersions,
    scalacOptions ++= Seq("-deprecation","-feature","-language:implicitConversions"),

    name := "scredis-json",
    organization := "com.sopranoworks",
    version := projectVersion,
    versionScheme := Some("semver-spec"),

    libraryDependencies ++= (
      redisLibrary ++
      circeLibraries ++
      testLibraries),

    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    sonatypeRepository := "https://oss.sonatype.org/service/local",

    Test / publishArtifact := false,
    pomIncludeRepository := { _ => false },
    sonatypeProfileName := "com.sopranoworks",
    pomExtra :=
      <url>https://github.com/sopranoworks/scredis-json</url>
        <licenses>
          <license>
            <name>MIT</name>
            <url>https://opensource.org/licenses/MIT</url>
          </license>
        </licenses>
        <scm>
          <url>https://github.com/sopranoworks/scredis-json</url>
          <connection>https://github.com/sopranoworks/scredis-json.git</connection>
        </scm>
        <developers>
          <developer>
            <id>OsamuTakahashi</id>
            <name>Osamu Takahashi</name>
            <url>https://github.com/OsamuTakahashi/</url>
          </developer>
        </developers>
  )

