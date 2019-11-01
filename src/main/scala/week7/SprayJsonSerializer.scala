package week7

import spray.json.DefaultJsonProtocol
import week7.model.{Director, ErrorResponse, Movie, SuccessfulResponse}

// DefaultJsonProtocol is responsible for default formats:
// Int, Double, String, ....

trait SprayJsonSerializer extends DefaultJsonProtocol {
  // custom formats
  implicit val successfulResponse = jsonFormat2(SuccessfulResponse)
  implicit val errorResponse = jsonFormat2(ErrorResponse)
  implicit val directorFormat = jsonFormat4(Director)
  implicit val movieFormat = jsonFormat4(Movie)
//  implicit val moviesFormat = jsonFormat2(Map[String,Movie])

}