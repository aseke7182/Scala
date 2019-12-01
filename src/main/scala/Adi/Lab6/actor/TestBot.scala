package Adi.Lab6.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import Lab6.model.{Director, Movie, ErrorResponse, SuccessfulResponse}

object TestBot {

  case object TestCreate

  case object TestConflict

  case object TestRead

  case object TestNotFound

  case object TestUpdate

  case object TestDelete

  def props(manager: ActorRef) = Props(new TestBot(manager))
}

class TestBot(manager: ActorRef) extends Actor with ActorLogging {
  import TestBot._

  override def receive: Receive = {
    case TestCreate =>
      manager ! MovieManager.CreateMovie(Movie("1", "Joker", Director("dir-1", "Todd", None, "Philips"), 2019))
      manager ! MovieManager.CreateMovie(Movie("2", "Lord of the Rings", Director("dir-2", "Peter", None, "Jackson"), 2003))
      manager ! MovieManager.CreateMovie(Movie("3", "Star Wars", Director("dir-3", "George", Some("P"), "Lucas"), 1977))

    case TestConflict =>
      manager ! MovieManager.CreateMovie(Movie("2", "Alien", Director("dir-1", "Ridley", None,  "Scott"), 1979))
      manager ! MovieManager.CreateMovie(Movie("3", "Avatar", Director("dir-3", "James", None, "Cameron"), 2010))

    case TestRead =>
      manager ! MovieManager.ReadMovie("1")
      manager ! MovieManager.ReadMovie("2")

    case TestNotFound =>
      manager ! MovieManager.ReadMovie("4")
      manager ! MovieManager.UpdateMovie(Movie("35", "Avatar", Director("dir-3", "James", Some("P"), "Cameron"), 2010))

    case TestUpdate =>
      manager ! MovieManager.UpdateMovie(Movie("3", "Avatar 2", Director("dir-3", "James", None, "Cameron"), 2020))

    case TestDelete =>
      manager ! MovieManager.DeleteMovie("2")
      manager ! MovieManager.DeleteMovie("3")

    case SuccessfulResponse(status, msg) =>
      log.info("Received Successful Response with status: {} and message: {}", status, msg)

    case ErrorResponse(status, msg) =>
      log.warning("Received Error Response with status: {} and message: {}", status, msg)

    case movie: Movie =>
      log.info("Received movie: [{}]", movie)
  }
}