resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.3")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.11.0")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.2.1")
