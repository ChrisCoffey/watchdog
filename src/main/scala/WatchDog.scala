package scala.watchdog

import org.joda.time.{Duration, DateTime}
import scala.watchdog.Configuration

class WatchDog(val records: Map[String, List[EventRecord]])
  extends IWatchDog {

  //Note maybe need to clean this up to return an either
  final def allRecordsWhere(predicate: (EventRecord) => Boolean) = {
    fullSet().filter(predicate)
  }

  final def recordsFor(description: String) = {
    records.getOrElse(description, Nil)
  }

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

  final def recordCall[B](call:() => B, description: String): ResultDelta[B] = {
    val start = DateTime.now()
    val res = call()
    val record = EventRecord(start, description, DateTime.now().getMillis - start.getMillis)

    val f = (w: WatchDog) => {
      val updatedRecords = w.records + ((description, record :: w.records.getOrElse(description, Nil)))
      new WatchDog(updatedRecords)
    }

    ResultDelta[B](res, f)
  }

  final def ||>[B](call:() => B, description: String)(implicit f: ResultDelta[B] => B): B = {
    f(recordCall(call, description))
  }
}

object WatchDog{
  def purgeDescriptor(watchDog: WatchDog, description: String) = {
    new WatchDog(watchDog.records - description)
  }

  def merge(l: WatchDog, r: WatchDog) = {
    val partialMerge = l.records.toSeq ++ r.records.toSeq
    val grouped = partialMerge.groupBy(_._1)
    val merged = grouped.mapValues(_.map(_._2).toList).mapValues(_.foldLeft(List[EventRecord]())((a, b) => a union b))

    new WatchDog(merged)
  }
}

trait IWatchDog{

  def recordCall[B](call: () => B, description: String): ResultDelta[B]

  val totalCalls: Int
  def totalCallsFor(description: String) : Either[String,  Int]

  val fullSet:() => List[EventRecord]
  def allRecordsWhere(predicate: EventRecord => Boolean): List[EventRecord]
  def recordsFor(description: String) : List[EventRecord]
}


