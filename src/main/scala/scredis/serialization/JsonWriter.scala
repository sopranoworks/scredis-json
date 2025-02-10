package scredis.serialization

import io.circe.Json

object JsonWriter extends Writer[Json] {
  override protected def writeImpl(value: Json): Array[Byte] = value.toString().getBytes("UTF-8")
}
