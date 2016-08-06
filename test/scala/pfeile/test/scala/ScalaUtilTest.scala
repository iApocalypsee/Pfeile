package pfeile.test.scala

import java.util.concurrent.TimeoutException

import akka.actor._
import ActorDSL._
import general.ScalaUtil
import org.scalatest.BeforeAndAfterAll
import scala.concurrent.duration._

/**
  * Class being tested is [[general.ScalaUtil]].
  * Not every method will be tested, this test focuses rather on the actor part for now.
  *
  * Tests related to `ScalaUtil.awaitReply` in this class should not use variants without the
  * [[akka.actor.Inbox]] object as parameter; calling these variants results in loading up the [[general.Main]]
  * class (which this test does not intend to do).
  */
class ScalaUtilTest extends CommonTestSuite with BeforeAndAfterAll {

  // Mimick seperate actor system, referencing the actor system from the game
  // will result in failed tests.
  implicit val actorSystem = ActorSystem("testsys")

  override def afterAll() = {
    actorSystem.terminate()
  }

  private def newLocalInbox = Inbox.create(actorSystem)

  "A message sent to an actor" should "be received, replied to and cast correctly with ScalaUtil.awaitReply" in {

    val respondActor = actor(new Act {
      become {
        case "Ping" => sender ! "Pong"
        case other  => throw new IllegalArgumentException(other.toString)
      }
    })

    val inbox = newLocalInbox

    val typedResponse = ScalaUtil.awaitReply[String](inbox, "Ping", respondActor, 1.second)
    assert(typedResponse == "Pong")

    // If ClassCastException not thrown, fail the test.
    // This piece should fail because of the 'Int' type parameter. It does not match the type of "Pong" string value
    // returned from the 'respondActor' actor.
    intercept[ClassCastException] {
      ScalaUtil.awaitReply[Int](inbox, "Ping", respondActor, 1.second)
    }

  }

  "A message to which the recipient has been configured not to respond" should "throw a timeout exception when result is queried" in {

    val respondActor = actor(new Act {
      become {
        case "Keep waiting" =>
          // Returns nothing to the sender via
          // 'sender ! <response>' or
          // 'sender.tell(<response>, self)'
        case other => throw new IllegalArgumentException(other.toString)
      }
    })

    intercept[TimeoutException] {
      ScalaUtil.awaitReply[Any](newLocalInbox, "Keep waiting", respondActor, 1.second)
    }

  }

}
