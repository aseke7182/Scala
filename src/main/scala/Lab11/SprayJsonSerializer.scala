package Lab11

import spray.json.DefaultJsonProtocol
import Lab11.models._


trait SprayJsonSerializer extends DefaultJsonProtocol {

  implicit val successfulResponse = jsonFormat2(SuccessfulResponse)
  implicit val errorResponse = jsonFormat2(ErrorResponse)

}
