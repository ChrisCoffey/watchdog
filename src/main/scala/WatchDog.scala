package scala.watchdog

import org.joda.time.{Duration, DateTime}
import scala.watchdog.Configuration

//Needs to return data very quickly, but the actual handling of updates can be eventually consistent.
//No need to have an up to the millisecond picture of the data
//todo implement purging logic
//todo this is no longer a functional api either!! it only kind of is...

class WatchDog(val records: Map[String, List[EventRecord]])(val configuration: Configuration)
  extends IWatchDog {

  //Note maybe need to clean this up to return an either
  final def allRecordsWhere(predicate: (EventRecord) => Boolean) = {
    fullSet().filter(predicate)
  }

  final def recordsFor(description: String) = records.getOrElse(description, Nil)

  final val fullSet: () => List[EventRecord] = () => {
    records.values.foldLeft(List[EventRecord]())((s, lst) => lst ::: s)
  }

  final def totalCallsFor(description: String): Either[String, Int] = {
    records.get(description) match{
      case Some(lst) => Right(lst.length)
      case None => Left("Cannot find key for %s".format(description))
    }
  }

  final val totalCalls: Int = {
    records.values.foldLeft(0)((l, r) => l + r.length )
  }

  final def recordCall[B](call:() => B, description: String): Result[B] = {
    val start = DateTime.now()
    val res = call()
    val record = EventRecord(start, description, DateTime.now().getMillis - start.getMillis)
    val records2 = records + ((description, record :: records.getOrElse(description, Nil)))

    Result[B](res, new WatchDog(records2)(configuration))
  }
}

object WatchDog{
  implicit val config = Configuration(Duration.standardSeconds(10), 10000, true, true)
}


trait IWatchDog{

  def recordCall[B](call: () => B, description: String): Result[B]

  val totalCalls: Int
  def totalCallsFor(description: String) : Either[String,  Int]

  val fullSet:() => List[EventRecord]
  def allRecordsWhere(predicate: EventRecord => Boolean): List[EventRecord]
  def recordsFor(description: String) : List[EventRecord]
}


