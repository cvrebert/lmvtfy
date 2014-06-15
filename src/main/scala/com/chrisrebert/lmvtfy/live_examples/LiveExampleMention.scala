package com.chrisrebert.lmvtfy.live_examples

import com.chrisrebert.lmvtfy.github.{GitHubIssue, GitHubUser}

sealed case class LiveExampleMention(example: LiveExample, user: GitHubUser, issue: GitHubIssue)
