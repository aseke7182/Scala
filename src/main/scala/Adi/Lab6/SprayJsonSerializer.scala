package Adi.Lab6

import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import Lab6.model.{Director, ErrorResponse, Movie, SuccessfulResponse}

trait SprayJsonSerializer extends DefaultJsonProtocol {
  implicit val directorFormat: RootJsonFormat[Director] = jsonFormat4(Director)
  implicit val movieFormat: RootJsonFormat[Movie] = jsonFormat4(Movie)

  implicit val errorResponse: RootJsonFormat[ErrorResponse] = jsonFormat2(ErrorResponse)
  implicit val successfulResponse: RootJsonFormat[SuccessfulResponse] = jsonFormat2(SuccessfulResponse)
}