package Adi.Lab6.actor

import akka.actor.{Actor, ActorLogging, Props}
import Lab6.model.{Movie, ErrorResponse, SuccessfulResponse}

object MovieManager {

  case class CreateMovie(movie: Movie)

  case class ReadMovie(id: String)

  case class UpdateMovie(movie: Movie)

  case class DeleteMovie(id: String)

  def props() = Props(new MovieManager)
}

class MovieManager extends Actor with ActorLogging {
  import MovieManager._

  var movies: Map[String, Movie] = Map()

  override def receive: Receive = {

    case CreateMovie(movie) =>
      movies.get(movie.id) match {
        case Some(existingMovie) =>
          log.error(s"Could not create movie with ID: ${movie.id} because it already exists.")
          sender() ! Left(ErrorResponse(409, s"Movie with ID: ${movie.id} already exists."))

        case None =>
          movies = movies + (movie.id -> movie)
          log.info("Created movie with ID: {}.", movie.id)
          sender() ! Right(SuccessfulResponse(201, s"Created movie with ID: ${movie.id}."))
      }

    case msg: ReadMovie =>
      movies.get(msg.id) match {
        case Some(movie) =>
          sender() ! Right(movie)

        case None =>
          log.error(s"Movie with ID: ${msg.id} not found.")
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${msg.id} not found."))
      }

    case UpdateMovie(movie) =>
      movies.get(movie.id) match {
        case Some(existingMovie) =>
          movies += (movie.id -> movie)
          log.info(s"Updated movie with ID: ${movie.id}.")
          sender() ! Right(SuccessfulResponse(200, s"Updated movie with ID: ${movie.id}."))

        case None =>
          log.error(s"Could not update movie. Movie with ID: ${movie.id} not found")
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${movie.id} not found."))
      }

    case msg: DeleteMovie =>
      movies.get(msg.id) match {
        case Some(movie) =>
          movies -=  movie.id
          log.info(s"Deleted movie with ID: ${movie.id}.")
          sender() ! Right(SuccessfulResponse(204, s"Deleted movie with ID: ${movie.id}."))

        case None =>
          log.error(s"Could not delete movie. Movie with ID: ${msg.id} not found")
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${msg.id} not found."))
      }
  }
}