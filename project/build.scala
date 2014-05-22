import sbt._
import Keys._
import sbtbuildinfo.Plugin._
import com.github.retronym.SbtOneJar

object WatchDogBuild extends Build{

	val buildSettings = Defaults.defaultSettings ++ Seq(
			organization := "Chris Coffey",
      name := "watchdog",
      version := "0.0.0.1",
			scalaVersion := "2.11.0",
      exportJars := true,
			scalacOptions ++= Seq("-unchecked", "-deprecation", "utf8"),
      resolvers ++= Seq(Resolvers.sonatype, Resolvers.typesafe),
      libraryDependencies ++= Seq(
        Dependencies.jodaTime,
        Dependencies.specs2,
        Dependencies.lmaxDisruptor
      )
		)

  val pluginSettings = Seq(
    sourceGenerators in Compile <+= buildInfo,
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "watchdog"
  )

  lazy val root = Project(
    id = "watchdog",
    base= file("."),
    settings = Project.defaultSettings ++ buildSettings ++ SbtOneJar.oneJarSettings
  )

/*  lazy val examples = Project(
    id="watchdog-examples",
    base=file("examples"),
    settings = Project.defaultSettings ++ buildSettings
  )*/
}

object Dependencies{
  val jodaTime = "joda-time" % "joda-time" % "2.3"
  val specs2 = "org.specs2" %% "specs2" % "2.3.12" % "test"
  val lmaxDisruptor = "com.lmax" % "disruptor" % "3.0.1"
}

object Resolvers{
  val sonatype =  "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/releases"
  val typesafe = "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases/"
}