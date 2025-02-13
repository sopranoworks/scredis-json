/*
 * The contents of this file are mostly taken from scredis.
 *
 * https://github.com/scredis/scredis/blob/master/src/main/scala/scredis/Client.scala
 *
 * Copyright (c) 2013 Livestream LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scredis

import akka.actor.ActorSystem
import com.typesafe.config.Config
import scredis.commands.JsonCommands
import scredis.protocol.AuthConfig

import scala.concurrent.duration.FiniteDuration

class JsonClient(
  host: String = RedisConfigDefaults.Redis.Host,
  port: Int = RedisConfigDefaults.Redis.Port,
  authOpt: Option[AuthConfig] = RedisConfigDefaults.Redis.AuthOpt,
  database: Int = RedisConfigDefaults.Redis.Database,
  nameOpt: Option[String] = RedisConfigDefaults.Redis.NameOpt,
  connectTimeout: FiniteDuration = RedisConfigDefaults.IO.ConnectTimeout,
  receiveTimeoutOpt: Option[FiniteDuration] = RedisConfigDefaults.IO.ReceiveTimeoutOpt,
  maxWriteBatchSize: Int = RedisConfigDefaults.IO.MaxWriteBatchSize,
  tcpSendBufferSizeHint: Int = RedisConfigDefaults.IO.TCPSendBufferSizeHint,
  tcpReceiveBufferSizeHint: Int = RedisConfigDefaults.IO.TCPReceiveBufferSizeHint,
  akkaListenerDispatcherPath: String = RedisConfigDefaults.IO.Akka.ListenerDispatcherPath,
  akkaIODispatcherPath: String = RedisConfigDefaults.IO.Akka.IODispatcherPath,
  akkaDecoderDispatcherPath: String = RedisConfigDefaults.IO.Akka.DecoderDispatcherPath,
  failCommandOnConnecting: Boolean =  RedisConfigDefaults.Global.FailCommandOnConnecting
)(implicit system: ActorSystem) extends Client with JsonCommands {
  def this(config: RedisConfig)(implicit system: ActorSystem) = this(
    host = config.Redis.Host,
    port = config.Redis.Port,
    authOpt = config.Redis.AuthOpt,
    database = config.Redis.Database,
    nameOpt = config.Redis.NameOpt,
    connectTimeout = config.IO.ConnectTimeout,
    receiveTimeoutOpt = config.IO.ReceiveTimeoutOpt,
    maxWriteBatchSize = config.IO.MaxWriteBatchSize,
    tcpSendBufferSizeHint = config.IO.TCPSendBufferSizeHint,
    tcpReceiveBufferSizeHint = config.IO.TCPReceiveBufferSizeHint,
    akkaListenerDispatcherPath = config.IO.Akka.ListenerDispatcherPath,
    akkaIODispatcherPath = config.IO.Akka.IODispatcherPath,
    akkaDecoderDispatcherPath = config.IO.Akka.DecoderDispatcherPath,
    failCommandOnConnecting = config.Global.FailCommandOnConnecting
  )
  def this(config: Config)(implicit system: ActorSystem) = this(RedisConfig(config))
  def this(configName: String)(implicit system: ActorSystem) = this(RedisConfig(configName))
  def this(configName: String, path: String)(implicit system: ActorSystem) = this(
    RedisConfig(configName, path)
  )
}

object JsonClient {
  def apply(
    host: String = RedisConfigDefaults.Redis.Host,
    port: Int = RedisConfigDefaults.Redis.Port,
    authOpt: Option[AuthConfig] = RedisConfigDefaults.Redis.AuthOpt,
    database: Int = RedisConfigDefaults.Redis.Database,
    nameOpt: Option[String] = RedisConfigDefaults.Redis.NameOpt,
    connectTimeout: FiniteDuration = RedisConfigDefaults.IO.ConnectTimeout,
    receiveTimeoutOpt: Option[FiniteDuration] = RedisConfigDefaults.IO.ReceiveTimeoutOpt,
    maxWriteBatchSize: Int = RedisConfigDefaults.IO.MaxWriteBatchSize,
    tcpSendBufferSizeHint: Int = RedisConfigDefaults.IO.TCPSendBufferSizeHint,
    tcpReceiveBufferSizeHint: Int = RedisConfigDefaults.IO.TCPReceiveBufferSizeHint,
    akkaListenerDispatcherPath: String = RedisConfigDefaults.IO.Akka.ListenerDispatcherPath,
    akkaIODispatcherPath: String = RedisConfigDefaults.IO.Akka.IODispatcherPath,
    akkaDecoderDispatcherPath: String = RedisConfigDefaults.IO.Akka.DecoderDispatcherPath,
    failCommandOnConnecting: Boolean =  RedisConfigDefaults.Global.FailCommandOnConnecting
  )(implicit system: ActorSystem): JsonClient = new JsonClient(
    host = host,
    port = port,
    authOpt = authOpt,
    database = database,
    nameOpt = nameOpt,
    connectTimeout = connectTimeout,
    receiveTimeoutOpt = receiveTimeoutOpt,
    maxWriteBatchSize = maxWriteBatchSize,
    tcpSendBufferSizeHint = tcpSendBufferSizeHint,
    tcpReceiveBufferSizeHint = tcpReceiveBufferSizeHint,
    akkaListenerDispatcherPath = akkaListenerDispatcherPath,
    akkaIODispatcherPath = akkaIODispatcherPath,
    akkaDecoderDispatcherPath = akkaDecoderDispatcherPath,
    failCommandOnConnecting =  failCommandOnConnecting
  )
  def apply(config: RedisConfig)(implicit system: ActorSystem): JsonClient = new JsonClient(config)
  def apply(config: Config)(implicit system: ActorSystem): JsonClient = new JsonClient(config)
  def apply(configName: String)(implicit system: ActorSystem): JsonClient = new JsonClient(configName)
  def apply(configName: String, path: String)(implicit system: ActorSystem): JsonClient = new JsonClient(
    configName, path
  )
}
