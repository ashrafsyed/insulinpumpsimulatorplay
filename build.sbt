import play.sbt.PlayJava

name := "insulinpumpsimulatorplay"

version := "1.0.0-SNAPSHOT"

lazy val `insulinpumpsimulatorplay` = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers +=  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"
resolvers += "Farmdawg's PayPal MVN Repo" at "https://raw.github.com/farmdawgnation/paypal-java-mvn/master/releases"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq( javaJdbc , ehcache , javaWs, guice, filters,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.typesafe.sbt" % "sbt-interface" % "0.13.15",
  "com.google.guava" % "guava" % "19.0",
  "com.google.code.gson" % "gson" % "2.8.2",
  "commons-io" % "commons-io" % "2.5",
  "org.apache.commons" % "commons-email" % "1.5"


)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

