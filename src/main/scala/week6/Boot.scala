package week6

import akka.actor.ActorSystem;
import week6.actor._
import week6.model._

object Boot extends App {
  val system = ActorSystem("movie-service")
  val movieManager = system.actorOf(MovieManager.props(), "movie-manager")
  val testBot = system.actorOf(TestBot.props(movieManager), "test-bot")
  val movie1 = Movie("3","Something",Director("dir-4","asd","fgh"),2019)
  val movie2 = Movie("4","Something",Director("dir-5","asd","fgh"),20195)
  // Create
  testBot ! TestBot.TestCreate
  testBot ! TestBot.TestConflict

  // For Fun
//  testBot ! "movie1"

  // Read
//  testBot ! TestBot.TestRead
//  testBot ! TestBot.TestNotFound

  // Update
  testBot ! TestBot.TestUpdate

  // Delete
  testBot ! TestBot.TestDelete

}
