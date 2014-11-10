package com.chrisrebert.lmvtfy.bootlint

import com.chrisrebert.lmvtfy.validation.SourceLocation

object MarkdownRenderer {
  implicit class MarkdownBootlintProblem(problem: BootlintProblem) {
    def markdown: String = {
      val location = problem.location.flatMap{ loc => SourceLocation(loc.line + 1, loc.column + 1) }
      val locationStr = location.map{ _.toString + ": " }.getOrElse("")
      s"${locationStr}[${problem.id}](${problem.explanationUrl}): ${problem.message}"
    }
  }

  def markdownFor(bootlintProblems: Seq[BootlintProblem]): String = bootlintProblems.map{ "* " + _.markdown }.mkString("\n")
}
