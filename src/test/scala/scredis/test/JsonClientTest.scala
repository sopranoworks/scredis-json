package scredis.test

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.specs2.mutable.{After, Specification}
import org.specs2.specification.{AfterEach, BeforeEach}
import scredis.JsonClient

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class JsonClientTest extends Specification with BeforeEach with AfterEach {
  sequential

  import scredis.serialization.JsonImplicits._

  override protected def before: Any = {
  }

  override protected def after: Any = {
  }

  private val _5s = Duration("5s")

  abstract class AkkaTestkitSpecs2Support extends TestKit(ActorSystem()) with After with ImplicitSender {
    def after: Any = Await.result(system.terminate(), _5s)
  }

  case class JsonRecord(name:String, score:Int, flag:Boolean, optValue:Option[Int])

  "JSON.SET then JSON.GET" should {
    "normally success" in new AkkaTestkitSpecs2Support {
      import system.dispatcher

      val obj = JsonRecord("hello json", 100, true, Some(-1))
      val jsonClient = JsonClient(host="127.0.0.1", port=6379)
      jsonClient.Json.set("json", "$", obj)
      jsonClient.Json.set("json2", "$", "test")

      val res1 = Await.result(jsonClient.Json.get[JsonRecord]("json2"),_5s)

      res1 must beNone

      val res2 = Await.result(jsonClient.Json.get[JsonRecord]("json"),_5s)

      res2 must_!= None
      res2.get must_== obj

      Await.result(jsonClient.Json.get[String]("json2"),Duration("5s")) must_== Some("test")

      Await.result(jsonClient.Json.toggle("json", "$.flag"),_5s) must_== List(Some(0L))

      val res3 = Await.result(jsonClient.Json.get[JsonRecord]("json"),_5s)
      res3.get.flag must_== false

      Await.result(jsonClient.Json.clear("json", "$.score"),_5s) must_== 1

      val res4 = Await.result(jsonClient.Json.get[JsonRecord]("json"),_5s)
      res4.get.score must_== 0

      Await.result(jsonClient.Json.del("json", "$.optValue"),_5s) must_== 1

      val res5 = Await.result(jsonClient.Json.get[JsonRecord]("json"),_5s)
      res5.get.optValue must beNone

      Await.result(jsonClient.Json.del("json", "$.optValue"),_5s) must_== 0
    }
  }
}
