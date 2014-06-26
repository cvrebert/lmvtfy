LMVTFY: Let Me Validate That For You
======
[![Build Status](https://travis-ci.org/cvrebert/lmvtfy.svg?branch=master)](https://travis-ci.org/cvrebert/lmvtfy)

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

## Usage
Java 7+ is required to run LMVTFY. For instructions on building LMVTFY yourself, see [the Contributing docs](https://github.com/cvrebert/lmvtfy/blob/master/CONTRIBUTING.md).

LMVTFY requires and accepts exactly one command-line argument, which is the port number to run its HTTP server on, e.g. `8080`. Once you've built the JAR, run `java -jar lmvtfy-assembly-1.0.jar 8080` (replace `8080` with whatever port number you want).

Other settings live in `application.conf`. In addition to the normal Akka and Spray settings, LMVTFY offers the following settings:
```
lmvtfy {
    // List of full names of GitHub repos to watch for new issues and new issue comments
    github-repos-to-watch = ["twbs/bootstrap"]
    // Username of the account that reply comments will be posted from
    username = "twbs-lmvtfy"
    // Password for the account that reply comments will be posted from
    password = "not-actually-the-password"
    // This goes in the "Secret" field when setting up the Webhook
    // in the "Webhooks & Services" part of your repo's Settings.
    // This string will be converted to UTF-8 for the HMAC-SHA1 computation.
    // The HMAC is used to verify that LMVTFY is really being contacted by GitHub,
    // and not by some random hacker.
    web-hook-secret-key = "some-random-gibberish-here"
}
```

### GitHub webhook configuration
* Payload URL: `http://your-domain.example/lmvtfy`
* Content type: `application/json`
* Secret: Same as your `web-hook-secret-key` config value
* Which events would you like to trigger this webhook?: "Issues" and "Issue comment"

## Acknowledgements
We all stand on the shoulders of giants and get by with a little help from our friends. LMVTFY is written in [Scala](http://www.scala-lang.org) and built on top of:
* [validator.nu](https://github.com/validator/validator), the HTML5 validator
* [Akka](http://akka.io) & [Spray](http://spray.io), for async processing & HTTP
* [Eclipse EGit GitHub library](https://github.com/eclipse/egit-github), for working with [the GitHub API](https://developer.github.com/v3/)
* [twitter-text-java](https://github.com/twitter/twitter-text-java), for extracting URLs from comment text
