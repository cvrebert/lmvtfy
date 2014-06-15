package com.chrisrebert.lmvtfy.github

import scala.util.Try
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.IssueService
import org.eclipse.egit.github.core.RepositoryId
import com.chrisrebert.lmvtfy.ValidationResult
import com.chrisrebert.lmvtfy.live_examples.{LiveExampleMention, LiveExample}
import com.chrisrebert.lmvtfy.server.ActorWithLogging


class GitHubIssueCommenter extends ActorWithLogging {
  private val someRepo = RepositoryId.create("cvrebert", "lmvtfy-test")
  private val client = {
    val c = new GitHubClient()
    c.setCredentials("user", "passw0rd")
    // client.setOAuth2Token("SlAV32hkKG")
    c
  }

  private def tryToCommentOn(repo: RepositoryId, issue: IssueNumber, commentMarkdown: String) = {
    val issueService = new IssueService(client)
    Try { issueService.createComment(repo, issue.number, commentMarkdown) }
  }

  override def receive = {
    case ValidationResult(messagesMarkdown, mention@LiveExampleMention(example: LiveExample, GitHubUser(username), GitHubIssue(repo, issue))) => {
      val exampleUrl = example.url
      val commentMarkdown = s"""
        |Hi @${username}!
        |
        |You seem to have posted a live example (${exampleUrl}), which is always a good first step.
        |However, according to [the HTML5 validator](http://validator.nu), **your example has some validation errors**, which might be causing your issue:
        |${messagesMarkdown}
        |
        |You'll need to **fix these errors** and post a revised example before we can proceed further.
        |Thanks!
        |
        |(*Please note that this is a fully automated comment.*)
      """.stripMargin

      log.info(s"Posting comment for ${mention}")
      val commentTry = tryToCommentOn(repo.id, issue, commentMarkdown)
      log.info(s"Result of comment attempt:\n${commentTry}")
    }
  }
}
