package scredis.commands

import io.circe._
import io.circe.syntax._
import scredis.io.NonBlockingConnection
import scredis.protocol.requests.JsonRequests._
import scredis.serialization.{JsonImplicits, Writer}

import scala.concurrent.{ExecutionContext, Future}

trait JsonCommands { self: NonBlockingConnection =>

  private implicit class DoubleQuoteEscape(str:String) {
    def escapeDoubleQuote:String = {
      var skip = false
      new String(str.toCharArray.flatMap {
        c =>
          if (skip) {
            skip = false
            Array(c)
          } else {
            if (c == '\"') {
              Array('\\', '\"')
            } else {
              if (c == '\\') {
                skip = true
              }
              Array(c)
            }
          }
      })
    }
  }

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

    def strAppendRaw(key:String, path:String = "$", value:Any):Future[List[Option[Long]]] =
      send(JsonStrAppend(key, path, s""""${value.toString}""""))

    def strAppend(key:String, path:String = "$", value:Any):Future[List[Option[Long]]] =
      send(JsonStrAppend(key, path, s""""${value.toString.escapeDoubleQuote}""""))


    def numIncBy[W](key:String, path:String = "$", value:W)(implicit writer: Writer[W]):Future[Option[Json]] =
      send(JsonNumIncBy(key, path, value))

    def numMulBy[W](key:String, path:String = "$", value:W)(implicit writer: Writer[W]):Future[Option[Json]] =
      send(JsonNumMulBy(key, path, value))


    def mSetRaw[W](keyPathValue:(String, String, W)*)(implicit writer: Writer[W]):Future[Boolean] =
      send(JsonMSet(keyPathValue:_*))

    def mSet[W](keyPathValue:(String, String, W)*)(implicit encoder: Encoder[W]):Future[Boolean] =
      send(JsonMSet(keyPathValue.map(kpv => (kpv._1, kpv._2, kpv._3.asJson)):_*)(JsonImplicits.jsonWriter))

    def mGetRaw[R](keys:Seq[String], path:String ="$"):Future[List[Option[Json]]] =
      send(JsonMGet(keys, path))

    def mGet[R](keys:Seq[String], path:String ="$")(implicit decoder: Decoder[R], ec:ExecutionContext):Future[List[Option[R]]] =
      mGetRaw(keys, path).map(_.map(_.flatMap(_.as[R].toOption)))


    /**
     * Append the json values into the array at path after the last element in it
     *
     * @param key is key to modify.
     * @param path is JSONPath to specify. Default is root $.
     * @param values is one or more values to append to one or more arrays.
     * @param writer is value type parameter writer
     * @tparam W is source value type
     * @return an array of integer replies for each path, the array's new size, or None, if the matching JSON value is not an array.
     */
    def arrAppendRaw[W](key:String, path:String, values:W*)(implicit writer: Writer[W]):Future[List[Option[Long]]] =
      send(JsonArrAppend(key, path, values:_*))

    /**
     * Append the json values into the array at path after the last element in it
     * 
     * @param key is key to modify.
     * @param path is JSONPath to specify. Default is root $.
     * @param values is one or more values to append to one or more arrays.
     * @param encoder is json encoder for W
     * @tparam W is source value type
     * @return an array of integer replies for each path, the array's new size, or None, if the matching JSON value is not an array.
     */
    def arrAppend[W](key:String, path:String, values:W*)(implicit encoder: Encoder[W]):Future[List[Option[Long]]] =
      send(JsonArrAppend(key, path, values.map(_.asJson):_*)(JsonImplicits.jsonWriter))

    /**
     *
     * Search for the first occurrence of a JSON value in an array
     *
     * @param key is key to parse.
     * @param path is JSONPath to specify.
     * @param value is value to find its index in one or more arrays.
     * @param start is inclusive start value to specify in a slice of the array to search. Default is 0.
     * @param stop is exclusive stop value to specify in a slice of the array to search, including the last element. Default is 0. Negative values are interpreted as starting from the end.
     * @param writer is value type parameter writer
     * @tparam W is value type
     * @return an array of integer replies for each path, the first position in the array of each JSON value that matches the path, -1 if unfound in the array, or nil, if the matching JSON value is not an array.
     */
    def arrIndexRaw[W](key:String, path:String, value:W, start:Int = 0, stop:Int = 0)(implicit writer: Writer[W]):Future[List[Option[Long]]] =
      send(JsonArrIndex(key, path, value, start, stop))

    /**
     *
     * Search for the first occurrence of a JSON value in an array
     *
     * @param key is key to parse.
     * @param path is JSONPath to specify.
     * @param value is value to find its index in one or more arrays.
     * @param start is inclusive start value to specify in a slice of the array to search. Default is 0.
     * @param stop is exclusive stop value to specify in a slice of the array to search, including the last element. Default is 0. Negative values are interpreted as starting from the end.
     * @param encoder is json encoder for W
     * @tparam W is source value type
     * @return an array of integer replies for each path, the first position in the array of each JSON value that matches the path, -1 if unfound in the array, or nil, if the matching JSON value is not an array.
     */
    def arrIndex[W](key:String, path:String, value:W, start:Int = 0, stop:Int = 0)(implicit encoder: Encoder[W]):Future[List[Option[Long]]] =
      arrIndexRaw(key, path, value.asJson, start, stop)(JsonImplicits.jsonWriter)

    /**
     * Insert the json values into the array at path before the index (shifts to the right)
     *
     * @param key is key to modify.
     * @param path is JSONPath to specify. Default is root $.
     * @param index is position in the array where you want to insert a value. The index must be in the array's range. Inserting at index 0 prepends to the array. Negative index values start from the end of the array.
     * @param values is one or more values to insert in one or more arrays.
     * @param writer is value type parameter writer
     * @tparam W is source value type
     * @return an array of integer replies for each path, the array's new size, or None, if the matching JSON value is not an array.
     */
    def arrInsertRaw[W](key:String, path:String, index:Int, values:W*)(implicit writer: Writer[W]):Future[List[Option[Long]]] =
      send(JsonArrInsert(key, path, index, values:_*))

    /**
     * Insert the json values into the array at path before the index (shifts to the right)
     * 
     * @param key is key to modify.
     * @param path is JSONPath to specify. Default is root $.
     * @param index is position in the array where you want to insert a value. The index must be in the array's range. Inserting at index 0 prepends to the array. Negative index values start from the end of the array.
     * @param values is one or more values to insert in one or more arrays.
     * @param encoder is json encoder for W
     * @tparam W is source value type
     * @return an array of integer replies for each path, the array's new size, or None, if the matching JSON value is not an array.
     */
    def arrInsert[W](key:String, path:String, index:Int, values:W*)(implicit encoder: Encoder[W]):Future[List[Option[Long]]] =
      send(JsonArrInsert(key, path, index, values.map(_.asJson):_*)(JsonImplicits.jsonWriter))

    /**
     * Report the length of the JSON array at path in key
     * 
     * @param key is key to parse.
     * @param path is JSONPath to specify. Default is root $, if not provided. Returns null if the key or path do not exist.
     * @return an array of integer replies, an integer for each matching value, each is the array's length, or None, if the matching value is not an array.
     */
    def arrLen(key:String, path:String = "$"):Future[List[Option[Long]]] =
      send(JsonArrLen(key, path))

    /**
     * Remove and return an element from the index in the array
     *
     * @param key is key to modify.
     * @param path is JSONPath to specify. Default is root $.
     * @param index is position in the array to start popping from. Default is -1, meaning the last element. Out-of-range indexes round to their respective array ends. Popping an empty array returns null.
     * @return an array of JSON value, or None, if the matching JSON value is not an array.
     */
    def arrPopRaw(key:String, path:String = "$", index:Int = -1):Future[List[Option[Json]]] =
      send(JsonArrPop(key, path, index))

    /**
     * Remove and return an element from the index in the array
     * 
     * @param key is key to modify.
     * @param path is JSONPath to specify. Default is root $.
     * @param index is position in the array to start popping from. Default is -1, meaning the last element. Out-of-range indexes round to their respective array ends. Popping an empty array returns null.
     * @param decoder is json decoder for R
     * @param ec is execution context
     * @tparam R is result value type
     * @return an array of JSON value, or None, if the matching JSON value is not an array.
     */
    def arrPop[R](key:String, path:String = "$", index:Int = -1)(implicit decoder: Decoder[R], ec:ExecutionContext):Future[List[Option[R]]] =
      arrPopRaw(key, path, index).map(_.map(_.flatMap(_.as[R].toOption)))

    /**
     * Trim an array so that it contains only the specified inclusive range of elements
     * 
     * @param key is key to modify.
     * @param path is JSONPath to specify. Default is root $.
     * @param start is index of the first element to keep (previous elements are trimmed). Default is 0.
     * @param stop is the index of the last element to keep (following elements are trimmed), including the last element. Default is 0. Negative values are interpreted as starting from the end.
     * @return an array of integer replies for each path, the array's new size, or None, if the matching JSON value is not an array.
     */
    def arrTrim(key:String, path:String = "$", start:Int = 0, stop:Int = 0):Future[List[Option[Long]]] =
      send(JsonArrTrim(key, path, start, stop))
  }
}
