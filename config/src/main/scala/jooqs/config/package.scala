package jooqs

import configs.ConfigReader
import org.jooq.conf.{Settings, SettingsTools}

package object config {

  implicit val jooqSettingsConfigReader: ConfigReader[Settings] =
    ConfigReader.deriveBeanWith(SettingsTools.defaultSettings())

}
