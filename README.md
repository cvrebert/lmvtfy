LMVTFY: Let Me Validate That For You
======

LMVTFY is a service that watches for new issues and new issue comments on a given GitHub repository. If the comments contain live Web examples (e.g. a [jsFiddle](http://jsfiddle.net) or [JS Bin](http://jsbin.com)), the example's HTML is extracted and run thru [the HTML5 validator](http://validator.github.io). If there are any validation errors, LMVTFY then posts a comment on the issue pointing out these errors, so that the poster may correct them and/or realize the error of their ways.

## Supported live example types
* [jsFiddle](http://jsfiddle.net)
* [JS Bin](http://jsbin.com)
* An HTML code block in the Markdown source of the issue comment

## Motivation
You're a member of a popular open source project that involves Web technologies. Cool.

But due to the project's popularity, you will get some issues reported by newbies who think that they're encountering some bug in your code, when in fact the problem is due to their invalid HTML. And sometimes the validity error is not obvious, so it won't occur to you to try checking their HTML's validity in the first place.

By automating the process of checking the validity of HTML examples, such issues can be resolved more quickly and with less work on the part of issue triagers.

## Used by
* In the future, this will hopefully be used by Bootstrap.
