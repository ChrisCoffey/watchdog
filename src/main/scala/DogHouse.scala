package scala.watchdog

import java.util.concurrent.Executors
import com.lmax.disruptor._
import com.lmax.disruptor.dsl.{ProducerType, Disruptor}


trait DogHouse {

  var watchdog = new WatchDog(Map[String, List[EventRecord]]())
  val configuration: Configuration

  private val executor = Executors.newFixedThreadPool(1)
  private val factory = new EventFactory[Mutation] {
    def newInstance() = Mutation((w: WatchDog) => w)
  }
  private val disruptor = new Disruptor[Mutation](factory, configuration.bufferSize, executor, ProducerType.MULTI, new SleepingWaitStrategy())
  disruptor.handleEventsWith(new DeltaEventHandler())
  disruptor.start()

  def record[A](call: () => A, tag: String): A = {
    val delta = watchdog.recordCall(call, tag)
    disruptor.publishEvent(DeltaEventTranslator(delta.f))
    delta.value
  }

  //Disruptor specific classes
  case class Mutation(var f: WatchDog => WatchDog)

  case class DeltaEventTranslator(f: WatchDog => WatchDog) extends EventTranslator[Mutation]{
    def translateTo(event: Mutation, sequence: Long) = {
      event.f = f
      event
    }
  }

  class DeltaEventHandler extends EventHandler[Mutation]{
    def onEvent(event: Mutation, sequence: Long, endOfBatch: Boolean){
      watchdog = event.f(watchdog)
    }
  }

}


