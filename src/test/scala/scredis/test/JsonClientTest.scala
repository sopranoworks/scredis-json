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

  case class JsonRecord(name:String, score:Int, flag:Boolean, optValue:Option[Int], arr:List[Int], emptyArr:List[Int] = Nil)
  case class JsonRecord2(name:String, score:Int, level:Int, flag:Boolean, optValue:Option[Int], arr:List[Int], emptyArr:List[Int] = Nil)
  case class Level(level:Int)

  "executing JSON. commands" should {
    "normally success" in new AkkaTestkitSpecs2Support {
      import system.dispatcher

      val obj = JsonRecord("hello json", 100, true, Some(-1), List(1,2,3))
      val jsonClient = JsonClient(host="127.0.0.1", port=6379)
      jsonClient.Json.set("json", "$", obj)
      jsonClient.Json.set("json2", "$", "test")
      jsonClient.Json.set("json3", "$", obj.copy(optValue = None))

      val res1 = Await.result(jsonClient.Json.get[JsonRecord]("json2"),_5s)

      res1 must beNone

      val res2 = Await.result(jsonClient.Json.get[JsonRecord]("json"),_5s)

      res2 must_!= None
      res2.get must_== obj

      Await.result(jsonClient.Json.get[String]("json2"),Duration("5s")) must_== Some("test")

      Await.result(jsonClient.Json.toggle("json", "$.flag"),_5s) must_== List(Some(0L))

      val res3 = Await.result(jsonClient.Json.get[JsonRecord]("json"),_5s)
      res3.get.flag must_== false

      // JSON.CLEAR

      Await.result(jsonClient.Json.clear("json", "$.score"),_5s) must_== 1

      val res4 = Await.result(jsonClient.Json.get[JsonRecord]("json"),_5s)
      res4.get.score must_== 0

      // JSON.DEL

      Await.result(jsonClient.Json.del("json", "$.optValue"),_5s) must_== 1

      val res5 = Await.result(jsonClient.Json.get[JsonRecord]("json"),_5s)
      res5.get.optValue must beNone

      Await.result(jsonClient.Json.del("json", "$.optValue"),_5s) must_== 0

      // JSON.STRLEN

      Await.result(jsonClient.Json.strLen("json", "$.name"),_5s).head.get must_== 10
      Await.result(jsonClient.Json.strLen("json", "$.name2"),_5s).isEmpty must_== true
      Await.result(jsonClient.Json.strLen("json", "$.score"),_5s).head must beNone

      // JSON.TYPE

      Await.result(jsonClient.Json.types("json", "$.score"), _5s) must_== List(Some("integer"))
      Await.result(jsonClient.Json.types("json", "$.name"), _5s) must_== List(Some("string"))

      // JSON.STRAPPEND

      Await.result(jsonClient.Json.strAppend("json", "$.name", "!!"),_5s).head.get must_== 12
      Await.result(jsonClient.Json.strAppend("json", "$.score", "!!"),_5s).head must beNone
      Await.result(jsonClient.Json.strAppend("json", "$.name", "\"quote\""),_5s).head.get must_== 19
      Await.result(jsonClient.Json.strAppend("json", "$.name", """"quote2""""),_5s).head.get must_== 27

      // JSON.NUMINCBY

      Await.result(jsonClient.Json.numIncBy("json", "$.score", 10),_5s).head.get must_== 10
      Await.result(jsonClient.Json.numIncBy("json", "$.score", 10),_5s).head.get must_== 20
      Await.result(jsonClient.Json.numIncBy("json", "$.name", 10),_5s).head must beNone

      // JSON.MSET

      Await.result(jsonClient.Json.mSet(("json","$.score", 1000), ("json", "$.optValue", 999)),_5s) must_== true
      val res6 = Await.result(jsonClient.Json.get[JsonRecord]("json"),_5s).get
      res6.score must_== 1000
      res6.optValue must beSome(999)

      // JSON.MGet

      Await.result(jsonClient.Json.mGet[Int](List("json", "json3", "json4"), "$.score"), _5s) must_== List(Some(List(Some(1000))), Some(List(Some(100))), None)
      Await.result(jsonClient.Json.mGet[Int](List("json", "json3"), "$.optValue"), _5s) must_== List(Some(List(Some(999))), Some(List(None)))

      // JSON.OBJLEN

      Await.result(jsonClient.Json.objLen("json"), _5s) must_== List(Some(6))
      Await.result(jsonClient.Json.objLen("json2"), _5s) must_== List(None)

      // JSON.OBJKEYS

      Await.result(jsonClient.Json.objKeys("json"), _5s) must_== List(Some(List("name", "score", "flag", "emptyArr", "arr", "optValue")))
      Await.result(jsonClient.Json.objKeys("json2"), _5s) must_== List(None)

      // JSON.ARRAPPEND

      Await.result(jsonClient.Json.arrAppend("json", "$.arr", 4, 5, 6),_5s).head.get must_== 6
      val res7 = Await.result(jsonClient.Json.get[JsonRecord]("json"),_5s)
      res7.get.arr must_== List(1,2,3,4,5,6)
      
      Await.result(jsonClient.Json.arrAppend("json", "$.name", 4, 5, 6),_5s).head must beNone

      // JSON.ARRLEN

      Await.result(jsonClient.Json.arrLen("json", "$.arr"),_5s).head.get must_== 6
      Await.result(jsonClient.Json.arrLen("json", "$.name"),_5s).head must beNone

      // JSON.ARRINSERT

      Await.result(jsonClient.Json.arrInsert("json", "$.arr", 3, 10, 11, 12),_5s).head.get must_== 9
      val res8 = Await.result(jsonClient.Json.get[JsonRecord]("json"),_5s)
      res8.get.arr must_== List(1,2,3,10,11,12,4,5,6)

      // JSON.ARRINDEX

      Await.result(jsonClient.Json.arrIndex("json", "$.arr", 3), _5s).head.get must_== 2

      // JSON.ARRPOP

      Await.result(jsonClient.Json.arrPop[Int]("json", "$.arr"), _5s).head.get must_== 6
      Await.result(jsonClient.Json.arrPop[Int]("json", "$.arr", 0), _5s).head.get must_== 1
      Await.result(jsonClient.Json.arrPop[Int]("json", "$.emptyArr"), _5s).head must beNone
      Await.result(jsonClient.Json.arrPop[Int]("json", "$.name"), _5s).head must beNone

      // JSON.ARRTRIM

      Await.result(jsonClient.Json.arrTrim("json", "$.arr", 2, 4),_5s).head.get must_== 3
      val res9 = Await.result(jsonClient.Json.get[JsonRecord]("json"),_5s)
      res9.get.arr must_== List(10,11,12)
      Await.result(jsonClient.Json.arrTrim("json", "$.emptyArr", 2, 4),_5s).head.get must_== 0
      Await.result(jsonClient.Json.arrTrim("json", "$.name", 2, 4),_5s).head must beNone

      // JSON.MERGE

      Await.result(jsonClient.Json.merge("json", "$", Level(99)), _5s) must_== true
      val res10 = Await.result(jsonClient.Json.get[JsonRecord2]("json"),_5s).get
      res10.level must_== 99

      // JSON.DEBUG MEMORY

      Await.result(jsonClient.Json.debugMemory("json"), _5s).nonEmpty must_== true
      Await.result(jsonClient.Json.debugMemory("json", "$.unknown"), _5s).isEmpty must_== true
      Await.result(jsonClient.Json.debugMemory("json5"), _5s).isEmpty must_== true
    }
  }
}
