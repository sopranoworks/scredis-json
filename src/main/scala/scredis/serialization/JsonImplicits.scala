package scredis.serialization

import io.circe.Json

object JsonImplicits {
  import Implicits._

  implicit val jsonWriter:Writer[Json] = JsonWriter
}
