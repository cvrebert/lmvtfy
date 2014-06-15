name := "lmvtfy"

version := "1.0"

scalaVersion := "2.10.3"

mainClass := Some("com.getbootstrap.lmvtfy.server.Boot")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

libraryDependencies += "org.eclipse.mylyn.github" % "org.eclipse.egit.github.core" % "2.1.5"

libraryDependencies += "com.twitter" % "twitter-text" % "1.9.1"

libraryDependencies ++= {
  val akkaV = "2.3.0"
  val sprayV = "1.3.1"
  Seq(
    "io.spray"            %   "spray-can"     % sprayV,
    "io.spray"            %   "spray-routing" % sprayV,
    "io.spray"            %   "spray-testkit" % sprayV  % "test",
    "io.spray"            %%  "spray-json"    % "1.2.6",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2"   % "2.3.12" % "test"
  )
}

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")

scalacOptions in Test ++= Seq("-Yrangepos")

// parallelExecution in Test := false

Revolver.settings
