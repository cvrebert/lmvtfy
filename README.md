LMVTFY: Let Me Validate That For You
======

LMVTFY is a service that watches for new issues and new issue comments on a given GitHub repository. If the comments contain (links to) live Web examples (e.g. a [jsFiddle](http://jsfiddle.net)), the example's HTML is extracted and run thru [the HTML5 validator](http://validator.github.io). If there are any validation errors, LMVTFY then posts a comment ([such as this one](https://github.com/twbs/bootstrap/issues/11984#issuecomment-46140343)) on the issue pointing out these errors, so that the poster may correct them and/or realize the error of their ways.

Affectionately named after [LMGTFY](http://knowyourmeme.com/memes/sites/let-me-google-that-for-you-lmgtfy).

## Supported live example types
* [jsFiddle](http://jsfiddle.net)
* [JS Bin](http://jsbin.com)
* [Bootply](http://www.bootply.com)
* [Plunker](http://plnkr.co)
* [CodePen](http://codepen.io)
* An HTML code block in the Markdown source of the issue comment [Planned]

## Motivation
You're a member of a popular open source project that involves Web technologies. Cool.

But due to the project's popularity, you will get some issues reported by newbies who think that they're encountering some bug in your code, when in fact the problem is due to their invalid HTML. And sometimes the validity error is not obvious, so it won't occur to you to try checking their HTML's validity in the first place.

By automating the process of checking the validity of HTML examples, such issues can be resolved more quickly and with less work on the part of issue triagers.

## Used by
* [Bootstrap](https://github.com/twbs/bootstrap); see [@twbs-lmvtfy](https://github.com/twbs-lmvtfy)
