package Adi.Lab6

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import Adi.Lab6.actor.MovieManager
import Lab6.model.{ErrorResponse, Movie, SuccessfulResponse}
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object Boot extends App with SprayJsonSerializer {

  implicit val system: ActorSystem = ActorSystem("movie-service")
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(10.seconds)

  val movieManager = system.actorOf(MovieManager.props(), "movie-manager")

  val route =
    pathPrefix("kbtu-cinema") {
      path("movie" / Segment) { movieId =>
        get {
          complete {
            (movieManager ? MovieManager.ReadMovie(movieId)).mapTo[Either[ErrorResponse, Movie]]
          }
        }
      } ~
        path("movie") {
          post {
            entity(as[Movie]) { movie =>
              complete {
                (movieManager ? MovieManager.CreateMovie(movie)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
              }
            }
          }
        } ~
        path("movie") {
          put {
            entity(as[Movie]) { movie =>
              complete {
                (movieManager ? MovieManager.UpdateMovie(movie)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
              }
            }
          }
        } ~
        path("movie" / Segment) { movieId =>
          delete {
            complete {
              (movieManager ? MovieManager.DeleteMovie(movieId)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
            }
          }
        }
    }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

  //  val testBot = system.actorOf(TestBot.props(movieManager), "test-bot")
  //
  //  testBot ! TestBot.TestCreate
  //
  //  testBot ! TestBot.TestConflict
  //
  //  testBot ! TestBot.TestRead
  //
  //  testBot ! TestBot.TestNotFound
  //
  //  testBot ! TestBot.TestUpdate
  //
  //  testBot ! TestBot.TestDelete
}