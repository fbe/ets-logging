package de.kaufhof.ets.logging.split.test.encoding

import java.time.LocalDateTime
import java.util.UUID

import de.kaufhof.ets.logging.split._
import de.kaufhof.ets.logging.split.test.domain._
import de.kaufhof.ets.logging.split.ext.playjson._
import de.kaufhof.ets.logging.split.syntax._
import play.api.libs.json._

import scala.util.Random

object playjson {
  object JsValueKeys extends LogKeysSyntax[JsValue] with DefaultJsValueEncoders {
    val Logger: Key[Class[_]] = Key("logger").withImplicitEncoder
    val Level: Key[Level] = Key("level").withImplicitEncoder
    val Message: Key[String] = Key("msg").withImplicitEncoder
    val Timestamp: Key[LocalDateTime] = Key("ts").withExplicit(Encoder.fromToString)
    val VariantId: Key[VariantId] = Key("variantid").withExplicit(Encoder[VariantId].by(_.value))
    val VariantName: Key[String] = Key("variantname").withImplicitEncoder
    val SomeUUID: Key[UUID] = Key("uuid").withImplicitEncoder
    val RandomEncoder: Key[Random] = Key("randenc").withExplicit(Encoder[Random].by(_.nextInt(100)))
    val RandomEval: Key[Int] = Key("randeval").withImplicitEncoder
  }

  object JsonLogConfig extends DefaultLogConfig[JsValue, Unit] with DefaultJsValueEncoders {
    override type Combined = JsValue
    override def combiner: EventCombiner = JsValueLogEventCombiner
    override def appender: Appender = StdOutStringLogAppender.comap(_.toString())
    override val classNameLevels: Map[String, Level] = Map(
      "Asd" -> Level.Debug
    )

    val syntax = ConfigSyntax(JsValueKeys, Decomposers)
    override def predefKeys: PredefKeys = syntax.Keys

    object Decomposers extends Decomposer2DecomposedImplicits[Encoded] {
      import syntax._
      implicit lazy val variantDecomposer: Decomposer[Variant] = variant =>
        Decomposed(
          Keys.VariantId ~> variant.id,
          Keys.VariantName ~> variant.name
      )
    }
  }
}
