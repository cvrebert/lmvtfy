Hacking on LMVTFY
=================
## How do I build LMVTFY?
1. [Install sbt](http://www.scala-sbt.org/download.html)
2. Go to your `lmvtfy` directory.
3. Run `sbt compile`

## How do I run LMVTFY's unit test suite?
0. Ensure that sbt is installed (see above).
1. Go to your `lmvtfy` directory.
2. Run `sbt test`

## How do I generate a single self-sufficient JAR that includes all of the necessary dependencies?
0. Ensure that sbt is installed (see above).
1. Go to your `lmvtfy` directory.
2. Run `sbt assembly`
3. If the build is successful, the desired JAR will be generated as `target/scala-2.10/lmvtfy-assembly-1.0.jar`.
