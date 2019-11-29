package Lab9.SprayJsonSerializer

import Lab9.model.{Company, ErrorResponse, Laptop, SuccessfulResponse,TelegramMessage}
import spray.json.DefaultJsonProtocol

trait SprayJsonSerializer extends DefaultJsonProtocol{
  implicit val companyFormat = jsonFormat3(Company)
  implicit val notebookFormat= jsonFormat6(Laptop)
  implicit val telegramMessageFormat = jsonFormat2(TelegramMessage)
  implicit val successfulResponseFormat = jsonFormat2(SuccessfulResponse)
  implicit val errorResponseFormat = jsonFormat2(ErrorResponse)
}
