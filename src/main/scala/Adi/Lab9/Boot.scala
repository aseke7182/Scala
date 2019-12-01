package Adi.Lab9

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.index.CreateIndexResponse
import com.sksamuel.elastic4s.http.{HttpClient, RequestFailure, RequestSuccess}
import Adi.Lab9.model.{Country, President}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Boot extends App with ElasticSerializer {

  val client = HttpClient(ElasticsearchClientUri("localhost", 9200))

  def createEsIndex(): Unit = {
    val cmd: Future[Either[RequestFailure, RequestSuccess[CreateIndexResponse]]] =
      client.execute { createIndex("countries") }

    cmd.onComplete {
      case Success(value) =>
        value.foreach {requestSuccess =>
          println(requestSuccess)}

      case Failure(exception) =>
        println(exception.getMessage)
    }
  }

  def createCountry(): Unit = {
    val country = Country("1", "Russia", President("1", "Vladimir", "Putin"), 150000000)
    val cmd = client.execute(indexInto("countries" / "_doc").id("1").doc(country))

    cmd.onComplete{
      case Success(value)=>
        println(value)

      case Failure(fail)=>
        println(fail.getMessage)
    }
  }

  def readCountry(id: String): Unit = {
    client.execute{
      get(id).from("countries" / "_doc")
    }.onComplete{
      case Success(either) =>
        either.map(e => e.result.to[Country]).foreach{
          country =>
            println(country)
        }
      case Failure(fail) =>
    }
  }

  def updateCountry(id: String): Unit = {
    val country = Country("1", "Kazakhstan", President("1", "Qasym-Jomart", "Tokayev"), 18000000)

    client.execute{
      delete(id).from("countries"/"_doc")
      indexInto("countries" / "_doc").id(id).doc(country)
    }.onComplete{
      case Success(either) =>
        println(country)
      case Failure(fail) =>
        println("Failed to update country")
    }
  }


  def deleteCountry(id: String) : Unit = {
    client.execute{
      delete(id).from("countries"/"_doc")
    }.onComplete{
      case Success(success) =>
        println("Country succesfully destroyed")

      case Failure(fail)=>
        println("Failed to delete country")
    }
  }

  createEsIndex()
  createCountry()
//  updateCountry("1")
  //  deleteCountry("1")
}
