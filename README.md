## scredis-json

scredis-json is an extension library to append Redis Json commands to scredis using circe.
               
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
val jsonClient = RedisJsonClient(host="127.0.0.1", port=6379)
client
```

You can use powerful circe json serialization/deserialization in many commands .

```scala
case class JsonRecord(name:String, score:Int, flag:Boolean, optValue:Option[Int], arr:List[Int], emptyArr:List[Int] = Nil)

jsonClient.Json.setNX("json", "$", obj)
jsonClient.Json.set("json2", "$", "test")

jsonClient.Json.get[JsonRecord]("json")
```

## To use library

Add to your project's libraryDependencies like this

```scala
librartDependencies += "com.sopranoworks" %% "scredis-json" % "1.0.0"
```
