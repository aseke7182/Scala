package Lab9.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.index.CreateIndexResponse
import com.sksamuel.elastic4s.http.{HttpClient, RequestFailure, RequestSuccess}
import Lab9.model._
import Lab9.ElasticSerializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


object LaptopManager {
  case class CreateLaptop(laptop: Laptop)
  case class UpdateLaptop(laptop: Laptop)
  case class ReadLaptop(id:String)
  case class DeleteLaptop(id:String)

  def props() = Props(new LaptopManager)
}


class LaptopManager extends Actor with ActorLogging with ElasticSerializer{
  import LaptopManager._

  val client = HttpClient(ElasticsearchClientUri("localhost", 9200))

  def createEsIndex() = {
    val cmd: Future[Either[RequestFailure, RequestSuccess[CreateIndexResponse]]] = client.execute{ createIndex("laptop")}

    cmd.onComplete {
      case Success(value) =>
        value.foreach {requestSuccess =>
          println(requestSuccess)}

      case Failure(exception) =>
        println(exception.getMessage)
    }
  }

  def receive: Receive = {

    case CreateLaptop(laptop) =>
      val sender1 = sender()

      val cmd = client.execute(indexInto("laptop" / "_doc").id(laptop.id).doc(laptop))

      cmd.onComplete{
        case Success(value) =>
          sender1 ! Right(SuccessfulResponse(201,s"Laptop with ID: ${} created."))

        case Failure(exception) =>
          println(exception)
          sender1 ! Left(ErrorResponse(500,"some error"))
      }

    case  ReadLaptop(id: String) =>
      val sender1 = sender()

      client.execute{get(id).from("laptop"/"_doc")}.onComplete{
        case Success(either) =>
          either match {
            case Right(value) =>
              if(value.result.found){
                val laptop = value.result.to[Laptop]
                sender1 ! Right(laptop)
              }else{
                sender1 ! Left(ErrorResponse(404,"there is no laptop with such id"))
              }
            case Left(value) =>
              sender1 ! Left(ErrorResponse(value.status,"internal error1"))
          }
        case Failure(fail) =>
          sender1 ! Left(ErrorResponse(500,"internal error"))
      }

    case UpdateLaptop(laptop: Laptop) =>
      val sender1 = sender()

      val cmd = client.execute(update(laptop.id).in("laptop" / "_doc").doc(laptop))

      cmd.onComplete{
        case Success(either) =>
          either match {
            case Right(value) =>
              if(value.result.found){
                sender1 ! Right(SuccessfulResponse(200,s"Laptop with ${laptop.id} ID updated "))
              }else{
                sender1 ! Left(ErrorResponse(404,"can not find laptop with such id"))
              }
            case Left(left) =>
              sender1 ! Left(ErrorResponse(left.status,"can not find laptop with such id"))
          }
        case Failure(exception) =>
          sender1 ! Left(ErrorResponse(500,"internal error"))
      }

    case DeleteLaptop(id:String) =>
      val sender1 = sender()

      val cmd = client.execute{ delete(id).from("laptop"/"_doc")}

      cmd.onComplete{
        case Success(either) =>
          either match {
            case Right(value) =>
              sender1 ! Right(SuccessfulResponse(200,s"laptop with ${id} ID successfully deleted"))
            case Left(value) =>
              sender1 ! Left(ErrorResponse(404,s"Laptop with ${id} was not found"))
          }
        case Failure(fail) =>
          sender1 ! Left(ErrorResponse(500,"internal error"))
      }
  }

}
