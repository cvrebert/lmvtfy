package com.chrisrebert.lmvtfy.server

import scala.collection.JavaConversions._
import com.typesafe.config.Config
import akka.actor.ActorSystem
import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ExtendedActorSystem
import akka.util.ByteString
import com.jcabi.github.Github
import com.jcabi.github.Coordinates.{Simple=>RepoId}
import com.chrisrebert.lmvtfy.github.Credentials
import com.chrisrebert.lmvtfy.http.{UserAgent=>UA}
import com.chrisrebert.lmvtfy.util.Utf8String

class SettingsImpl(config: Config) extends Extension {
  val RepoIds: Set[RepoId] = config.getStringList("lmvtfy.github-repos-to-watch").toSet[String].map{ new RepoId(_) }
  val BotUsername: String = config.getString("lmvtfy.username")
  private val botPassword: String = config.getString("lmvtfy.password")
  private val botCredentials: Credentials = Credentials(username = BotUsername, password = botPassword)
  private val githubRateLimitThreshold: Int = config.getInt("lmvtfy.github-rate-limit-threshold")
  def github(): Github = botCredentials.github(githubRateLimitThreshold)(UserAgent)
  val WebHookSecretKey: ByteString = ByteString(config.getString("lmvtfy.web-hook-secret-key").utf8Bytes)
  val UserAgent: UA = UA(config.getString("spray.can.client.user-agent-header"))
  val DefaultPort: Int = config.getInt("lmvtfy.default-port")
  val SquelchInvalidHttpLogging: Boolean = config.getBoolean("lmvtfy.squelch-invalid-http-logging")
  val DebugHtml: Boolean = config.getBoolean("lmvtfy.debug-html")
  val EnableBootlint: Boolean = config.getBoolean("lmvtfy.bootlint.enabled")
  val BootlintPort: Int = config.getInt("lmvtfy.bootlint.port")
}
object Settings extends ExtensionId[SettingsImpl] with ExtensionIdProvider {
  override def lookup() = Settings
  override def createExtension(system: ExtendedActorSystem) = new SettingsImpl(system.settings.config)
  override def get(system: ActorSystem): SettingsImpl = super.get(system)
}
