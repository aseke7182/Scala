package Lab9

import Lab9.SprayJsonSerializer.SprayJsonSerializer
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.marshalling.Marshal
import akka.util.Timeout
import model._
import actor._
import org.slf4j.LoggerFactory
import scala.util.{Failure, Success}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._

object  Boot extends App with SprayJsonSerializer {
  implicit val system: ActorSystem = ActorSystem("laptop-service")
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val timeout: Timeout = Timeout(10.seconds)
  val laptopManager = system.actorOf(LaptopManager.props(),"laptop-manager")

  val token = "token"
  val log = LoggerFactory.getLogger("Boot")
  log.info(s"Token: ${token}")

  def SendMessage(msg: String): Unit ={
    val message: TelegramMessage = TelegramMessage(-352088280, msg)

    val httpReq = Marshal(message).to[RequestEntity].flatMap { entity =>
      val request = HttpRequest(HttpMethods.POST, s"https://api.telegram.org/bot${token}/sendMessage", Nil, entity)
      log.debug("Request: {}", request)
      Http().singleRequest(request)
    }

    httpReq.onComplete {
      case Success(value) =>
        log.info(s"Response: $value")
        value.discardEntityBytes()

      case Failure(exception) =>
        log.error("error")
    }

  }

  val route =
    path("healthcheck"){
      get {
        complete{
          "OK"
        }
      }
    } ~
    pathPrefix("laptop-manager"){
      path("laptop" / Segment ){ laptopID =>
        get{
          val output = (laptopManager ? LaptopManager.ReadLaptop(laptopID)).mapTo[Either[ErrorResponse,Laptop]]
          onSuccess(output){
            case Right(laptop) => {
              SendMessage(s" Response: ${laptop}")
              complete(200,laptop)
            }
            case Left(error) => {
              SendMessage(s"Response: ${error.message}")
              complete(error.status,error)
            }
          }
        }
      }~
  path("laptop"/ Segment){ laptopID =>
    delete {
      complete {
        (laptopManager ? LaptopManager.DeleteLaptop(laptopID)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
      }
    }
  } ~
  path("laptop"){
    post{
      entity(as[Laptop]) { laptop =>
        val output = (laptopManager ? LaptopManager.CreateLaptop(laptop)).mapTo[Either[ErrorResponse, SuccessfulResponse]]

        onSuccess(output) {
          case Right(success) => {
            SendMessage(s"${success.message}")
            complete(output)
          }
          case Left(error) => {
            SendMessage(s"${error.message}")
            complete(output)
          }
        }
      }
    }
  }~
  path("laptop"){
    put{
      entity(as[Laptop]){ laptop =>
        complete{
          (laptopManager ? LaptopManager.UpdateLaptop(laptop)).mapTo[Either[ErrorResponse,SuccessfulResponse]]
        }
      }
    }
  }
}

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

}