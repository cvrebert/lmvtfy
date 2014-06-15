package com.chrisrebert.lmvtfy.server

import akka.actor.ActorRef
import com.chrisrebert.lmvtfy.github.{IssueOrCommentEvent, GitHubIssue, GitHubUser}
import com.chrisrebert.lmvtfy.live_examples.{LiveExamplesExtractor, LiveExampleMention, LiveExample}

class IssueCommentEventHandler(fetcher: ActorRef) extends ActorWithLogging {
  implicit class RichLiveExamplesSet(examples: Set[LiveExample]) {
    def contextualize(user: GitHubUser, issue: GitHubIssue) = {
      examples.map{ LiveExampleMention(_, user, issue) }
    }
  }

  val settings = Settings(context.system)

  override def receive = {
    case event: IssueOrCommentEvent => {
      if (event.repository.fullName == settings.RepoFullName) {
        event.gitHubIssue.map { issue =>
          event.message.map { message =>
            if (message.user.username == settings.BotUsername) {
              log.info(s"Ignoring event about our own comment.")
            }
            else {
              val exampleMentions = LiveExamplesExtractor.liveExamplesFromWithin(message.body).contextualize(message.user, issue)
              for (mention <- exampleMentions) {
                log.info(s"Requesting fetch for ${mention}")
                fetcher ! mention
              }
            }
          }
        }
      }
      else {
        log.error(s"Received event from GitHub about irrelevant repository: ${event}")
      }
    }
  }
}
