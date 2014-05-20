import sbt._
import Keys._
import sbt.IO._

object WatchDogBuild extends Build{

	val buildSettings = Defaults.defaultSettings ++ Seq(
			organization := "Chris Coffey",
			scalaVersion := "2.11.0",
			scalacOptions ++= Seq("-unchecked", "-deprecation")
		)
}