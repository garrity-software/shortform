import java.time.LocalDate
val scala3: String = "3.3.1"

ThisBuild / organizationName     := "garrity software"
ThisBuild / organization         := "gs"
ThisBuild / organizationHomepage := Some(url("https://garrity.co/"))
ThisBuild / scalaVersion         := scala3

//externalResolvers := Seq(
//  "Garrity Software Releases" at "https://artifacts.garrity.co/releases"
//)

val ProjectName: String = "shortform"
val Description: String = "Presentation and discussion platform."

// Helper for getting properties from `-Dprop=value`.
def getProp[A](
  name: String,
  conv: String => A
): Option[A] =
  Option(System.getProperty(name)).map(conv)

// Use `sbt -Dversion=<version>` to provide the version, minus the SNAPSHOT
// modifier. This is the typical approach for producing releases.
val VersionProperty: String = "version"

def getVersion(): Option[String] =
  getProp(VersionProperty, identity)

// Use `sbt -Drelease=true` to trigger a release build.
val ReleaseProperty: String = "release"

def getModifier(): String =
  if (getProp(ReleaseProperty, _.toBoolean).getOrElse(false)) ""
  else "-SNAPSHOT"

// Use `sbt -Dsha=<commit>` to provide a commit SHA.
val ShaProperty: String = "sha"

def getSha(): String =
  getProp(ShaProperty, identity).map(sha => s"-$sha").getOrElse("")

// Basis of CalVer, used if version is not supplied.
val Today: LocalDate = LocalDate.now()

// This is the base version of the published artifact. If the build is not a
// release, "-SNAPSHOT" will be appended.
val shortformVersion: String =
  getVersion()
    .map(v => s"$v${getModifier()}")
    .getOrElse(
      s"${Today.getYear()}.${Today.getMonthValue()}.${Today
          .getDayOfMonth()}${getSha()}${getModifier()}"
    )

// shortform does not publish any code artifacts.
val sharedSettings = Seq(
  scalaVersion        := scala3,
  version             := shortformVersion,
  publish / skip      := true,
  publishLocal / skip := true,
  publishArtifact     := false
)

// All tests fork. These settings define all shared test dependencies.
lazy val testSettings = Seq(
  Test / fork := true,
  libraryDependencies ++= Seq(
    "org.scalameta" %% "munit" % "1.0.0-M10" % Test
  )
)

lazy val deps = new {
    val SkunkCore: ModuleID =
      "org.tpolecat" %% "skunk-core" % "1.0.0-M1"

    val ScalacCompatAnnotation: ModuleID =
      "org.typelevel" %% "scalac-compat-annotation" % "0.1.2"

    val CatsEffect: ModuleID =
      "org.typelevel" %% "cats-effect" % "3.5.2"
}

lazy val testDeps = new {
  val TestContainersMunit: ModuleID =
    "com.dimafeng"  %% "testcontainers-scala-munit" % "0.41.0" % Test

  val TestContainersPostgresql: ModuleID =
    "com.dimafeng"  %% "testcontainers-scala-postgresql" % "0.41.0" % Test

  val Liquibase: ModuleID =
    "org.liquibase"  % "liquibase-core" % "4.24.0" % Test

  val Postgresql: ModuleID =
      "org.postgresql" % "postgresql" % "42.6.0" % Test
}

lazy val shortform = (project in file("."))
  .aggregate(
    uuid,
    error,
    crypto,
    model,
    db,
    app
   )
  .settings(sharedSettings)
  .settings(name := "shortform")

lazy val uuid = project
  .in(file("modules/uuid"))
  .settings(name := s"$ProjectName-uuid")
  .settings(sharedSettings)
  .settings(testSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.fasterxml.uuid" % "java-uuid-generator" % "4.1.1"
    )
  )

lazy val error = project
  .in(file("modules/error"))
  .settings(name := s"$ProjectName-error")
  .settings(sharedSettings)
  .settings(testSettings)
  .settings(
    libraryDependencies ++= Seq(
    )
  )

lazy val crypto = project
  .in(file("modules/crypto"))
  .dependsOn(uuid, error)
  .settings(name := s"$ProjectName-crypto")
  .settings(sharedSettings)
  .settings(testSettings)
  .settings(
    libraryDependencies ++= Seq(
    )
  )

lazy val model = project
  .in(file("modules/model"))
  .dependsOn(uuid, error, crypto)
  .settings(name := s"$ProjectName-model")
  .settings(sharedSettings)
  .settings(testSettings)
  .settings(
    libraryDependencies ++= Seq(
    )
  )

lazy val db = project
  .in(file("modules/db"))
  .dependsOn(uuid, error, crypto, model)
  .settings(name := s"$ProjectName-db")
  .settings(sharedSettings)
  .settings(testSettings)
  .settings(
    libraryDependencies ++= Seq(
      deps.SkunkCore,
      deps.ScalacCompatAnnotation
    )
  )

// Note: This task should NOT be aggregated at the top level. All integration
// tests should be manually invoked.
lazy val `db-integration-tests` = project
  .in(file("modules/db-integration-tests"))
  .dependsOn(db)
  .settings(name := s"$ProjectName-db-integration-tests")
  .settings(sharedSettings)
  .settings(testSettings)
  .settings(
    scalacOptions := integrationScalacOptions,
    libraryDependencies ++= Seq(
      testDeps.TestContainersMunit,
      testDeps.TestContainersPostgresql,
      testDeps.Liquibase,
      testDeps.Postgresql
    )
  )

lazy val app = project
  .in(file("modules/app"))
  .enablePlugins(JavaServerAppPackaging)
  .dependsOn(uuid, error, crypto, model, db)
  .settings(name := s"$ProjectName-app")
  .settings(sharedSettings)
  .settings(testSettings)
  .settings(
    run / fork := true,
    libraryDependencies ++= Seq(
      deps.CatsEffect
    )
  )

// Set Scala compiler option defaults.
ThisBuild / scalacOptions ++= allScalacOptions

lazy val allScalacOptions: Seq[String] = Seq(
  "-encoding",
  "utf8",         // Set source file character encoding.
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-explain", // Explain errors in more detail.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-explain-types",           // Explain type errors in more detail.
  "-Xfatal-warnings",         // Fail the compilation if there are any warnings.
  "-language:strictEquality", // Enable multiversal equality (require CanEqual)
  "-Wunused:implicits",       // Warn if an implicit parameter is unused.
  "-Wunused:explicits",       // Warn if an explicit parameter is unused.
  "-Wunused:imports",         // Warn if an import selector is not referenced.
  "-Wunused:locals",          // Warn if a local definition is unused.
  "-Wunused:privates",        // Warn if a private member is unused.
  "-Ysafe-init" // Enable the experimental safe initialization check.
)

lazy val integrationScalacOptions = allScalacOptions
  .filterNot(_ == "-Ysafe-init")
