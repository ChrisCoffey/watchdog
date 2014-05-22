package scala.watchdog.tests

import org.specs2.mutable._
import scala.watchdog._

class WatchdogSpec extends Specification
  with WatchDogStubData
{
  "The WatchDog" should{

    "return the proper result of a passed call" in {
      val res = dog.recordCall(AlwaysTrue, TestName)
      res.value must beEqualTo(true)
    }

    "increment call count by one when called once" in {
      val res = dog.recordCall(AlwaysTrue, TestName)
      res.f(dog).totalCalls must beEqualTo(1)
    }
    
    "increment call count in successive instances" in {
      val r1 = dog.recordCall(AlwaysTrue, TestName)
      val newDog = r1.f(dog)
      val r2 = newDog.recordCall(AlwaysTrue, TestName)
      r2.f(newDog).totalCalls must beEqualTo(2)
    }

    "not change the original watchdog" in {
      dog.recordCall(AlwaysTrue, TestName)
      dog.totalCalls must beEqualTo(0)
    }

    //note yes this is redundant, but still useful for edification
    "not change the original watchdog on multiple updates" in {
      dog.recordCall(AlwaysTrue, TestName)
      val res = dog.recordCall(AlwaysTrue, TestName)
      res.f(dog).totalCalls must beEqualTo(1)
    }

    "return a unique watchdog per call" in {
      val r1 = dog.recordCall(AlwaysTrue, TestName)
      val r2 = dog.recordCall(AlwaysTrue, TestName)
      r1.f(dog) must not beTheSameAs(r2.f(dog))
    }
    
    "record proper count for multiple calls on same id" in {
      val r1 = dog.recordCall(AlwaysTrue, TestName)
      val newDog = r1.f(dog)
      val r2 = newDog.recordCall(AlwaysTrue, TestName)
      val calls = r2.f(newDog).totalCallsFor(TestName) match{
        case Right(i) => i
        case _ => 0
      }
      calls must beEqualTo(2)
    }

    "not increment other descriptions on a call" in {
      val r1 = dog.recordCall(AlwaysTrue, TestName)
      val newDog = r1.f(dog)
      val r2 = newDog.recordCall(AlwaysTrue, OtherName)
      val calls = r2.f(newDog).totalCallsFor(TestName) match{
        case Right(i) => i
        case _ => 0
      }
      calls must beEqualTo(1)
    }

    "properly extract value using ||>" in {
      dog ||> (AlwaysTrue, TestName) must beEqualTo(true)
    }

    "return full list of calls during period" in {
      var d2 = dog
      implicit val extractUpdate = (r: ResultDelta[Boolean]) => {
        d2 = r.f(d2)
        r.value
      }

      d2 ||> (AlwaysTrue, TestName)
      d2 ||> (AlwaysTrue, TestName)
      d2 ||> (AlwaysTrue, TestName)
      d2.fullSet() must have size(3)
    }

    "return list of calls filtered by description" in {
      var d2 = dog
      implicit val extractUpdate = (r: ResultDelta[Boolean]) => {
        d2 = r.f(d2)
        r.value
      }

      d2 ||> (AlwaysTrue, TestName)
      d2 ||> (AlwaysTrue, TestName)
      d2 ||> (AlwaysTrue, OtherName)
      d2.recordsFor(OtherName) must have size(1)
    }

    //This is a gamble that the resolution on joda's milliseconds is coarse enough that a long interrupt won't fail the test
    "properly records duration" in {
      val res = dog.recordCall(() =>{ Thread.sleep(500); true; }, TestName)
      res.f(dog).recordsFor(TestName).head.millisecondDuration must beEqualTo(500)
    }

    "return list of calls filtered by description" in {
      var d2 = dog
      implicit val extractUpdate = (r: ResultDelta[Boolean]) => {
        d2 = r.f(d2)
        r.value
      }

      d2 ||> (AlwaysTrue, TestName)
      d2 ||> (AlwaysTrue, TestName)
      d2 ||> (AlwaysTrue, OtherName)
      d2.recordsFor(OtherName) must have size(1)
    }
    

  }


}


