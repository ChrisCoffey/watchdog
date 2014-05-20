package scala.watchdog

import org.joda.time.{Duration}
import org.specs2.mutable._
import org.specs2.specification.Scope

class WatchdogSpec extends Specification{

  "The WatchDog" should{
    val dog = new WatchDog(Map[String, List[EventRecord]]())(WatchDog.config)
    val AlwaysTrue = () => true
    val TestName = "test"
    val OtherName = "platypus"

    "return the proper result of a passed call" in {
      val res = dog.recordCall(AlwaysTrue, TestName)
      res.value must beEqualTo(true)
    }

    "increment call count by one when called once" in {
      val res = dog.recordCall(AlwaysTrue, TestName)
      res.dog.totalCalls must beEqualTo(1)
    }
    
    "increment call count in successive instances" in {
      val r1 = dog.recordCall(AlwaysTrue, TestName)
      val r2 = r1.dog.recordCall(AlwaysTrue, TestName)
      r2.dog.totalCalls must beEqualTo(2)
    }

    "not change the original watchdog" in {
      dog.recordCall(AlwaysTrue, TestName)
      dog.totalCalls must beEqualTo(0)
    }

    //note yes this is redundant, but still useful for edification
    "not change the original watchdog on multiple updates" in {
      dog.recordCall(AlwaysTrue, TestName)
      val res = dog.recordCall(AlwaysTrue, TestName)
      res.dog.totalCalls must beEqualTo(1)
    }

    "return a unique watchdog per call" in {
      val r1 = dog.recordCall(AlwaysTrue, TestName)
      val r2 = dog.recordCall(AlwaysTrue, TestName)
      r1.dog must not beTheSameAs(r2.dog)
    }
    
    "record proper count for multiple calls on same id" in {
      val r1 = dog.recordCall(AlwaysTrue, TestName)
      val r2 = r1.dog.recordCall(AlwaysTrue, TestName)
      val calls = r2.dog.totalCallsFor(TestName) match{
        case Right(i) => i
        case _ => 0
      }
      calls must beEqualTo(2)
    }

    "not increment other descriptions on a call" in {
      val r1 = dog.recordCall(AlwaysTrue, TestName)
      val r2 = r1.dog.recordCall(AlwaysTrue, OtherName)
      val calls = r2.dog.totalCallsFor(TestName) match{
        case Right(i) => i
        case _ => 0
      }
      calls must beEqualTo(1)
    }
    

  }


}


