package Lab11.models

trait Response

case class SuccessfulResponse(status: Int, message: String) extends Response
case class ErrorResponse(status: Int, message: String) extends Response
