name := "capi-wpi-querybot"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.gu" %% "content-api-client" % "7.3",
  // Test dependencies
  "org.specs2" %% "specs2" % "2.3.12" % "test",
  //ok http dependencies
  "com.squareup.okhttp" % "okhttp" % "2.5.0",
  //play json dependencies
  "com.typesafe.play" % "play-json_2.11" % "2.4.0-M2",
  //play ws dependencies
  "com.typesafe.play" % "play-ws_2.11" % "2.4.5",
  //Taig communicator - wraps OkHttp fro Scala
  "io.taig" %% "communicator" % "2.2.2",
  //scalax.io
  "org.scalaforge" % "scalax" % "0.1",
  //aws S3 stuff
  "com.amazonaws" % "aws-java-sdk-s3" % "1.10.44",
  // typesafe config
  "com.typesafe" % "config" % "1.3.0"
)