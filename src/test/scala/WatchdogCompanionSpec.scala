package scala.watchdog.tests

import org.specs2.mutable.Specification
import scala.watchdog._

class WatchdogCompanionSpec extends Specification
  with WatchDogStubData {

  "The watchdog companion object " should {

    "Purge the records and key for provided description" in {
      val res = WatchDog.purgeDescriptor(populatedDog1, OtherName)
      res.fullSet() must beEmpty
    }

    "Purge leaves no records behind for provided description" in {
      val res = WatchDog.purgeDescriptor(populatedDog1, OtherName)
      res.fullSet().length must not beGreaterThan(0)
    }

    "Purge records only for the provided key" in {
      val res = WatchDog.purgeDescriptor(fullDog, OtherName)
      res.recordsFor(OtherName) must beEmpty
      res.recordsFor(TestName) must have size(2)
      res.fullSet() must have size(2)
    }

    "Purge leaves original watchdog unchanged" in{
      val res = WatchDog.purgeDescriptor(populatedDog1, OtherName)
      populatedDog1.recordsFor(OtherName) must have size(2)
    }


  }
}
