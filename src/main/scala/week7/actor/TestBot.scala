package week7.actor

import akka.actor.{Actor,ActorLogging,ActorRef,Props}
import week7.model._

object TestBot {
  case object TestCreate
  case object TestConflict
  case object TestRead
  case object TestUpdate
  case object TestDelete
  case object TestNotFound

  def props(manager: ActorRef) = Props(new TestBot(manager))
}

class TestBot(manager: ActorRef) extends Actor with ActorLogging {
  import TestBot._

  override def receive: Receive = {
    case TestCreate =>
      manager ! MovieManager.CreateMovie(Movie("1","Joker",Director("dir-1","Askar",None,"Mussayev"),2014))

    case TestConflict =>
      manager ! MovieManager.CreateMovie(Movie("2","asd",Director("dir-2","firstname",None,"lastname"),1111))
      manager ! MovieManager.CreateMovie(Movie("2","asd",Director("dir-3","firstnamee",None,"lastename"),2003))

    case TestRead =>
      manager ! MovieManager.ReadMovie("2")

    case TestNotFound =>
      manager ! MovieManager.ReadMovie("3")

    case TestUpdate =>
      manager ! MovieManager.UpdateMovie(Movie("2","asd",Director("dir-2","firstname",None,"lastname"),2222))
//      manager ! MovieManager.ReadMovie("2")
//      manager ! MovieManager.ReadMovie("1")

    case TestDelete =>
      manager ! MovieManager.DeleteMovie("2")
      manager ! MovieManager.DeleteMovie("3")

    case SuccessfulResponse(status, msg) =>
       log.info("Received Successful Response with status: {} and message: {}",status,msg)

    case ErrorResponse(status,msg) =>
      log.warning("Received Error Response with status: {} and message: {} ",status,msg)

    case movie: Movie =>
      log.info("Received movie: [{}] ", movie)
  }
}