package scala.watchdog

import org.joda.time.DateTime

case class EventRecord(time: DateTime, id: String, millisecondDuration: Long)

case class ResultDelta[A](value: A, f: WatchDog => WatchDog)

case class Result[A](value: A, dog: WatchDog)
