package Adi.Lab10

import Adi.Lab9.model.{Country, ErrorResponse, SuccessfulResponse}
import Adi.Lab9.SprayJsonSerializer
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.marshalling.Marshal
import akka.util.Timeout
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._

object  Boot extends App with SprayJsonSerializer with Serializer {
  implicit val system: ActorSystem = ActorSystem("country-bot")
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val timeout: Timeout = Timeout(10.seconds)
  val countryManager = system.actorOf(CountryManager.props(),"country-manager")

  val token = "1060150051:AAFJJQlY0OXi8k3thajQGfu3MrWMOkq8QV0"
  val log = LoggerFactory.getLogger("Boot")
  log.info(s"Token: ${token}")

  def SendMessage(msg: String): Unit = {

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
    pathPrefix("country-bot") {
      path("country" / Segment) { countryId =>
        get {
          complete {
            SendMessage(s"Obtained country with ID ${countryId}. Status code: 200")
            (countryManager ? CountryManager.ReadCountry(countryId)).mapTo[Either[ErrorResponse, Country]]
          }
        }
      } ~
        path("country") {
          post {
            entity(as[Country]) { country =>
              complete {
                SendMessage(s"Created country with ID ${country.id}. Status code: 201")
                (countryManager ? CountryManager.CreateCountry(country)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
              }
            }
          }
        } ~
        path("country") {
          put {
            entity(as[Country]) { country =>
              complete {
                SendMessage(s"Updated country with ID ${country.id}. Status code: 200")
                (countryManager ? CountryManager.UpdateCountry(country)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
              }
            }
          }
        } ~
        path("country" / Segment) { countryId =>
          delete {
            complete {
              SendMessage(s"Succesfully destroyed country. Status code: 204")
              (countryManager ? CountryManager.DeleteCountry(countryId)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
            }
          }
        }
    }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

}
