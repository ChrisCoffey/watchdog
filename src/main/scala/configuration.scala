import org.joda.time.Duration

case class Configuration(fullDatasetKeepAlive: Duration, maxRecords: Int, flushOnPull: Boolean, preserveCountsOnFlush: Boolean)

