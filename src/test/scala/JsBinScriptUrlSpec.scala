import org.specs2.mutable._
import spray.http.Uri
import com.chrisrebert.lmvtfy.live_examples._

class JsBinScriptUrlSpec extends Specification {
  "JS Bin JavaScript src URL extractor" should {
    "successfully and correctly extract the JavaScript src URL from a realistic example" in {
      val binEditorHtml =
          """<!DOCTYPE html>
            |<html id="jsbin" lang="en" class="">
            |<head>
            |  <meta charset=utf-8>
            |  <title>JS Bin - Collaborative JavaScript Debugging</title>
            |  <link rel="alternate" type="application/json+oembed" href="http://jsbin.com/oembed?url=http://jsbin.com/zubera/edit?html,css,output">
            |  <link rel="icon" href="http://static.jsbin.com/images/favicon.png">
            |  <link rel="stylesheet" href="http://static.jsbin.com/css/style.css?3.35.0">
            |
            |  <link rel="stylesheet" href="http://static.jsbin.com/css/font.css?3.35.0">
            |  <meta http-equiv="X-UA-Compatible" content="IE=edge">
            |
            |
            |  <!--[if lte IE 9 ]><link rel="stylesheet" href="http://static.jsbin.com/css/ie.css?3.35.0"><![endif]-->
            |    <script>
            |    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
            |    (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            |    m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
            |    })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
            |    ga('create', 'UA-1656750-13', 'jsbin.com');
            |    ga('require', 'linkid', 'linkid.js');
            |    ga('require', 'displayfeatures');
            |    ga('send', 'pageview');
            |    ga('set', 'dimension1', '');
            |    </script>
            |  <meta name="description" content="A live pastebin for HTML, CSS & JavaScript and a range of processors, including SCSS, CoffeeScript, Jade and more..." />
            |  <meta property="og:url" content="http://jsbin.com" />
            |  <meta property="og:title" content="JS Bin" />
            |  <meta property="og:description" content="A live pastebin for HTML, CSS & JavaScript and a range of processors, including SCSS, CoffeeScript, Jade and more..." />
            |  <meta property="og:image" content="http://static.jsbin.com/images/logo.png" />
            |</head>
            |<!--[if lt IE 7]>  <body class="source ie ie6"> <![endif]-->
            |<!--[if IE 7]>     <body class="source ie ie7"> <![endif]-->
            |<!--[if gt IE 7]>  <body class="source ie">     <![endif]-->
            |<!--[if !IE]><!--> <body class="source public">    <!--<![endif]-->
            |<script>
            |if(top != self) {
            |  window.location = location.pathname.split('/').slice(0, -1).concat(['embed']).join('/') + (location.search ? location.search : '');
            |  document.write('<' + '!--');
            |}
            |</script>
            |<a href="#html" id="skipToEditor" class="visuallyhidden">Skip welcome & menu and move to editor</a>
            |  <div id="toppanel" class="toppanel-wrapper" style="display:none;">
            |    <div class="toppanel-column toppanel-column-first">
            |      <a href="#" class="toppanel-hide" title="Close">Close welcome panel</a>
            |      <a href="#" class="toppanel-logo">
            |        <img src="http://static.jsbin.com/images/dave.min.svg" alt="Welcome to JS Bin">
            |      </a>
            |      <div class="toppanel-actions toppanel-actions-alone">
            |        <div>
            |          <a id="createnew" href="http://jsbin.com" class="toppanel-button">New bin</a>
            |        </div>
            |        <div>
            |        </div>
            |      </div>
            |    </div>
            |    <div class="toppanel-column">
            |      <div><label id="enableUniversalEditorLabel" for="enableUniversalEditor"><input type="checkbox" id="enableUniversalEditor">Textarea editor mode</label></div>
            |      <h2 class="toppanel-title"><a href="http://jsbin.com/help/features" class="toppanel-link">JS Bin features</a></h2>
            |        <ul class="toppanel-list">
            |        <li><a href="http://jsbin.com/help/getting-started" class="toppanel-link">Getting started</a></li><li><a href="http://jsbin.com/help/keyboard-shortcuts" class="toppanel-link">Keyboard Shortcuts</a></li><li><a href="http://jsbin.com/help/export-gist" class="toppanel-link">Exporting/importing gist</a></li>
            |        </ul>
            |    </div>
            |
            |    <div class="toppanel-column">
            |      <h2 class="toppanel-title"><a href="/upgrade" class="toppanel-link">Pro features</a></h2>
            |        <ul class="toppanel-list">
            |        <li><a href="http://jsbin.com/help/private-bins" class="toppanel-link">Private bins</a></li><li><a href="http://jsbin.com/help/dropbox" class="toppanel-link">Dropbox backup</a></li><li><a href="http://jsbin.com/help/vanity-urls" class="toppanel-link">Vanity URLs</a></li>
            |        </ul>
            |    <div class="toppanel-section toppanel-love toppanel-no-pro">
            |      <center><a href="/upgrade">Upgrade to pro now</a></center>
            |    </div>
            |    </div>
            |
            |    <div class="toppanel-column toppanel-stretch">
            |      <div class="toppanel-blog toppanel-section">
            |        <h2 class="toppanel-title"><a href="http://jsbin.com/blog" class="toppanel-link">Blog</a></h2>
            |          <ul class="toppanel-list">
            |          <li><a href="http://jsbin.com/blog/twdtw-10-jshint-highlighting-pro" class="toppanel-link">JSHint, line highlighting and more</a></li>
            |          </ul>
            |      </div>
            |      <div class="toppanel-help toppanel-section">
            |        <h2 class="toppanel-title"><a href="http://jsbin.com/help" class="toppanel-link">Help</a></h2>
            |          <ul class="toppanel-list">
            |          <li><a href="http://jsbin.com/help/versions" class="toppanel-link">Versions: processors &amp; more</a></li><li><a href="http://jsbin.com/help/delete-a-bin" class="toppanel-link">Delete a bin</a></li>
            |          </ul>
            |      </div>
            |    </div>
            |
            |    <div class="toppanel-column">
            |        <div class="toppanel-section">
            |          <h2 class="toppanel-title"><a href="https://gratipay.com/jsbin" target="_blank" class="toppanel-link">Donate to JS Bin ❤</a></h2>
            |            <ul class="toppanel-list">
            |                <li><a href="https://gratipay.com/jsbin">Support JS Bin to keep the project open source & MIT for all</a></li>
            |                <li><a href="https://twitter.com/js_bin">Follow @js_bin on twitter</a></li>
            |            </ul>
            |        </div>
            |      <div class="toppanel-section toppanel-love haspro">
            |        <p><a target="_blank" href="http://jsbin.com/dave/last/edit">Dave</a> loves you <span class="love">&hearts;</span> because you're a Pro.</p>
            |      </div>
            |    </div>
            |
            |    <div class="toppanel-column">
            |      <div class="toppanel-section">
            |        <div class="toppanel-quote">&ldquo;Everyone should learn how to program a computer because it teaches you how to think&rdquo; &mdash; <span class="toppanel-quote-author">Steve Jobs</span></div>
            |      </div>
            |    </div>
            |  </div>
            |
            |  <script>
            |  //
            |  try {
            |    if (localStorage.settings === undefined || localStorage.settings.indexOf('"toppanel":false') === -1) {
            |      document.body.className += ' toppanel';
            |    }
            |  } catch (e) {
            |    // if there is no localStorage
            |  }
            |  </script><div id="control">
            |  <div class="control">
            |    <div id="menuinfo"><p></p></div>
            |    <div class="buttons">
            |        <div class="menu">
            |          <a href="http://jsbin.com/zubera/edit#file" target="_blank" class="brand button button-dropdown group button-dropdown-arrow"><img src="http://static.jsbin.com/images/favicon.svg"> File</a>
            |          <div class="dropdown" id="file">
            |            <div class="dropdownmenu">
            |              <a id="createnew" data-desc="Create a brand new bin" class="button group" title="Create fresh bin" href="http://jsbin.com" data-label="new">New</a>
            |              <a data-desc="Set this bin to private" data-pro="true" class="button group" title="Set this bin to private" href="http://jsbin.com/upgrade">Make bin private</a>
            |              <a class="deletebin button group" data-desc="Delete bin" title="Delete this bin" href="http://jsbin.com/delete" data-shortcut="ctrl+shift+del">Delete</a>
            |              <a class="archivebin button group" data-desc="Archive bin" title="Archive this bin" href="/zubera/archive" data-shortcut="ctrl+y">Archive</a>
            |              <a class="unarchivebin button group" data-desc="Unrchive bin" title="Restore this bin from the archive" href="/zubera/unarchive" data-shortcut="ctrl+y">Unarchive</a>
            |              <hr data-desc="">
            |              <a id="addmeta" data-desc="Insert a description shown in My Bins" title="Add meta data to bin" class="button group" href="#add-description">Add description</a>
            |              <a title="Save snapshot" data-desc="Save current work, and begin new revision on next change" data-shortcut="ctrl+s" class="button save group" data-label="save" href="http://jsbin.com/save">Save snapshot</a>
            |              <a data-desc="Copy and create a new bin start at revision #1" id="clone" title="Create a new copy" class="button clone group" data-label="clone" href="http://jsbin.com/clone">Clone</a>
            |              <hr data-desc="">
            |              <a data-pro="true" data-desc="Publish the current bin to http://jsbin.com" class="button group" title="Publish the current bin to http://jsbin.com" href="http://jsbin.com/upgrade">Publish to vanity homepage</a>
            |              <a data-desc="Export individual panels to Github's gist as an anonymous user" class="export-as-gist button group" title="Create a new anonymous GitHub Gist from this bin" href="#export-to-gist">Export as gist</a>
            |              <a data-desc="Download a complete html file for this bin" id="download" title="Save to local drive" class="button download group" href="http://jsbin.com/download" data-label="download">Download</a>
            |              <a data-desc="Use content from this bin when creating new bins" class="startingpoint button group" title="Set as starting code" href="http://jsbin.com/save" data-label="save-as-template">Save as template</a>
            |              <a hidden data-desc="How to embed a bin" target="_blank" title="How to embed a bin" data-label="how-to-embed" class="button group" href="http://jsbin.com/help/how-can-i-embed-jsbin">How to embed</a>
            |            </div>
            |          </div>
            |        </div><div class="menu">
            |          <span class="button group">Add library
            |          <select id="library"></select>
            |          </span>
            |        </div><div id="sharemenu" class="menu ">
            |          <a href="#share"  class="button button-dropdown group">Share</a>
            |          <div class="dropdown share-split" id="share">
            |            <div class="dropdowncontent">
            |              <form>
            |                <div class="controls">
            |                  <strong>State</strong>
            |                  <div id="sharebintype" data-desc="Streaming state of the bin when it's shared">
            |                    <ul>
            |                      <li><label><input type="radio" name="state" checked value="realtime"> Latest<span id="andlive"></span></label></li>
            |                      <li><label><input type="radio" name="state" class="lockrevision" value="snapshot"> Snapshot </label></li>
            |                    </ul>
            |                  </div>
            |                  <div id="sharepanels">
            |                    <strong>View</strong>
            |                    <label><input checked type="radio" name="view" value="editor"> Editor<span class="codecasting"><br><small>with codecasting</small></span></label>
            |                    <ul>
            |                      <li><label><input name="panel" type="checkbox" value="html">HTML</label></li>
            |                      <li><label><input name="panel" type="checkbox" value="css">CSS</label></li>
            |                      <li><label><input name="panel" type="checkbox" value="js">JS</label></li>
            |                      <li><label><input name="panel" type="checkbox" value="console">Console</label></li>
            |                      <li><label><input name="panel" type="checkbox" value="output">Output</label></li>
            |                    </ul>
            |                  </div>
            |                  <label for="output-view"><input id="output-view" type="radio" name="view" value="output"> Output only<span class="codecasting"><br><small>with live reload</small></span></label>
            |                </div>
            |                <div class="result">
            |                  <div data-desc="This bin's full output without the JS Bin editor">
            |                    <strong><a class="link" data-path="/" target="_blank" href="http://jsbin.com/zubera">Link</a></strong><br>
            |                    <input name="url" class="link" value="http://jsbin.com/zubera/edit" type="text">
            |                  </div>
            |                  <div data-desc="Direct links to specific sources of the bin">
            |                    <span class="direct-links"></span>
            |                  </div>
            |                  <div data-desc="Embed this bin with the live output on your site">
            |                    <span class="heading">Embed</span><br><textarea id="embedfield" name="embed" data-path="/embed" class="link">&lt;a class=&quot;jsbin-embed&quot; href=&quot;http://jsbin.com/zubera/embed?live&quot;&gt;JS Bin demo&lt;/a&gt;&lt;script src=&quot;http://static.jsbin.com/js/embed.js&quot;&gt;&lt;/script&gt;</textarea>
            |                  </div>
            |                  <div data-desc="The preview of JS Bin when you share your bin">
            |                    <span class="heading">What they'll see</span><br>
            |                    <div id="share-preview">
            |                      <div class="output"></div>
            |                      <span class="header"></span>
            |                      <div class="editor">
            |                        <div class="html">
            |                        </div>
            |                        <div class="css">
            |                        </div>
            |                        <div class="js">
            |                        </div>
            |                        <div class="console">
            |                        </div>
            |                        <div class="output">
            |                        </div>
            |                      </div>
            |                    </div>
            |                  </div>
            |                </div>
            |                </div>
            |              </form>
            |            </div>
            |
            |          </div>
            |        </div>
            |        <div id="start-saving" class="menu">
            |          <a href="/zubera/save" class="save button group">Start saving your work</a>
            |        </div>
            |      <div id="panels"></div>
            |      <div class="help">
            |          <div class="loggedout menu">
            |            <a href="http://jsbin.com/login" class="button button-dropdown focusbtn" id="loginbtn">Login or Register</a>
            |            <div class="dropdown login" id="registerLogin">
            |              <div class="dropdowncontent">
            |                <a class="btn-github" href="http://jsbin.com/auth/github">
            |                  <img src="http://static.jsbin.com/images/github-32.png">
            |                  Login or Register via GitHub
            |                </a>
            |                <span class="login-group">
            |                  Or
            |                  <a class="btn-login" href="http://jsbin.com/login">use your email address</a>
            |                </span>
            |              </div>
            |            </div>
            |          </div>
            |          <div class="loggedin menu">
            |          </div>
            |
            |        <div class="menu blog">
            |          <a href="http://jsbin.com/blog" class="button">Blog</a>
            |        </div>
            |        <div class="menu">
            |          <a href="#help" class="button button-dropdown">Help</a>
            |          <div class="dropdown dd-right" id="help">
            |            <div class="dropdownmenu">
            |              <a data-shortcut="ctrl+shift+?" data-desc="Discover poweruser keyboard shortcuts" id="showhelp" href="#keyboardHelp">Keyboard shortcuts</a>
            |              <a data-desc="Shortcut & direct access JS Bin URLs" id="showurls" href="#urls">JS Bin URLs</a>
            |              <hr data-desc="">
            |              <input placeholder="Search help..." class="button" id="helpsearch"><span id="result-count"></span><span id="results"></span>
            |              <a id="menu-all-help" data-desc="Learn about JS Bin features & tricks" target="_blank" href="http://jsbin.com/help">All help topics</a>
            |              <hr data-desc="">
            |              <a id="menu-feedback" data-desc="Help make JS Bin better" id="newissue" target="_blank" href="http://github.com/jsbin/jsbin/issues/new">Send feedback &amp; file bugs</a>
            |              <a id="menu-fork" data-desc="Help make JS Bin better" target="_blank" href="https://gratipay.com/jsbin/">Donate on Gratipay</a>
            |              <a id="menu-follow-jsbin" data-desc="Find out the latest news & info" target="_blank" href="http://twitter.com/js_bin">Follow @js_bin</a>
            |              <a class="" data-pro="true" id="menu-pro" data-desc="Upgrade" href="/upgrade">Support JS Bin: upgrade now</a>
            |            </div>
            |          </div>
            |        </div>
            |      </div>
            |    </div>
            |  </div>
            |</div><script type="template/text" id="profile-template">
            |  <a title="@{name}" href="#profile" class="button button-dropdown avatar" id="accountBtn"><picture>
            |    <source srcset="{avatar} 1x, {avatar}?&s=58 2x">
            |    <img src="{avatar}">
            |  </picture> Account</a>
            |  <div class="dropdown dd-right" id="profile">
            |    <div class="dropdowncontent">
            |      <div class="large-gravatar"><object data="http://static.jsbin.com/images/default-avatar.min.svg?{name}&3.35.0" type="image/svg+xml">
            |    <img src="http://static.jsbin.com/images/default-avatar.min.svg?3.35.0"></object><picture class="large-gravatar">
            |    <source srcset="{large_avatar} 1x, {large_avatar}?&s=240 2x">
            |    <img class="large-gravatar" src="{large_avatar}">
            |  </picture></div>
            |      <span class="input" id="username" readonly>
            |        @{name}<sup class="pro">pro</sup>
            |      </span>
            |
            |      <ul>
            |        <li><a href="http://jsbin.com/account/profile">Profile</a></li>
            |        <li><a href="http://jsbin.com/account/editor">Editor settings</a></li>
            |        <li><a href="http://jsbin.com/account/preferences">Preferences</a></li>
            |        <li><a href="http://jsbin.com/logout">Logout</a></li>
            |      </ul>
            |
            |    </div>
            |    <a href="http://jsbin.com/upgrade" class="gopro">Support JS Bin: upgrade to PRO</a>
            |  </div>
            |</script>
            |<div id="bin" class="stretch" style="opacity: 0; filter:alpha(opacity=0);">
            |  <div id="source" class="binview stretch">
            |  </div>
            |  <div id="panelswaiting">
            |    <div class="code stretch html panel">
            |      <div class="label menu"><span class="name"><strong><a href="#htmlprocessors" class="fake-dropdown button-dropdown">HTML</a></strong></span><div class="dropdown" id="htmlprocessors">
            |          <div class="dropdownmenu processorSelector" data-type="html">
            |            <a href="#html">HTML</a>
            |            <a href="#markdown">Markdown</a>
            |            <a href="#jade">Jade</a>
            |            <a href="#convert">Convert to HTML</a>
            |          </div>
            |        </div>
            |      </div>
            |      <div class="editbox">
            |        <textarea aria-label="HTML Code Panel" spellcheck="false" autocapitalize="none" autocorrect="off" id="html"></textarea>
            |      </div>
            |    </div>
            |    <div class="code stretch javascript panel">
            |      <div class="label menu"><span class="name"><strong><a  class="fake-dropdown button-dropdown" href="#javascriptprocessors">JavaScript</a></strong></span>
            |        <div class="dropdown" id="javascriptprocessors">
            |          <div class="dropdownmenu processorSelector" data-type="javascript">
            |            <a href="#javascript">JavaScript</a>
            |            <a href="#babel">ES6 / Babel</a>
            |            <a href="#jsx">JSX (React)</a>
            |            <a href="#coffeescript">CoffeeScript</a>
            |            <a href="#traceur">Traceur</a>
            |            <a href="#typescript">TypeScript</a>
            |            <a href="#processing">Processing</a>
            |            <a href="#livescript">LiveScript</a>
            |            <a href="#clojurescript">ClojureScript</a>
            |            <a href="#convert">Convert to JavaScript</a>
            |          </div>
            |        </div>
            |      </div>
            |      <div class="editbox">
            |        <textarea aria-label="JavaScript Code Panel" spellcheck="false" autocapitalize="none" autocorrect="off" id="javascript"></textarea>
            |      </div>
            |    </div>
            |    <div class="code stretch css panel">
            |      <div class="label menu"><span class="name"><strong><a class="fake-dropdown button-dropdown" href="#cssprocessors">CSS</a></strong></span>
            |        <div class="dropdown" id="cssprocessors">
            |          <div class="dropdownmenu processorSelector" data-type="css">
            |            <a href="#css">CSS</a>
            |            <a href="#less">Less</a>
            |            <a href="#myth">Myth</a>
            |            <a href="#sass" data-label="Sass">Sass <span class="small">with Compass</span></a>
            |            <a href="#scss" data-label="SCSS">SCSS <span class="small">with Compass</span></a>
            |            <a href="#stylus">Stylus</a>
            |            <a href="#convert">Convert to CSS</a>
            |          </div>
            |        </div>
            |      </div>
            |      <div class="editbox">
            |        <textarea aria-label="CSS Code Panel" spellcheck="false" autocapitalize="none" autocorrect="off" id="css"></textarea>
            |      </div>
            |    </div>
            |    <div class="stretch console panel">
            |      <div class="label">
            |        <span class="name"><strong>Console</strong></span>
            |        <span class="options">
            |          <button id="runconsole" title="ctrl + enter">Run</button>
            |          <button id="clearconsole" title="ctrl + l">Clear</button>
            |        </span>
            |      </div>
            |      <div id="console" class="stretch"><ul id="output"></ul><form>
            |          <textarea aria-label="Console Panel" id="exec" spellcheck="false" autocapitalize="none" rows="1" autocorrect="off"></textarea>
            |      </form></div>
            |    </div>
            |    <div id="live" class="stretch live panel">
            |      <div class="label">
            |        <span class="name"><strong>Output</strong></span>
            |        <span class="options">
            |          <button id="runwithalerts" title="ctrl + enter
            |Include alerts, prompts &amp; confirm boxes">Run with JS</button>
            |          <label>Auto-run JS<input type="checkbox" id="enablejs"></label>
            |          <a target="_blank" title="Live preview" id="jsbinurl" class="" href="/zubera"><img src="http://static.jsbin.com/images/popout.png"></a>
            |        </span>
            |        <span class="size"></span>
            |      </div>
            |    </div>
            |  </div>
            |  <form id="saveform" method="post" action="/zubera/save">
            |    <input type="hidden" name="method">
            |    <input type="hidden" name="_csrf" value="aBZcNa7Pr9mPgd+oohJ1dOaM">
            |  </form>
            |</div>
            |<div id="tip" class=" notification">
            |  <p>
            |
            |    You can jump to the latest bin by adding <code>/latest</code> to your URL
            |  </p>
            |  <a class="dismiss" href="#">Dismiss x</a>
            |</div>
            |<div id="keyboardHelp" class="modal">
            |  <div>
            |    <h2>Keyboard Shortcuts</h2>
            |    <table>
            |      <thead>
            |        <tr>
            |          <th class="shortcut">Shortcut</th>
            |          <th>Action</th>
            |        </tr>
            |      </thead>
            |      <tbody>
            |        <tr>
            |          <td>ctrl + [num]</td>
            |          <td>Toggle nth panel</td>
            |        </tr>
            |        <tr>
            |          <td colspan="2">
            |            <small>
            |              <input type="checkbox" id="enablealt" class="enablealt">
            |              <label for="enablealt">Require alt key, leaving cmd+1, 2 etc for tab switching.</label>
            |            </small>
            |          </td>
            |        </tr>
            |        <tr>
            |          <td>ctrl + 0</td>
            |          <td>Close focused panel</td>
            |        </tr>
            |        <tr>
            |          <td>ctrl + enter</td>
            |          <td>Re-render output.<br>If console visible: run JS in console</td>
            |        </tr>
            |        <tr>
            |          <td>Ctrl + l</td>
            |          <td>Clear the console</td>
            |        </tr>
            |        <tr>
            |          <td>ctrl + /</td>
            |          <td>Toggle comment on selected lines</td>
            |        </tr>
            |        <tr>
            |          <td>ctrl + [</td>
            |          <td>Indents selected lines</td>
            |        </tr>
            |        <tr>
            |          <td>ctrl + ]</td>
            |          <td>Unindents selected lines</td>
            |        </tr>
            |        <tr>
            |          <td>tab</td>
            |          <td>Code complete &amp; <a href="http://docs.emmet.io/" target="_blank">Emmet</a> expand</td>
            |        </tr>
            |        <tr>
            |          <td>ctrl + shift + L</td>
            |          <td>Beautify code in active panel</td>
            |        </tr>
            |        <tr>
            |          <td>ctrl + s</td>
            |          <td>Save &amp; lock current Bin from further changes</td>
            |        </tr>
            |        <tr>
            |          <td>ctrl + shift + s</td>
            |          <td>Open the share options</td>
            |        </tr>
            |        <tr>
            |          <td>ctrl + y</td>
            |          <td>Archive Bin</td>
            |        </tr>
            |        <tr><td colspan="2"><small><br><a href="http://jsbin.com/help/keyboard-shortcuts" target="_blank">Complete list of JS Bin shortcuts</a></small></td></tr>
            |      </tbody>
            |    </table>
            |  </div>
            |</div>
            |<div id="urlHelp" class="modal">
            |  <div>
            |    <h2>JS Bin URLs</h2>
            |    <table>
            |      <thead>
            |        <tr>
            |          <th class="shortcut">URL</th>
            |          <th>Action</th>
            |        </tr>
            |      </thead>
            |      <tbody>
            |        <tr>
            |          <td>/</td>
            |          <td>Show the full rendered output.<br><small>This content will update in real time as it's updated from the /edit url.</small></td>
            |        </tr>
            |        <tr>
            |          <td>/edit</td>
            |          <td>Edit the current bin</td>
            |        </tr>
            |        <tr>
            |          <td>/watch</td>
            |          <td>Follow a Code Casting session</td>
            |        </tr>
            |        <tr>
            |          <td>/embed</td>
            |          <td>Create an embeddable version of the bin</td>
            |        </tr>
            |        <tr>
            |          <td>/latest</td>
            |          <td>Load the very latest bin (/latest goes in place of the revision)</td>
            |        </tr>
            |        <tr>
            |          <td>/[username]/last</td>
            |          <td>View the last edited bin for this user</td>
            |        </tr>
            |        <tr>
            |          <td>/[username]/last/edit</td>
            |          <td>Edit the last edited bin for this user</td>
            |        </tr>
            |        <tr>
            |          <td>/[username]/last/watch</td>
            |          <td>Follow the Code Casting session for the latest bin for this user</td>
            |        </tr>
            |        <tr>
            |          <td>/quiet</td>
            |          <td>Remove analytics and edit button from rendered output</td>
            |        </tr>
            |        <tr>
            |          <td>.js</td>
            |          <td>Load only the JavaScript for a bin</td>
            |        </tr>
            |        <tr>
            |          <td>.css</td>
            |          <td>Load only the CSS for a bin</td>
            |        </tr>
            |        <tr><td colspan="2"><br><small>Except for username prefixed urls, the url may start with http://jsbin.com/abc and the url fragments can be added to the url to view it differently.</small></td></tr>
            |      </tbody>
            |    </table>
            |  </div>
            |</div>
            |<div hidden>
            |  <div class="card" id="infocard">
            |    <div class="body">
            |      <ul class="controls">
            |        <li><a class="transfer" href="#transfer">Transfer</a> </li>
            |        <li><a href="/clone">Clone</a> </li>
            |        <li><a class="startingpoint" href="/save-as-template">Save as template</a> </li>
            |        <li><a class="export-as-gist" href="#export-gist">Export gist</a> </li>
            |        <li class="owner"><a href="/download">Download</a> </li>
            |        <li class="owner"><a class="deletebin" href="/delete">Delete</a></li>
            |      </ul>
            |    </div>
            |    <header><img>
            |      <div class="visibility"></div>
            |      <div class="meta">
            |        <div class="author">Bin info</div>
            |        <div class="name"><b></b><span class="pro">pro</span></div><span class="when"><canvas></canvas><time></time></span>
            |      </div>
            |      <div class="viewers"><b>0</b><span>viewers</span></div>
            |    </header>
            |  </div>
            |</div>
            |
            |<script src="http://static.jsbin.com/js/vendor/jquery-1.11.0.min.js"></script>
            |<script src="http://jsbin.com/bin/user.js?f936"></script>
            |<script src="http://static.jsbin.com/js/prod/jsbin-3.35.0.min.js"></script>
            |<script src="http://jsbin.com/bin/start.js?7d56"></script>
            |</body>
            |</html>
            |
            |
          """.stripMargin
      val expectedScriptUrl = Uri("http://jsbin.com/bin/start.js?7d56")
      val scriptUrlOption = binEditorHtml match {
        case JsBinScriptUrl(scriptUrl) => Some(scriptUrl)
        case _ => None
      }

      scriptUrlOption must beSome(expectedScriptUrl)
    }
  }
}
