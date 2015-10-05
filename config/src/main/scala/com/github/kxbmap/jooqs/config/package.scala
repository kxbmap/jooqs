package com.github.kxbmap.jooqs

import com.github.kxbmap.configs.Configs
import org.jooq.conf.{MappedSchema, MappedTable, RenderMapping, Settings, SettingsTools}

package object config {

  private[this] def defaults = SettingsTools.defaultSettings()

  implicit lazy val settingsConfigs: Configs[Settings] =
    Configs.bean(defaults)

  implicit lazy val renderMappingConfigs: Configs[RenderMapping] =
    Configs.bean(Option(defaults.getRenderMapping).getOrElse(new RenderMapping()))

  implicit lazy val mappedSchemaConfigs: Configs[MappedSchema] =
    Configs.bean[MappedSchema]

  implicit lazy val mappedTableConfigs: Configs[MappedTable] =
    Configs.bean[MappedTable]

}
