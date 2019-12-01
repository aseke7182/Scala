package Adi.Lab9

import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import Adi.Lab9.model.{ErrorResponse, SuccessfulResponse}
import Adi.Lab9.model.{President, Country}


trait SprayJsonSerializer extends  DefaultJsonProtocol {
  implicit val presidentFormat: RootJsonFormat[President] = jsonFormat3(President)
  implicit val countryFormat: RootJsonFormat[Country] = jsonFormat4(Country)
  implicit val successfulResponse: RootJsonFormat[SuccessfulResponse] = jsonFormat2(SuccessfulResponse)
  implicit val errorResponse: RootJsonFormat[ErrorResponse] = jsonFormat2(ErrorResponse)
}