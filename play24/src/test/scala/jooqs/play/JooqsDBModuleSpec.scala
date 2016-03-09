package jooqs.play

import com.google.inject.ConfigurationException
import javax.inject.Inject
import jooqs.Database
import jooqs.syntax._
import org.jooq.SQLDialect
import org.jooq.conf.RenderKeywordStyle
import org.scalatestplus.play.MixedPlaySpec
import play.api.test.FakeApplication
import scala.reflect.ClassTag

class JooqsDBModuleSpec extends MixedPlaySpec {

  abstract class DBApp(props: (String, _)*)
    extends App(FakeApplication(additionalConfiguration = Map(props: _*))) {

    def instanceOf[A: ClassTag]: A = app.injector.instanceOf[A]
  }

  "JooqsDBModule" should {

    "bind databases by name" in new DBApp(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url" -> "jdbc:h2:mem:default",
      "db.other.driver" -> "org.h2.Driver",
      "db.other.url" -> "jdbc:h2:mem:other"
    ) {
      assert(instanceOf[JooqsDBApi].databases().size == 2)
      assert(instanceOf[DefaultComponent].url == "jdbc:h2:mem:default")
      assert(instanceOf[NamedDefaultComponent].url == "jdbc:h2:mem:default")
      assert(instanceOf[NamedOtherComponent].url == "jdbc:h2:mem:other")
    }

    "not bind default databases without configuration" in new DBApp(
      "db.other.driver" -> "org.h2.Driver",
      "db.other.url" -> "jdbc:h2:mem:other"
    ) {
      assert(instanceOf[JooqsDBApi].databases().size == 1)
      intercept[ConfigurationException](instanceOf[DefaultComponent])
      intercept[ConfigurationException](instanceOf[NamedDefaultComponent])
      assert(instanceOf[NamedOtherComponent].url == "jdbc:h2:mem:other")
    }

    "not bind databases without configuration" in new DBApp {
      assert(instanceOf[JooqsDBApi].databases().isEmpty)
      intercept[ConfigurationException](instanceOf[DefaultComponent])
      intercept[ConfigurationException](instanceOf[NamedDefaultComponent])
      intercept[ConfigurationException](instanceOf[NamedOtherComponent])
    }

    "allow default database name to be configured" in new DBApp(
      "play.db.default" -> "other",
      "db.other.driver" -> "org.h2.Driver",
      "db.other.url" -> "jdbc:h2:mem:other"
    ) {
      assert(instanceOf[JooqsDBApi].databases().size == 1)
      assert(instanceOf[DefaultComponent].url == "jdbc:h2:mem:other")
      assert(instanceOf[NamedOtherComponent].url == "jdbc:h2:mem:other")
      intercept[ConfigurationException](instanceOf[NamedDefaultComponent])
    }

    "allow db config key to be configured" in new DBApp(
      "play.db.config" -> "databases",
      "databases.default.driver" -> "org.h2.Driver",
      "databases.default.url" -> "jdbc:h2:mem:default"
    ) {
      assert(instanceOf[JooqsDBApi].databases().size == 1)
      assert(instanceOf[DefaultComponent].url == "jdbc:h2:mem:default")
      assert(instanceOf[NamedDefaultComponent].url == "jdbc:h2:mem:default")
    }

    "allow jOOQ Settings to be configured" in new DBApp(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url" -> "jdbc:h2:mem:default",
      "db.default.jooq.dialect" -> "MYSQL",
      "db.default.jooq.renderSchema" -> false,
      "db.default.jooq.render-keyword-style" -> "LOWER"
    ) {
      val db = instanceOf[DefaultComponent].db
      assert(db.dialect == SQLDialect.MYSQL)
      assert(!db.settings.isRenderSchema)
      assert(db.settings.getRenderKeywordStyle == RenderKeywordStyle.LOWER)
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

case class DefaultComponent @Inject()(db: Database) extends Component

case class NamedDefaultComponent @Inject()(@NamedDatabase("default") db: Database) extends Component

case class NamedOtherComponent @Inject()(@NamedDatabase("other") db: Database) extends Component
