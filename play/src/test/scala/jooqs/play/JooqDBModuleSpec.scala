package jooqs.play

import javax.inject.Inject
import jooqs.Database
import jooqs.syntax._
import org.jooq.conf.RenderKeywordStyle
import play.api.test._

class JooqDBModuleSpec extends PlaySpecification {

  "JooqDBModule" should {

    "bind databases by name" in new WithApplication(FakeApplication(
      additionalConfiguration = Map(
        "db.default.driver" -> "org.h2.Driver",
        "db.default.url" -> "jdbc:h2:mem:default",
        "db.other.driver" -> "org.h2.Driver",
        "db.other.url" -> "jdbc:h2:mem:other"
      )
    )) {
      app.injector.instanceOf[JooqDBApi].databases must have size 2
      app.injector.instanceOf[DefaultComponent].url must_== "jdbc:h2:mem:default"
      app.injector.instanceOf[NamedDefaultComponent].url must_== "jdbc:h2:mem:default"
      app.injector.instanceOf[NamedOtherComponent].url must_== "jdbc:h2:mem:other"
    }

    "not bind default databases without configuration" in new WithApplication(FakeApplication(
      additionalConfiguration = Map(
        "db.other.driver" -> "org.h2.Driver",
        "db.other.url" -> "jdbc:h2:mem:other"
      )
    )) {
      app.injector.instanceOf[JooqDBApi].databases must have size 1
      app.injector.instanceOf[DefaultComponent] must throwA[com.google.inject.ConfigurationException]
      app.injector.instanceOf[NamedDefaultComponent] must throwA[com.google.inject.ConfigurationException]
      app.injector.instanceOf[NamedOtherComponent].url must_== "jdbc:h2:mem:other"
    }

    "not bind databases without configuration" in new WithApplication(FakeApplication()) {
      app.injector.instanceOf[JooqDBApi].databases must beEmpty
      app.injector.instanceOf[DefaultComponent] must throwA[com.google.inject.ConfigurationException]
      app.injector.instanceOf[NamedDefaultComponent] must throwA[com.google.inject.ConfigurationException]
      app.injector.instanceOf[NamedOtherComponent] must throwA[com.google.inject.ConfigurationException]
    }

    "allow default database name to be configured" in new WithApplication(FakeApplication(
      additionalConfiguration = Map(
        "play.db.default" -> "other",
        "db.other.driver" -> "org.h2.Driver",
        "db.other.url" -> "jdbc:h2:mem:other"
      )
    )) {
      app.injector.instanceOf[JooqDBApi].databases must have size 1
      app.injector.instanceOf[DefaultComponent].url must_== "jdbc:h2:mem:other"
      app.injector.instanceOf[NamedOtherComponent].url must_== "jdbc:h2:mem:other"
      app.injector.instanceOf[NamedDefaultComponent] must throwA[com.google.inject.ConfigurationException]
    }

    "allow db config key to be configured" in new WithApplication(FakeApplication(
      additionalConfiguration = Map(
        "play.db.config" -> "databases",
        "databases.default.driver" -> "org.h2.Driver",
        "databases.default.url" -> "jdbc:h2:mem:default"
      )
    )) {
      app.injector.instanceOf[JooqDBApi].databases must have size 1
      app.injector.instanceOf[DefaultComponent].url must_== "jdbc:h2:mem:default"
      app.injector.instanceOf[NamedDefaultComponent].url must_== "jdbc:h2:mem:default"
    }

    "allow jOOQ Settings to be configured" in new WithApplication(FakeApplication(
      additionalConfiguration = Map(
        "db.default.driver" -> "org.h2.Driver",
        "db.default.url" -> "jdbc:h2:mem:default",
        "db.default.jooq.renderSchema" -> false,
        "db.default.jooq.render-keyword-style" -> "LOWER"
      )
    )) {
      val s = app.injector.instanceOf[DefaultComponent].db.settings
      s.isRenderSchema must_== false
      s.getRenderKeywordStyle must_== RenderKeywordStyle.LOWER
    }

  }

}

trait Component {

  def db: Database

  def url: String =
    db.configuration.connectionProvider().withConnection {
      _.getMetaData.getURL
    }

}

case class DefaultComponent @Inject() (db: Database) extends Component

case class NamedDefaultComponent @Inject() (@NamedDatabase("default") db: Database) extends Component

case class NamedOtherComponent @Inject() (@NamedDatabase("other") db: Database) extends Component
