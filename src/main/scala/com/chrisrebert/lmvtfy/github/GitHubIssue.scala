package com.chrisrebert.lmvtfy.github

sealed case class GitHubIssue(repo: GitHubRepository, issueNum: IssueNumber)
