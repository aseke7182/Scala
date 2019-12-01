package Adi.Lab10

import Adi.Lab9.model.{Country, ErrorResponse, SuccessfulResponse}
import akka.actor.{Actor, ActorLogging, Props}

object CountryManager {

  case class CreateCountry(country: Country)

  case class ReadCountry(id: String)

  case class UpdateCountry(country: Country)

  case class DeleteCountry(id: String)

  def props() = Props(new CountryManager)
}

class CountryManager extends Actor with ActorLogging {
  import CountryManager._

  var countries: Map[String, Country] = Map()

  override def receive: Receive = {

    case CreateCountry(country) =>
      countries.get(country.id) match {
        case Some(existingCountry) =>
          log.error(s"Could not create country with ID: ${country.id} because it already exists.")
          sender() ! Left(ErrorResponse(409, s"Movie with ID: ${country.id} already exists."))

        case None =>
          countries += (country.id -> country)
          log.info("Created country with ID: {}.", country.id)
          sender() ! Right(SuccessfulResponse(201, s"Created country with ID: ${country.id}."))
      }

    case msg: ReadCountry =>
      countries.get(msg.id) match {
        case Some(country) =>
          sender() ! Right(country)

        case None =>
          log.error(s"Country with ID: ${msg.id} not found.")
          sender() ! Left(ErrorResponse(404, s"Country with ID: ${msg.id} not found."))
      }

    case UpdateCountry(country) =>
      countries.get(country.id) match {
        case Some(existingCountry) =>
          countries += (country.id -> country)
          log.info(s"Updated movie with ID: ${country.id}.")
          sender() ! Right(SuccessfulResponse(200, s"Updated country with ID: ${country.id}."))

        case None =>
          log.error(s"Could not update country. Country with ID: ${country.id} not found")
          sender() ! Left(ErrorResponse(404, s"Country with ID: ${country.id} not found."))
      }

    case msg: DeleteCountry =>
      countries.get(msg.id) match {
        case Some(country) =>
          countries -=  country.id
          log.info(s"Destroyed country with ID: ${country.id}.")
          sender() ! Right(SuccessfulResponse(204, s"Destroyed country with ID: ${country.id}."))

        case None =>
          log.error(s"Could not destroy country. Country with ID: ${msg.id} not found")
          sender() ! Left(ErrorResponse(404, s"Country with ID: ${msg.id} not found."))
      }
  }

}