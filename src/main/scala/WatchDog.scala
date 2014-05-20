import org.joda.time.DateTime

//Needs to return data very quickly, but the actual handling of updates can be eventually consistent.
//No need to have an up to the millisecond picture of the data
//todo implement purging logic
trait WatchDog extends WatchDogAPI{

  val configuration : Configuration
  type Watcher= WatchDogImpl
  private var watchdogService = new WatchDogImpl(Map[String, List[EventRecord]]())

  //concurrency issues abound!!!!
  def record[B](call: () => B, description: String) : B = {
    val (res, svc) = watchdogService.recordCall(call, description)
    watchdogService = svc
    res
  }

  class WatchDogImpl(val records: Map[String, List[EventRecord]] ) extends IWatchDog{

    //Note maybe need to clean this up to return an either
    override def allRecordsWhere(predicate: (EventRecord) => Boolean) = {
      fullSet().filter(predicate)
    }

    override def recordsFor(description: String) = records.getOrElse(description, Nil)

    override val fullSet: () => List[EventRecord] = () => {
      records.values.foldLeft(List[EventRecord]())((s, lst) => lst ::: s)
    }

    override def totalCallsFor(description: String): Either[String, Int] = {
      records.get(description) match{
        case Some(lst) => Right(lst.length)
        case None => Left("Cannot find key for %s".format(description))
      }
    }

    override val totalCalls: Int = {
      records.values.foldLeft(0)((l, r) => l + r.length )
    }

    override def recordCall[B](call:() => B, description: String) = {
      val start = DateTime.now()
      val res = call()
      val record = EventRecord(start, description, DateTime.now().getMillis - start.getMillis)
      val records2 = records + ((description, record :: records.getOrElse(description, Nil)))

      (res, new WatchDogImpl(records2))
    }
  }
}

trait WatchDogAPI{

  def watchdogService: IWatchDog

  trait IWatchDog{

    def recordCall[B](call: () => B, description: String): (B, IWatchDog)

    val totalCalls: Int
    def totalCallsFor(description: String) : Either[String,  Int]

    val fullSet:() => List[EventRecord]
    def allRecordsWhere(predicate: EventRecord => Boolean): List[EventRecord]
    def recordsFor(description: String) : List[EventRecord]
  }
}


