package scala.watchdog

import org.joda.time.Duration

case class Configuration(fullDatasetKeepAlive: Duration, bufferSize: Int, flushOnPull: Boolean, preserveCountsOnFlush: Boolean)

