/*
 * The contents of this file are mostly taken from scredis.
 *
 * https://github.com/scredis/scredis/blob/master/src/main/scala/scredis/Redis.scala
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

class RedisJson(
    systemOrName: Either[ActorSystem, String],
    host: String,
    port: Int,
    authOpt: Option[AuthConfig],
    database: Int,
    nameOpt: Option[String],
    connectTimeout: FiniteDuration,
    receiveTimeoutOpt: Option[FiniteDuration],
    maxWriteBatchSize: Int,
    tcpSendBufferSizeHint: Int,
    tcpReceiveBufferSizeHint: Int,
    akkaListenerDispatcherPath: String,
    akkaIODispatcherPath: String,
    akkaDecoderDispatcherPath: String,
    failCommandOnConnecting: Boolean,
    subscription: Subscription
  ) extends Redis(systemOrName, host, port, authOpt, database, nameOpt, connectTimeout, receiveTimeoutOpt, maxWriteBatchSize, tcpSendBufferSizeHint, tcpReceiveBufferSizeHint, akkaListenerDispatcherPath, akkaIODispatcherPath, akkaDecoderDispatcherPath, failCommandOnConnecting, subscription) with JsonCommands {

  def this(
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
    actorSystemName: String = RedisConfigDefaults.IO.Akka.ActorSystemName,
    akkaListenerDispatcherPath: String = RedisConfigDefaults.IO.Akka.ListenerDispatcherPath,
    akkaIODispatcherPath: String = RedisConfigDefaults.IO.Akka.IODispatcherPath,
    akkaDecoderDispatcherPath: String = RedisConfigDefaults.IO.Akka.DecoderDispatcherPath,
    failCommandOnConnecting: Boolean = RedisConfigDefaults.Global.FailCommandOnConnecting,
    subscription: Subscription = RedisConfigDefaults.LoggingSubscription
  ) = this(
    systemOrName = Right(actorSystemName),
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
    failCommandOnConnecting = failCommandOnConnecting,
    subscription = subscription
  )

  def this(config: RedisConfig, subscription: Subscription) = this(
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
    actorSystemName = config.IO.Akka.ActorSystemName,
    akkaListenerDispatcherPath = config.IO.Akka.ListenerDispatcherPath,
    akkaIODispatcherPath = config.IO.Akka.IODispatcherPath,
    akkaDecoderDispatcherPath = config.IO.Akka.DecoderDispatcherPath,
    failCommandOnConnecting = config.Global.FailCommandOnConnecting,
    subscription = subscription
  )

  def this(config: RedisConfig) = this(config, RedisConfigDefaults.LoggingSubscription)
  def this() = this(RedisConfig())
  def this(config: Config) = this(RedisConfig(config))
  def this(configName: String) = this(RedisConfig(configName))
  def this(configName: String, path: String) = this(RedisConfig(configName, path))
}

object RedisJson {
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
    actorSystemName: String = RedisConfigDefaults.IO.Akka.ActorSystemName,
    akkaListenerDispatcherPath: String = RedisConfigDefaults.IO.Akka.ListenerDispatcherPath,
    akkaIODispatcherPath: String = RedisConfigDefaults.IO.Akka.IODispatcherPath,
    akkaDecoderDispatcherPath: String = RedisConfigDefaults.IO.Akka.DecoderDispatcherPath,
    failCommandOnConnecting: Boolean = RedisConfigDefaults.Global.FailCommandOnConnecting
  ): RedisJson = new RedisJson(
    host = host,
    port = port,
    authOpt = authOpt,
    database = database,
    nameOpt = nameOpt,
    connectTimeout = connectTimeout,
    receiveTimeoutOpt = receiveTimeoutOpt,
    maxWriteBatchSize = maxWriteBatchSize,
    tcpSendBufferSizeHint = tcpSendBufferSizeHint,
    tcpReceiveBufferSizeHint = tcpSendBufferSizeHint,
    actorSystemName = actorSystemName,
    akkaListenerDispatcherPath = akkaListenerDispatcherPath,
    akkaIODispatcherPath = akkaIODispatcherPath,
    akkaDecoderDispatcherPath = akkaDecoderDispatcherPath,
    failCommandOnConnecting = failCommandOnConnecting
  )

  def apply() = new RedisJson(RedisConfig())
  def apply(config: RedisConfig): RedisJson = new RedisJson(config)
  def apply(subscription: Subscription): RedisJson = new RedisJson(RedisConfig(), subscription)
  def apply(config: Config): RedisJson = new RedisJson(config)
  def apply(configName: String): RedisJson = new RedisJson(configName)
  def apply(configName: String, path: String): RedisJson = new RedisJson(configName, path)

  def withActorSystem(
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
    failCommandOnConnecting: Boolean = RedisConfigDefaults.Global.FailCommandOnConnecting,
    subscription: Subscription = RedisConfigDefaults.LoggingSubscription
  )(implicit system: ActorSystem): RedisJson = new RedisJson(
    systemOrName = Left(system),
    host = host,
    port = port,
    authOpt = authOpt,
    database = database,
    nameOpt = nameOpt,
    connectTimeout = connectTimeout,
    receiveTimeoutOpt = receiveTimeoutOpt,
    maxWriteBatchSize = maxWriteBatchSize,
    tcpSendBufferSizeHint = tcpSendBufferSizeHint,
    tcpReceiveBufferSizeHint = tcpSendBufferSizeHint,
    akkaListenerDispatcherPath = akkaListenerDispatcherPath,
    akkaIODispatcherPath = akkaIODispatcherPath,
    akkaDecoderDispatcherPath = akkaDecoderDispatcherPath,
    failCommandOnConnecting = failCommandOnConnecting,
    subscription = subscription
  )
  def withActorSystem()(implicit system: ActorSystem): RedisJson = withActorSystem(RedisConfig())
  def withActorSystem(config: RedisConfig)(implicit system: ActorSystem): RedisJson = new RedisJson(
    systemOrName = Left(system),
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
    failCommandOnConnecting = config.Global.FailCommandOnConnecting,
    subscription = RedisConfigDefaults.LoggingSubscription
  )
  def withActorSystem(config: Config)(implicit system: ActorSystem): RedisJson = withActorSystem(
    RedisConfig(config)
  )
  def withActorSystem(configName: String)(implicit system: ActorSystem): RedisJson = withActorSystem(
    RedisConfig(configName)
  )
  def withActorSystem(configName: String, path: String)(
    implicit system: ActorSystem
  ): RedisJson = withActorSystem(RedisConfig(configName, path))
}
