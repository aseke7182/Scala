package Lab9

import Lab9.SprayJsonSerializer.SprayJsonSerializer
import com.sksamuel.elastic4s.{Hit, HitReader, Indexable}

import scala.util.Try
import spray.json._
import Lab9.model._

trait ElasticSerializer extends SprayJsonSerializer {

  implicit object LaptopIndexable extends Indexable[Laptop]{
    override def json(laptop: Laptop): String = laptop.toJson.compactPrint
  }

  implicit object LaptopHitReader extends HitReader[Laptop]{
    override def read(hit: Hit): Either[Throwable, Laptop] =
      Try{
        val jsonAst = hit.sourceAsString.parseJson
        jsonAst.convertTo[Laptop]
      }.toEither
  }

}
