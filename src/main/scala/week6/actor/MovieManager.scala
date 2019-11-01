package week6.actor

import akka.actor.{Actor,ActorLogging,Props}
import week6.model._

object MovieManager {

    case class CreateMovie(movie: Movie)
    case class ReadMovie(id: String)
    case class UpdateMovie(movie: Movie)
    case class DeleteMovie(id: String)

    def props() = Props(new MovieManager)
}

class MovieManager extends Actor with ActorLogging {
  import MovieManager._
  var movies: Map[String,Movie] = Map()


  override def receive: Receive = {

      case CreateMovie(movie) => {

          movies.get(movie.id) match {

              case Some(existingMovie) =>
//                  log.warning(s"Could not create a movie with ID: ${movie.id} because it already exists.")
                  sender() ! ErrorResponse(409, s"Movie with ID: ${movie.id} already exists")

              case None =>
                  movies = movies + (movie.id -> movie)
//                  log.info("Movie with ID: {} created.", movie.id)
                  sender() ! SuccessfulResponse(201, s"Movie with ID: ${movie.id} created.")
          }
      }

      case ReadMovie(id) => {
        movies.get(id) match {

          case Some(existingMovie) =>
             sender() ! existingMovie
          case None =>
            sender() ! ErrorResponse(409, s"Movie with ID: ${id} does not exists")
        }
      }

      case UpdateMovie(movie) => {
        movies.get(movie.id) match  {
          case Some(existingMovie) =>
            movies = movies + (movie.id -> movie)
            sender() ! SuccessfulResponse(201,s"Movie with ID: ${movie.id} updated.")

          case None =>
            sender() ! ErrorResponse(409, s"Movie with ID: ${movie.id} does not exists.")
        }
      }

      case DeleteMovie(id) => {}
      movies.get(id) match  {
        case Some(existingMovie) =>
          movies = movies.filter( _._1 != existingMovie.id)
          movies = movies - id
          sender() ! SuccessfulResponse(206, s"Movie with ID: ${id} deleted.")

        case None =>
          sender() ! ErrorResponse(409, s"Movie with ID: ${id} does not exists.")
      }
  }

}