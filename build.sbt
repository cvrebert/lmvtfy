name := "lmvtfy"

version := "1.0"

scalaVersion := "2.11.5"

mainClass := Some("com.chrisrebert.lmvtfy.server.Boot")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

val jcabiV = "0.24"

libraryDependencies += "nu.validator" % "validator" % "16.1.1" excludeAll(
  ExclusionRule(organization = "org.eclipse.jetty"),
  ExclusionRule(organization = "javax.servlet"),
  ExclusionRule(organization = "commons-fileupload"),
  ExclusionRule(organization = "commons-httpclient")
)

libraryDependencies += "com.jcabi" % "jcabi-github" % jcabiV

libraryDependencies += "com.twitter" % "twitter-text" % "1.12.1"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "io.spray"            %%  "spray-json"    % "1.3.1",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-slf4j"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2"        % "2.3.12" % "test"
  )
}

packageOptions in (Compile, packageBin) ++= {
  Seq(
    Package.ManifestAttributes("JCabi-Version" -> jcabiV),
    Package.ManifestAttributes("JCabi-Build" -> "abcdef"),
    Package.ManifestAttributes("JCabi-Date" -> "2015-08-01")
  )
}

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "–Xlint", "-encoding", "utf8")

scalacOptions in Test ++= Seq("-Yrangepos")

// parallelExecution in Test := false

Revolver.settings
