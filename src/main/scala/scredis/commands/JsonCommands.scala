package scredis.commands

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import scredis.io.NonBlockingConnection
import scredis.protocol.requests.JsonRequests._
import scredis.serialization.{JsonImplicits, Writer}

import scala.concurrent.{ExecutionContext, Future}

trait JsonCommands { self: NonBlockingConnection =>
  object Json {
    def setRaw[W](key:String, path:String = "$", value:W)(implicit writer:Writer[W]):Future[Boolean] =
      send(JsonSet(key, path, value))

    def set[W](key:String, path:String = "$", value:W)(implicit encoder: Encoder[W]):Future[Boolean] =
      send(JsonSet(key, path, value.asJson)(JsonImplicits.jsonWriter))

    def getRaw(key:String, paths:String*):Future[Option[Json]] =
      send(JsonGet(key, paths:_*))

    def get[R](key:String, paths:String*)(implicit decoder: Decoder[R], ec:ExecutionContext):Future[Option[R]] =
      send(JsonGet(key, paths:_*)).map (_.flatMap (_.as[R].toOption))

    def mergeRaw[W](key:String, path:String, value:W)(implicit writer: Writer[W]):Future[Boolean] =
      send(JsonMerge(key, path, value))

    def merge[W](key:String, path:String, value:W)(implicit encoder: Encoder[W]):Future[Boolean] =
      send(JsonMerge(key, path, value.asJson)(JsonImplicits.jsonWriter))

    def del(key:String, path:String = "$"):Future[Long] =
      send(JsonDel(key, path))

    def toggle(key:String, path:String = "$"):Future[List[Option[Long]]] =
      send(JsonToggle(key, path))

    def clear(key:String, path:String = "$"):Future[Long] =
      send(JsonClear(key, path))

    def types(key:String, path:String = "$"):Future[List[Option[String]]] =
      send(JsonType(key, path))


    def strLen(key:String, path:String = "$"):Future[List[Option[Long]]] =
      send(JsonStrLen(key, path))

    def strAppend[W](key:String, path:String = "$", value:W)(implicit writer:Writer[W]):Future[List[Option[Long]]] =
      send(JsonStrAppend(key, path, value))


    def numIncBy[W](key:String, path:String = "$", value:W)(implicit writer: Writer[W]):Future[Option[Json]] =
      send(JsonNumIncBy(key, path, value))

    def numMulBy[W](key:String, path:String = "$", value:W)(implicit writer: Writer[W]):Future[Option[Json]] =
      send(JsonNumMulBy(key, path, value))


    def arrAppend[W](key:String, path:String, values:W*)(implicit writer: Writer[W]):Future[List[Option[Long]]] =
      send(JsonArrAppend(key, path, values:_*))  

    def arrIndex[W](key:String, path:String, start:Int, stop:Int):Future[List[Option[Long]]] =
      send(JsonArrIndex(key, path, start, stop))

    def arrInsert[W](key:String, path:String, index:Int, values:W*)(implicit writer: Writer[W]):Future[List[Option[Long]]] =
      send(JsonArrInsert(key, path, index, values:_*))

    def arrLen(key:String, path:String = "$"):Future[List[Option[Long]]] =
      send(JsonArrLen(key, path))

    def arrPop(key:String, path:String, index:Int):Future[List[Option[Json]]] =
      send(JsonArrPop(key, path, index))

    def arrTrim[W](key:String, path:String, start:Int, stop:Int):Future[List[Option[Long]]] =
      send(JsonArrTrim(key, path, start, stop))
  }
}
