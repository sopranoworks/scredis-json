/*
 * The contents of this file are mostly taken from scredis.
 *
 * https://github.com/scredis/scredis/blob/master/src/main/scala/scredis/RedisCluster.scala
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

class RedisJsonCluster(
    nodes: Seq[Server] = RedisConfigDefaults.Redis.ClusterNodes,
    maxRetries: Int = 4,
    receiveTimeoutOpt: Option[FiniteDuration] = RedisConfigDefaults.IO.ReceiveTimeoutOpt,
    connectTimeout: FiniteDuration = RedisConfigDefaults.IO.ConnectTimeout,
    maxWriteBatchSize: Int = RedisConfigDefaults.IO.MaxWriteBatchSize,
    tcpSendBufferSizeHint: Int = RedisConfigDefaults.IO.TCPSendBufferSizeHint,
    tcpReceiveBufferSizeHint: Int = RedisConfigDefaults.IO.TCPReceiveBufferSizeHint,
    akkaListenerDispatcherPath: String = RedisConfigDefaults.IO.Akka.ListenerDispatcherPath,
    akkaIODispatcherPath: String = RedisConfigDefaults.IO.Akka.IODispatcherPath,
    akkaDecoderDispatcherPath: String = RedisConfigDefaults.IO.Akka.DecoderDispatcherPath,
    tryAgainWait: FiniteDuration = RedisConfigDefaults.IO.Cluster.TryAgainWait,
    clusterDownWait: FiniteDuration = RedisConfigDefaults.IO.Cluster.ClusterDownWait,
    systemOpt:Option[ActorSystem] = None,
    failCommandOnConnecting: Boolean = RedisConfigDefaults.Global.FailCommandOnConnecting,
    authOpt: Option[AuthConfig] = RedisConfigDefaults.Config.Redis.AuthOpt
  ) extends RedisCluster with JsonCommands {

  def this(config: RedisConfig, systemOpt:Option[ActorSystem]) = this(
    nodes = config.Redis.ClusterNodes,
    maxRetries = 4,
    receiveTimeoutOpt = config.IO.ReceiveTimeoutOpt,
    connectTimeout = config.IO.ConnectTimeout,
    maxWriteBatchSize = config.IO.MaxWriteBatchSize,
    tcpSendBufferSizeHint = config.IO.TCPSendBufferSizeHint,
    tcpReceiveBufferSizeHint = config.IO.TCPReceiveBufferSizeHint,
    akkaListenerDispatcherPath = config.IO.Akka.ListenerDispatcherPath,
    akkaIODispatcherPath = config.IO.Akka.IODispatcherPath,
    akkaDecoderDispatcherPath = config.IO.Akka.DecoderDispatcherPath,
    tryAgainWait = config.IO.Cluster.TryAgainWait,
    clusterDownWait = config.IO.Cluster.ClusterDownWait,
    systemOpt = systemOpt,
    failCommandOnConnecting = config.Global.FailCommandOnConnecting,
    authOpt = config.Redis.AuthOpt
  )

  def this() = this(RedisConfig(), None)
  def this(system:ActorSystem) = this(RedisConfig(), Some(system))
}

object RedisJsonCluster {
  def apply(
    nodes: Seq[Server] = RedisConfigDefaults.Redis.ClusterNodes,
    maxRetries: Int = 4,
    receiveTimeoutOpt: Option[FiniteDuration] = RedisConfigDefaults.IO.ReceiveTimeoutOpt,
    connectTimeout: FiniteDuration = RedisConfigDefaults.IO.ConnectTimeout,
    maxWriteBatchSize: Int = RedisConfigDefaults.IO.MaxWriteBatchSize,
    tcpSendBufferSizeHint: Int = RedisConfigDefaults.IO.TCPSendBufferSizeHint,
    tcpReceiveBufferSizeHint: Int = RedisConfigDefaults.IO.TCPReceiveBufferSizeHint,
    akkaListenerDispatcherPath: String = RedisConfigDefaults.IO.Akka.ListenerDispatcherPath,
    akkaIODispatcherPath: String = RedisConfigDefaults.IO.Akka.IODispatcherPath,
    akkaDecoderDispatcherPath: String = RedisConfigDefaults.IO.Akka.DecoderDispatcherPath,
    tryAgainWait: FiniteDuration = RedisConfigDefaults.IO.Cluster.TryAgainWait,
    clusterDownWait: FiniteDuration = RedisConfigDefaults.IO.Cluster.ClusterDownWait,
    systemOpt: Option[ActorSystem] = None,
    failCommandOnConnecting: Boolean = RedisConfigDefaults.Global.FailCommandOnConnecting,
    authOpt: Option[AuthConfig] = RedisConfigDefaults.Config.Redis.AuthOpt
  ) = new RedisJsonCluster(
    nodes = nodes,
    maxRetries = maxRetries,
    receiveTimeoutOpt = receiveTimeoutOpt,
    connectTimeout = connectTimeout,
    maxWriteBatchSize = maxWriteBatchSize,
    tcpSendBufferSizeHint = tcpReceiveBufferSizeHint,
    akkaListenerDispatcherPath = akkaListenerDispatcherPath,
    akkaIODispatcherPath = akkaIODispatcherPath,
    tryAgainWait = tryAgainWait,
    clusterDownWait = clusterDownWait,
    systemOpt = systemOpt,
    failCommandOnConnecting = failCommandOnConnecting,
    authOpt = authOpt
  )

  def apply(node: Server, nodes: Server*): RedisJsonCluster = RedisJsonCluster( nodes = node +: nodes)
  def apply(config: RedisConfig, systemOpt:Option[ActorSystem]): RedisJsonCluster = new RedisJsonCluster(config, systemOpt)
  def apply(config: Config):RedisJsonCluster = new RedisJsonCluster(RedisConfig(config), None)
  def apply(configName: String): RedisJsonCluster = new RedisJsonCluster(RedisConfig(configName), None)
  def apply(configName: String, path: String): RedisJsonCluster = new RedisJsonCluster(RedisConfig(configName, path), None)
}
