import Dependencies.*

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.17"

lazy val root = (project in file("."))
  .aggregate(producer, processor)
  .settings(
    name := "e-comm-analytics"
  )

lazy val common = (project in file("common"))
  .settings(
    name := "e-comm-analytics-common",
    libraryDependencies ++= Seq(
      Libraries.catsEffect,
      Libraries.pureConfig,
      Libraries.circeCore,
      Libraries.circeGeneric,
      Libraries.circeGenericExt,
      Libraries.circeParser,
      Libraries.log4j,
      Libraries.logback,
      Libraries.otelCore,
      Libraries.otelJava,
      Libraries.otelContextStorage,
      Libraries.openTel,
      Libraries.openAutoConfig,
      Libraries.scalaTest      % Test,
      Libraries.scalaCheck     % Test,
      Libraries.catsScalaCheck % Test
    )
  )

lazy val producer = (project in file("producer"))
  .dependsOn(common)
  .settings(
    name := "e-comm-analytics-producer",
    libraryDependencies ++= Seq(
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.fs2,
      Libraries.fs2Kafka,
      Libraries.http4sDsl,
      Libraries.http4sServer,
      Libraries.http4sClient,
      Libraries.http4sCirce,
      Libraries.circeCore,
      Libraries.circeGeneric,
      Libraries.circeGenericExt,
      Libraries.circeParser,
      Libraries.pureConfig,
      Libraries.logback,
      Libraries.scalaTest      % Test,
      Libraries.scalaCheck     % Test,
      Libraries.catsScalaCheck % Test
    )
  )

lazy val processor = (project in file("processor"))
  .dependsOn(common)
  .settings(
    name := "e-comm-analytics-processor",
    libraryDependencies ++= Seq(
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.fs2,
      Libraries.fs2Kafka,
      Libraries.http4sDsl,
      Libraries.http4sServer,
      Libraries.http4sClient,
      Libraries.http4sCirce,
      Libraries.circeCore,
      Libraries.circeGeneric,
      Libraries.circeGenericExt,
      Libraries.circeParser,
      Libraries.pureConfig,
      Libraries.logback,
      Libraries.scalaTest      % Test,
      Libraries.scalaCheck     % Test,
      Libraries.catsScalaCheck % Test
    )
  )

javaOptions += "-Dotel.java.global-autoconfigure.enabled=true"
javaOptions += "-Dcats.effect.trackFiberContext=true"
