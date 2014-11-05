package com.chrisrebert.lmvtfy.bootlint

object MarkdownRenderer {
  implicit class MarkdownBootlintProblem(problem: BootlintProblem) {
    def markdown: String = {
        s"[${problem.id}](${problem.explanationUrl}): ${problem.message}"
    }
  }

  def markdownFor(bootlintProblems: Seq[BootlintProblem]): String = bootlintProblems.map{ "* " + _.markdown }.mkString("\n")
}
