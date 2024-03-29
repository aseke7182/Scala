package Lab12.model

sealed trait Response

case class SuccessfulResponse(status: Int, message: String) extends Response
case class ErrorResponse(status: Int, message: String) extends Response
case class PhotoResponse(status: Int, message: Array[Byte]) extends Response