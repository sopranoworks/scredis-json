## scredis-json

scredis-json is an extension library to append Redis Json commands to [scredis](https://github.com/scredis/scredis) using [circe](https://github.com/circe/circe).
               
scredis-json appends

* Json.arrAppend
* Json.arrIndex
* Json.arrInsert
* Json.arrLen
* Json.arrPop
* Json.clean
* Json.debugMemory
* Json.del
* Json.get
* Json.merge
* Json.numIncBy
* Json.objKeys
* Json.objLen
* Json.set(NX/XX)
* Json.strAppend
* Json.strLen
* Json.toggle
* Json.types

To know details, check [here](https://redis.io/docs/latest/develop/data-types/json/)

## Using Json.* commands

To use Json.* commands, use RedisJsonClient/RedisJsonCluster instead of RedisClient/RedisCluster.

The initialization method remains the same.

```scala
val jsonClient = RedisJson(host="127.0.0.1", port=6379)
```

You can use powerful circe json serialization/deserialization in many commands .

```scala
case class JsonRecord(name:String, score:Int, flag:Boolean, optValue:Option[Int], arr:List[Int], emptyArr:List[Int] = Nil)

val obj = JsonRecord("hello json", 100, true, Some(-1), List(1,2,3))

jsonClient.Json.setNX("json", "$", obj)
jsonClient.Json.set("json2", "$", "test")

jsonClient.Json.get[JsonRecord]("json")
```

## To use library

Add to your project's libraryDependencies like this

```scala
resolvers in Global += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
librartDependencies += "com.sopranoworks" %% "scredis-json" % "1.0.0-SNAPSHOT"
```

## License

MIT License

Copyright (c) 2025 Osamu Takahashi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
