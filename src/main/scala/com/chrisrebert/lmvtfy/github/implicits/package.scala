package com.chrisrebert.lmvtfy.github

import com.jcabi.github.Comment
import com.jcabi.github.Comment.{Smart=>SmartComment}

package object implicits {
  implicit class RichComment(comment: Comment) {
    def smart: SmartComment = new SmartComment(comment)
  }
}
