package jooqs.config

import com.typesafe.config.ConfigFactory
import configs.syntax._
import java.util.regex.Pattern
import org.jooq.conf.{BackslashEscaping, MappedSchema, MappedTable, ParamType, RenderKeywordStyle, RenderMapping, RenderNameStyle, Settings, SettingsTools, StatementType}
import scala.collection.JavaConverters._
import scalaprops.Property.forAll
import scalaprops.{Gen, Scalaprops}
import scalaz.std.anyVal._
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
    val integer = Gen[java.lang.Integer]
    A.apply5(
      A.tuple5(bool, bool, Gen[RenderMapping], Gen[RenderNameStyle], Gen[RenderKeywordStyle]),
      A.tuple5(bool, bool, Gen[BackslashEscaping], Gen[ParamType], Gen[StatementType]),
      A.tuple5(bool, bool, bool, bool, bool),
      A.tuple3(bool, bool, bool),
      A.tuple3(integer, integer, integer)
    ) {
      case ((rc, rs, rm, rns, rks), (rf, rss, be, pt, st), (el, ol, ar, upk, rca), (fw, ra, jpa), (qt, mr, fs)) =>
        val s = new Settings()
        s.setRenderCatalog(rc)
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
        s.setReflectionCaching(rca)
        s.setFetchWarnings(fw)
        s.setReturnAllOnUpdatableRecord(ra)
        s.setMapJPAAnnotations(jpa)
        s.setQueryTimeout(qt)
        s.setMaxRows(mr)
        s.setFetchSize(fs)
        s
    }
  }

  lazy val nameGen: Gen[String] =
    Apply[Gen].apply2(Gen.alphaChar, Gen.alphaNumString)(_ +: _)

  implicit lazy val patternGen: Gen[Pattern] =
    nameGen.map(Pattern.compile)

  implicit lazy val renderMappingGen: Gen[RenderMapping] =
    Apply[Gen].apply2(nameGen, Gen[List[MappedSchema]]) { (ds, schemata) =>
      val rm = new RenderMapping()
      rm.setDefaultSchema(ds)
      rm.setSchemata(schemata.asJava)
      rm
    }

  implicit lazy val mappedSchemaGen: Gen[MappedSchema] =
    Apply[Gen].apply4(nameGen, Gen[Pattern], nameGen, Gen[List[MappedTable]]) { (in, exp, out, ts) =>
      val ms = new MappedSchema()
      ms.setInput(in)
      ms.setInputExpression(exp)
      ms.setOutput(out)
      ms.setTables(ts.asJava)
      ms
    }

  implicit lazy val mappedTableGen: Gen[MappedTable] =
    Apply[Gen].apply3(nameGen, Gen[Pattern], nameGen) { (in, exp, out) =>
      val mt = new MappedTable()
      mt.setInput(in)
      mt.setInputExpression(exp)
      mt.setOutput(out)
      mt
    }


  implicit def javaListEqual[A: Equal]: Equal[java.util.List[A]] =
    Equal.equalBy(_.asScala.toList)

  implicit def javaEnumEqual[A <: java.lang.Enum[A]]: Equal[A] =
    Equal.equalA[A]

  implicit lazy val javaBooleanEqual: Equal[java.lang.Boolean] =
    Equal.equalA[java.lang.Boolean]

  implicit lazy val javaIntegerEqual: Equal[java.lang.Integer] =
    Equal.equalA[java.lang.Integer]

  implicit lazy val patternEqual: Equal[Pattern] =
    Equal.equalBy(p => (p.pattern(), p.flags()))

  implicit lazy val settingsEqual: Equal[Settings] =
    Equal.equalBy(s => (
      (
        s.isRenderCatalog,
        s.isRenderSchema,
        s.getRenderMapping,
        s.getRenderNameStyle,
        s.getRenderKeywordStyle,
        s.isRenderFormatted,
        s.isRenderScalarSubqueriesForStoredFunctions,
        s.getBackslashEscaping),
      (
        s.getParamType,
        s.getStatementType,
        s.isExecuteLogging,
        s.isExecuteWithOptimisticLocking,
        s.isAttachRecords,
        s.isUpdatablePrimaryKeys,
        s.isReflectionCaching,
        s.isFetchWarnings),
      (
        s.isReturnAllOnUpdatableRecord,
        s.isMapJPAAnnotations,
        s.getQueryTimeout,
        s.getMaxRows,
        s.getFetchSize))
    )

  implicit lazy val renderMappingEqual: Equal[RenderMapping] =
    Equal.equalBy(Option(_).map(rm => (rm.getDefaultSchema, rm.getSchemata)))

  implicit lazy val mappedSchemaEqual: Equal[MappedSchema] =
    Equal.equalBy(ms => (ms.getInput, ms.getInputExpression, ms.getOutput, ms.getTables))

  implicit lazy val mappedTableEqual: Equal[MappedTable] =
    Equal.equalBy(mt => (mt.getInput, mt.getInputExpression, mt.getOutput))

}
