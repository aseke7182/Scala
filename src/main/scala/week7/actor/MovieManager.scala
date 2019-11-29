package week7.actor

import akka.actor.{Actor, ActorLogging, Props}
import week7.actor.MovieManager.{DeleteMovie, UpdateMovie}
import week7.model.{ErrorResponse, SuccessfulResponse}
import week7.model.{ErrorResponse, Movie, SuccessfulResponse}

object MovieManager {

  case class CreateMovie(movie: Movie)
  case class ReadMovie(id: String)
  case class UpdateMovie(movie: Movie)
  case class DeleteMovie(id: String)
  case class GetAllMovies()

  def props() = Props(new MovieManager)
}

class MovieManager extends Actor with ActorLogging {

  import MovieManager._

  var movies: Map[String, Movie] = Map()

  override def receive: Receive = {

    case CreateMovie(movie) =>
      movies.get(movie.id) match {
        case Some(existingMovie) =>
          //          log.warning(s"Could not create a movie with ID: ${movie.id} because it already exists.")
          sender() ! Left(ErrorResponse(409, s"Movie with ID: ${movie.id} already exists."))

        case None =>
          movies = movies + (movie.id -> movie)
          //          log.info("Movie with ID: {} created.", movie.id)
          sender() ! Right(SuccessfulResponse(201, s"Movie with ID: ${movie.id} created."))
      }

    case GetAllMovies =>
      movies.isEmpty match {
        case true =>
          sender() ! Left(ErrorResponse(409,"There is no movie"))
        case false =>
          sender() ! Right(movies)
      }

    case msg: ReadMovie =>
      movies.get(msg.id) match {
        case Some(movie) =>
          sender() ! Right(movie)

        case None =>
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${msg.id} not found."))
      }

    case UpdateMovie(movie) => {
      movies.get(movie.id) match  {
        case Some(existingMovie) =>
          movies = movies + (movie.id -> movie)
          sender() ! Right(SuccessfulResponse(200,s"Movie with ID: ${movie.id} updated."))

        case None =>
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${movie.id} does not exists."))
      }
    }

    case DeleteMovie(id) => {}
      movies.get(id) match  {
        case Some(existingMovie) =>
          movies = movies.filter( _._1 != existingMovie.id)
          movies = movies - id
          sender() ! Right(SuccessfulResponse(206, s"Movie with ID: ${id} deleted."))

        case None =>
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${id} does not exists."))
      }
  }
}