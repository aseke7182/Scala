package Adi.Lab9

import com.sksamuel.elastic4s.{Hit, HitReader, Indexable}
import spray.json._
import Adi.Lab9.model.Country

import scala.util.Try

trait ElasticSerializer extends SprayJsonSerializer {
  implicit object CountryIndexable extends Indexable[Country] {
    override def json(country: Country): String = country.toJson.compactPrint
  }

  implicit object CountryHitReader extends HitReader[Country] {
    override def read(hit: Hit): Either[Throwable, Country] = {
      Try {
        val jsonAst = hit.sourceAsString.parseJson
        jsonAst.convertTo[Country]
      }.toEither
    }
  }
}