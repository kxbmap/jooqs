name := "jooqs"

enablePlugins(
  DisablePublishing,
  CrossPerProjectPlugin)

lazy val core = project
  .enablePlugins(SyntaxGenerator)
  .settings(
    name := "jooqs-core",
    dependencies.core,
    crossVersionSettings,
    scalapropsWithScalazlaws
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
    copySourcesProject := play24
  )
  .dependsOn(core, config)
