package com.chrisrebert.lmvtfy.github

import scala.util.{Try,Failure,Success}
import com.jcabi.github.Coordinates.{Simple=>RepoId}
import com.chrisrebert.lmvtfy.{MarkdownAboutHtml, MarkdownAboutBootstrap, Markdown, ValidationResult}
import com.chrisrebert.lmvtfy.live_examples.{LiveExampleMention, LiveExample}
import com.chrisrebert.lmvtfy.github.implicits._
import com.chrisrebert.lmvtfy.server.{ActorWithLogging, Settings}


class GitHubIssueCommenter extends ActorWithLogging {
  val settings = Settings(context.system)

  private val client = settings.github()

  private def tryToCommentOn(repo: RepoId, issue: IssueNumber, commentMarkdown: String) = {
    Try { client.repos.get(repo).issues.get(issue.number).comments.post(commentMarkdown) }
  }

  private def introFor(markdown: Markdown): String = {
    markdown match {
      case _:MarkdownAboutHtml => "However, according to [the HTML5 validator](http://html5.validator.nu), **your example has some validation errors**"
      case _:MarkdownAboutBootstrap => "However, according to [Bootlint](https://github.com/twbs/bootlint), **your example has some Bootstrap usage errors**"
    }
  }

  override def receive = {
    case ValidationResult(messagesMarkdown, mention@LiveExampleMention(example: LiveExample, GitHubUser(username), GitHubIssue(repo, issue))) => {
      val exampleUrl = example.displayUrl
      val intro = introFor(messagesMarkdown)
      val commentMarkdown = s"""
        |Hi @${username}!
        |
        |You appear to have posted a live example (${exampleUrl}), which is always a good first step. ${intro}, which might potentially be causing your issue:
        |${messagesMarkdown.markdown}
        |
        |You'll need to **fix these errors** and post a revised example before we can proceed further.
        |Thanks!
        |
        |(*Please note that this is a [fully automated](https://github.com/cvrebert/lmvtfy) comment.*)
      """.stripMargin

      tryToCommentOn(repo.id, issue, commentMarkdown) match {
        case Success(comment) => log.info(s"Successfully posted comment ${comment.smart.url} for ${mention}")
        case Failure(exc) => log.error(exc, s"Error posting comment for ${mention}")
      }
    }
  }
}
