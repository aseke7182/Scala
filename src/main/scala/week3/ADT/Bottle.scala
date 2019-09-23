package week3.ADT

sealed trait Source

case class Well() extends Source
case class Spring() extends Source
case class Tap() extends Source

case class Bottle(size: Int, carbonated: Boolean, source: Source )