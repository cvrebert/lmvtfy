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

## How do I run the LMVTFY service locally for test purposes?
**This method is not recommended for use in production deployments!**

0. If Bootlint integration is enabled, start Bootlint's server.
1. Ensure that sbt is installed (see above).
2. Go to your `lmvtfy` directory.
3. Run `sbt`
4. At the sbt prompt, enter `re-start 8080` (replace `8080` with whatever port you want the HTTP server to run on) or `re-start` (which will use the default port specified in `application.conf`). Note that running on ports <= 1024 requires root privileges (not recommended) or using port mapping.

## How do I generate a single self-sufficient JAR that includes all of the necessary dependencies?
0. Ensure that sbt is installed (see above).
1. Go to your `lmvtfy` directory.
2. Run `sbt assembly`
3. If the build is successful, the desired JAR will be generated as `target/scala-2.10/lmvtfy-assembly-1.0.jar`.

## Licensing
LMVTFY is licensed under The MIT License. By contributing to LMVTFY, you agree to license your contribution under [The MIT License](https://github.com/cvrebert/lmvtfy/blob/master/LICENSE.txt).
