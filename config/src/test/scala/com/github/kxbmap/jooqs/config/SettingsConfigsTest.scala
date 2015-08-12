package com.github.kxbmap.jooqs.config

import com.github.kxbmap.configs.Configs
import com.typesafe.config.{ConfigFactory, ConfigUtil}
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
    Configs[Settings].extract(config) === SettingsTools.defaultSettings()
  }

  val full = forAll { s: Settings =>
    val config = ConfigFactory.parseString(
      s"""render-schema = ${s.isRenderSchema}
         |render-mapping = ${render(s.getRenderMapping)}
         |render-name-style = ${s.getRenderNameStyle}
         |render-keyword-style = ${s.getRenderKeywordStyle}
         |render-formatted = ${s.isRenderFormatted}
         |render-scalar-subqueries-for-stored-functions = ${s.isRenderScalarSubqueriesForStoredFunctions}
         |backslash-escaping = ${s.getBackslashEscaping}
         |param-type = ${s.getParamType}
         |statement-type = ${s.getStatementType}
         |execute-logging = ${s.isExecuteLogging}
         |execute-with-optimistic-locking = ${s.isExecuteWithOptimisticLocking}
         |attach-records = ${s.isAttachRecords}
         |updatable-primary-keys = ${s.isUpdatablePrimaryKeys}
         |reflection-caching = ${s.isReflectionCaching}
         |fetch-warnings = ${s.isFetchWarnings}
         |""".stripMargin)

    Configs[Settings].extract(config) === s
  }

  def render(rm: RenderMapping): String =
    s"""{
       |  default-schema = ${ConfigUtil.quoteString(rm.getDefaultSchema)}
       |  schemata = ${rm.getSchemata.asScala.map(render).mkString("[\n", ",\n", "\n]")}
       |}
       |""".stripMargin

  def render(ms: MappedSchema): String =
    s"""{
       |  input = ${ms.getInput}
       |  output = ${ms.getOutput}
       |  tables = ${ms.getTables.asScala.map(render).mkString("[\n", ",\n", "\n]")}
       |}
       |""".stripMargin

  def render(mt: MappedTable): String =
    s"""{
       |  input = ${mt.getInput}
       |  output = ${mt.getOutput}
       |}
       |""".stripMargin


  implicit lazy val settingsGen: Gen[Settings] = {
    val A = Apply[Gen]
    val bool = Gen[java.lang.Boolean]
    A.apply3(
      A.tuple5(bool, Gen[RenderMapping], Gen[RenderNameStyle], Gen[RenderKeywordStyle], bool),
      A.tuple5(bool, Gen[BackslashEscaping], Gen[ParamType], Gen[StatementType], bool),
      A.tuple5(bool, bool, bool, bool, bool)
    ) {
      case ((rs, rm, rns, rks, rf), (rss, be, pt, st, el), (ol, ar, upk, rc, fw)) =>
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
        s
    }
  }

  lazy val nameGen: Gen[String] =
    Apply[Gen].apply2(Gen.alphaChar, Gen.alphaNumString)(_ +: _)

  implicit lazy val renderMappingGen: Gen[RenderMapping] =
    Apply[Gen].apply2(nameGen, Gen[List[MappedSchema]].map(_.asJava)) {
      case (ds, schemata) =>
        val rm = new RenderMapping()
        rm.setDefaultSchema(ds)
        rm.setSchemata(schemata)
        rm
    }

  implicit lazy val mappedSchemaGen: Gen[MappedSchema] =
    Apply[Gen].apply3(nameGen, nameGen, Gen[List[MappedTable]].map(_.asJava)) {
      case (in, out, ts) =>
        val ms = new MappedSchema()
        ms.setInput(in)
        ms.setOutput(out)
        ms.setTables(ts)
        ms
    }

  implicit lazy val mappedTableGen: Gen[MappedTable] =
    Apply[Gen].apply2(nameGen, nameGen) {
      case (in, out) =>
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
        s.isFetchWarnings))
    )

  implicit lazy val renderMappingEqual: Equal[RenderMapping] =
    Equal.equalBy(Option(_).map(rm => (rm.getDefaultSchema, rm.getSchemata)))

  implicit lazy val mappedSchemaEqual: Equal[MappedSchema] =
    Equal.equalBy(Option(_).map(ms => (ms.getInput, ms.getOutput, ms.getTables)))

  implicit lazy val mappedTableEqual: Equal[MappedTable] =
    Equal.equalBy(Option(_).map(mt => (mt.getInput, mt.getOutput)))

}
