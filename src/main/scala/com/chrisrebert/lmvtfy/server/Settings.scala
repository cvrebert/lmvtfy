package com.chrisrebert.lmvtfy.server

import akka.actor.ActorSystem
import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ExtendedActorSystem
import com.typesafe.config.Config

class SettingsImpl(config: Config) extends Extension {
  val RepoFullName: String = config.getString("lmvtfy.github-repo-to-watch")
  val BotUsername: String = config.getString("lmvtfy.username")
}
object Settings extends ExtensionId[SettingsImpl] with ExtensionIdProvider {
  override def lookup() = Settings
  override def createExtension(system: ExtendedActorSystem) = new SettingsImpl(system.settings.config)
  override def get(system: ActorSystem): SettingsImpl = super.get(system)
}