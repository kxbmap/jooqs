package jooqs.config

import com.typesafe.config.ConfigFactory
import configs.syntax._
import org.jooq.conf.{BackslashEscaping, MappedSchema, MappedTable, ParamType, RenderKeywordStyle, RenderMapping, RenderNameStyle, Settings, SettingsTools, StatementType}
import scala.collection.JavaConverters._
import scalaprops.Property.forAll
import scalaprops.{Gen, Scalaprops}
import scalaz.std.list._
import scalaz.std.option._
import scalaz.std.string._
import scalaz.std.tuple._
import scalaz.syntax.equal._
import scalaz.{Apply, Equal}

object SettingsConfigsTest extends Scalaprops {

  val empty = forAll {
    val config = ConfigFactory.empty()
    config.extract[Settings].exists(_ === SettingsTools.defaultSettings())
  }

  val full = forAll { s: Settings =>
    val config = s.toConfigValue.atKey("s")
    config.get[Settings]("s").exists(_ === s)
  }


  implicit lazy val settingsGen: Gen[Settings] = {
    val A = Apply[Gen]
    val bool = Gen[java.lang.Boolean]
    A.apply4(
      A.tuple4(bool, Gen[RenderMapping], Gen[RenderNameStyle], Gen[RenderKeywordStyle]),
      A.tuple4(bool, bool, Gen[BackslashEscaping], Gen[ParamType]),
      A.tuple4(Gen[StatementType], bool, bool, bool),
      A.tuple4(bool, bool, bool, bool)
    ) {
      case ((rs, rm, rns, rks), (rf, rss, be, pt), (st, el, ol, ar), (upk, rc, fw, jpa)) =>
        val s = new Settings()
        s.setRenderSchema(rs)
        s.setRenderMapping(rm)
        s.setRenderNameStyle(rns)
        s.setRenderKeywordStyle(rks)
        s.setRenderFormatted(rf)
        s.setRenderScalarSubqueriesForStoredFunctions(rss)
        s.setBackslashEscaping(be)
        s.setParamType(pt)
        s.setStatementType(st)
        s.setExecuteLogging(el)
        s.setExecuteWithOptimisticLocking(ol)
        s.setAttachRecords(ar)
        s.setUpdatablePrimaryKeys(upk)
        s.setReflectionCaching(rc)
        s.setFetchWarnings(fw)
        s.setMapJPAAnnotations(jpa)
        s
    }
  }

  lazy val nameGen: Gen[String] =
    Apply[Gen].apply2(Gen.alphaChar, Gen.alphaNumString)(_ +: _)

  implicit lazy val renderMappingGen: Gen[RenderMapping] =
    Apply[Gen].apply2(nameGen, Gen[List[MappedSchema]]) { (ds, schemata) =>
      val rm = new RenderMapping()
      rm.setDefaultSchema(ds)
      rm.setSchemata(schemata.asJava)
      rm
    }

  implicit lazy val mappedSchemaGen: Gen[MappedSchema] =
    Apply[Gen].apply3(nameGen, nameGen, Gen[List[MappedTable]]) { (in, out, ts) =>
      val ms = new MappedSchema()
      ms.setInput(in)
      ms.setOutput(out)
      ms.setTables(ts.asJava)
      ms
    }

  implicit lazy val mappedTableGen: Gen[MappedTable] =
    Apply[Gen].apply2(nameGen, nameGen) { (in, out) =>
      val mt = new MappedTable()
      mt.setInput(in)
      mt.setOutput(out)
      mt
    }


  implicit def javaListEqual[A: Equal]: Equal[java.util.List[A]] =
    Equal.equalBy(_.asScala.toList)

  implicit def javaEnumEqual[A <: java.lang.Enum[A]]: Equal[A] =
    Equal.equalA[A]

  implicit lazy val javaBooleanEqual: Equal[java.lang.Boolean] =
    Equal.equalA[java.lang.Boolean]

  implicit lazy val settingsEqual: Equal[Settings] =
    Equal.equalBy(s => (
      (
        s.isRenderSchema,
        s.getRenderMapping,
        s.getRenderNameStyle,
        s.getRenderKeywordStyle,
        s.isRenderFormatted,
        s.isRenderScalarSubqueriesForStoredFunctions,
        s.getBackslashEscaping,
        s.getParamType),
      (
        s.getStatementType,
        s.isExecuteLogging,
        s.isExecuteWithOptimisticLocking,
        s.isAttachRecords,
        s.isUpdatablePrimaryKeys,
        s.isReflectionCaching,
        s.isFetchWarnings,
        s.isMapJPAAnnotations))
    )

  implicit lazy val renderMappingEqual: Equal[RenderMapping] =
    Equal.equalBy(Option(_).map(rm => (rm.getDefaultSchema, rm.getSchemata)))

  implicit lazy val mappedSchemaEqual: Equal[MappedSchema] =
    Equal.equalBy(Option(_).map(ms => (ms.getInput, ms.getOutput, ms.getTables)))

  implicit lazy val mappedTableEqual: Equal[MappedTable] =
    Equal.equalBy(Option(_).map(mt => (mt.getInput, mt.getOutput)))

}
