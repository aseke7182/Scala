package week3.ADT

sealed trait Calculator

case class Succeed(result: Int) extends Calculator

case class Fail(message: String) extends Calculator