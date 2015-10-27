name := "jooqs"

disablePublishSettings

lazy val core = project
  .enablePlugins(SyntaxGenerator)
  .settings(
    name := "jooqs-core",
    dependencies.core,
    crossVersionSettings
  )

lazy val config = project
  .settings(
    name := "jooqs-config",
    dependencies.config,
    crossVersionSettings,
    scalapropsSettings
  )

lazy val play24 = project
  .settings(
    name := "jooqs-play24",
    dependencies.play24
  )
  .dependsOn(core, config)

lazy val play25 = project
  .enablePlugins(CopySources)
  .settings(
    name := "jooqs-play25",
    dependencies.play25,
    Seq(Compile, Test).flatMap(inConfig(_)(Seq(
      sourcesToCopies := copies(
        (sources in play24).value,
        (sourceDirectories in play24).value,
        sourceManaged.value
      ),
      resourcesToCopies := copies(
        (resources in play24).value,
        (resourceDirectories in play24).value,
        resourceManaged.value
      )
    )))
  )
  .dependsOn(core, config)
