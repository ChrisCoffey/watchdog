import sbt._
import Keys._
import sbtbuildinfo.Plugin._
import com.github.retronym.SbtOneJar

object WatchDogBuild extends Build{

	val buildSettings = Defaults.defaultSettings ++ Seq(
			organization := "org.scala.watchdog",
      name := "watchdog",
      version := "0.0.0.1",
			scalaVersion := "2.11.0",
      exportJars := true,
			scalacOptions ++= Seq("-unchecked", "-deprecation", "utf8"),
      resolvers ++= Seq(Resolvers.sonatype, Resolvers.typesafe),
      libraryDependencies ++= Dependencies.coreDependencies
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

  lazy val examples = Project(
    id="watchdog-examples",
    base=file("examples"),
    settings = Project.defaultSettings ++ buildSettings ++ Seq(
      libraryDependencies ++= Dependencies.exampleDependencies,
      resolvers ++= Seq(Resolvers.spray)
    )
  )
}

object Dependencies{

  val akkaV = "2.2-M4"
  val sprayV = "1.1.1"

  val jodaTime = "joda-time" % "joda-time" % "2.3"
  val specs2 = "org.specs2" %% "specs2" % "2.3.12" % "test"
  val lmaxDisruptor = "com.lmax" % "disruptor" % "3.0.1"
  val akka = "com.typesafe.akka" % "akka-actor_2.11.0-M3" % akkaV
  val sprayCan =  "io.spray" % "spray-can" % sprayV
  val sprayRouting = "io.spray" % "spray-routing" % sprayV


  val coreDependencies = Seq(
   jodaTime,
   specs2,
   lmaxDisruptor
  )

  val exampleDependencies = Seq(
    akka,
    sprayCan,
    sprayRouting
  )
}

object Resolvers{
  val sonatype =  "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/releases"
  val typesafe = "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases/"
  val spray = "spray" at "http://repo.spray.io/"
}