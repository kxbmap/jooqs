name := "jooqs"

disablePublishSettings

lazy val core = project.settings(
  name := "jooqs-core",
  dependencies.core,
  crossVersionSettings
).enablePlugins(SyntaxGenerator)

lazy val config = project.settings(
  name := "jooqs-config",
  dependencies.config,
  crossVersionSettings,
  scalapropsSettings
)

lazy val play = project.settings(
  name := "jooqs-play",
  dependencies.play
).dependsOn(core, config)
