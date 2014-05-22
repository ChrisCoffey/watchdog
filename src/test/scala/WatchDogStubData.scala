package scala.watchdog.tests

import scala.watchdog.{ResultDelta, EventRecord, WatchDog}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

trait WatchDogStubData {

  val dog = new WatchDog(Map[String, List[EventRecord]]())
  val AlwaysTrue = () => true
  val TestName = "test"
  val OtherName = "platypus"
  implicit def extractSimple[A](r: ResultDelta[A]) = r.value

  val format = DateTimeFormat.forPattern("yyyy-MM-dd")
  val ts1 = format.parseDateTime("2014-05-20")
  val ts2 = format.parseDateTime("2014-05-21")
  val ts3 = format.parseDateTime("2014-05-22")

  val record1 = EventRecord(ts1, "call1", 10)
  val record2 = EventRecord(ts1, "call2", 15)
  val record3 = EventRecord(ts2, "call3", 5)
  val record4 = EventRecord(ts3, "call4", 100)

  val populatedDog1 = new WatchDog(dog.records + ((OtherName, record1 :: record2 :: Nil)))
  val populatedDog2 = new WatchDog(dog.records + ((TestName, record3 :: record4 :: Nil)))

  val fullDog = new WatchDog(populatedDog1.records ++ populatedDog2.records)

}
