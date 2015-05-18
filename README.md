LMVTFY: Let Me Validate That For You
======
[![Build Status](https://travis-ci.org/cvrebert/lmvtfy.svg?branch=master)](https://travis-ci.org/cvrebert/lmvtfy)
[![MIT License](https://img.shields.io/github/license/cvrebert/lmvtfy.svg)](https://github.com/cvrebert/lmvtfy/blob/master/LICENSE.txt)

LMVTFY is a service that watches for new issues and new issue comments on a given GitHub repository. If the comments contain (links to) Web examples (e.g. a [jsFiddle](http://jsfiddle.net)), the example's HTML is extracted and run thru [the HTML5 validator](http://validator.github.io). If there are any validation errors, LMVTFY then posts a comment ([such as this one](https://github.com/twbs/bootstrap/issues/11984#issuecomment-46140343)) on the issue pointing out these errors, so that the poster may correct them and/or realize the error of their ways.

Also, you can optionally enable [Bootlint](https://github.com/twbs/bootlint) integration, which will make LMVTFY run valid HTML documents through Bootlint (via its [HTTP API](https://github.com/twbs/bootlint#http-api)) to check for [Bootstrap](https://github.com/twbs/bootstrap) usage errors.

Affectionately named after [LMGTFY](http://knowyourmeme.com/memes/sites/let-me-google-that-for-you-lmgtfy).

## Supported example types
### Live
* [jsFiddle](http://jsfiddle.net)
* [JS Bin](http://jsbin.com)
* [Bootply](http://www.bootply.com)
* [Plunker](http://plnkr.co)
* [CodePen](http://codepen.io)
* [bl.ocks.org](http://bl.ocks.org)

### Non-live
* [GitHub Gist](https://gist.github.com)
* An HTML code block in the Markdown source of the issue comment [Planned]

## Motivation
You're a member of a popular open source project that involves front-end Web technologies. Cool.

But due to the project's popularity, you will get some issues reported by newbies who think that they're encountering some bug in your code, when in fact the problem is due to their invalid HTML. And sometimes the validity error is not obvious, so it won't occur to you to try checking their HTML's validity in the first place.

By automating the process of checking the validity of HTML examples, such issues can be resolved more quickly and with less work on the part of issue triagers.

## Used by
* [Bootstrap](https://github.com/twbs/bootstrap); see [@twbs-lmvtfy](https://github.com/twbs-lmvtfy)

## Usage
Java 7+ is required to run LMVTFY. For instructions on building LMVTFY yourself, see [the Contributing docs](https://github.com/cvrebert/lmvtfy/blob/master/CONTRIBUTING.md).

LMVTFY accepts exactly one optional command-line argument, which is the port number to run its HTTP server on, e.g. `8080`. If you don't provide this argument, the default port specified in `application.conf` will be used. Once you've built the JAR, run e.g. `java -jar lmvtfy-assembly-1.0.jar 8080` (replace `8080` with whatever port number you want). Note that running on ports <= 1024 requires root privileges (not recommended) or using port mapping.

Other settings live in `application.conf`. In addition to the normal Akka and Spray settings, LMVTFY offers the following settings:
```
lmvtfy {
    // Port to run on, if not specified via the command line
    default-port = 8080
    // Log the HTML being validated, for debugging purposes?
    debug-html = false
    // Suppress Spray's logging of malformed HTTP requests/headers?
    // (Enable this to avoid floods in your log output when your LMVTFY instance gets weird requests from crackers.)
    squelch-invalid-http-logging = true
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
    // Bootlint integration settings
    bootlint {
        // Enable Bootlint integration?
        enabled = true
        // Local port that Bootlint is running on
        port = 7070
    }
}
```

### GitHub webhook configuration
* Payload URL: `http://your-domain.example/lmvtfy`
* Content type: `application/json`
* Secret: Same as your `web-hook-secret-key` config value
* Which events would you like to trigger this webhook?: "Issues" and "Issue comment"

## Deliberately ignored errors
The following validation errors are deliberately ignored by LMVTFY for pragmatic reasons:
* "An `img` element must have an `alt` attribute, except under certain conditions. [...]"
  * This is an accessibility problem, but typically doesn't cause any other problems.
  * Most folks don't include `alt`s for `<img>`s in live examples since it's almost never relevant to their problem and thus not worth the extra work to add them.
* "Element `head` is missing a required instance of child element `title`."
  * Again, folks often don't include a `<title>` in live examples since its presence or absence is almost never relevant to their problem and its absence almost never causes any other problems.
* "Attribute `autocomplete` not allowed on element [`input` or `button`] at this point."
  * This nonstandard usage of the `autocomplete` attribute is currently the only good workaround for a [bizarre Firefox bug](https://bugzilla.mozilla.org/show_bug.cgi?id=654072).
* "Attribute `data-foo` not allowed on element `bar` at this point."
  * Typically the element is an SVG element. These HTML5 `data-*` attributes can be useful and in practice don't seem to cause any problems.
* Errors due to the nonstandard `<meta>` that jsFiddle uses on its pages.
  * "Bad value `edit-Type` for attribute `http-equiv` on element `meta`."
  * "Attribute `edit` not allowed on element `meta` at this point."
  * These aren't errors in the jsFiddle user's code, so nothing can be done about them and it's pointless to complain about them.
  * In practice, this nonstandard `<meta>` causes no problems.

## License
LMVTFY is released under the [MIT License](https://github.com/cvrebert/lmvtfy/blob/master/LICENSE.txt).

## Acknowledgments
We all stand on the shoulders of giants and get by with a little help from our friends. LMVTFY is written in [Scala](http://www.scala-lang.org) and built on top of:
* [validator.nu](https://github.com/validator/validator), the HTML5 validator
* [Akka](http://akka.io) & [Spray](http://spray.io), for async processing & HTTP
* [Eclipse EGit GitHub library](https://github.com/eclipse/egit-github), for working with [the GitHub API](https://developer.github.com/v3/)
* [twitter-text-java](https://github.com/twitter/twitter-text/tree/master/java), for extracting URLs from comment text

## See also
* [Rorschach](https://github.com/twbs/rorschach), LMVTFY's sister bot who sanity-checks Bootstrap pull requests
* [Savage](https://github.com/cvrebert/savage), LMVTFY's sister bot who runs cross-browser JS tests on Sauce Labs
* [NO CARRIER](https://github.com/twbs/no-carrier), LMVTFY's sister bot who closes old abandoned issues
