package week3.Shaping

sealed trait Shape {
//  def name: String
  def Sides: Int
  def Perimeter: Double
  def Area: Double
}

sealed trait Rectangular {
  this: Shape =>
}

case class Circle(radius: Double) extends Shape {
  override def Area: Double = Math.PI * radius * radius
  override def Perimeter: Double = 2 * Math.PI * radius
  override def Sides: Int = 0
}

case class Rectangle(width: Double, height: Double) extends Rectangular with Shape {
  override def Area: Double = width * height
  override def Perimeter: Double = (width + height) * 2
  override def Sides: Int = 4
}

case class Square(length: Double) extends Rectangular with Shape {
  override def Area: Double = length * length
  override def Perimeter: Double = 4 * length
  override def Sides: Int = 4
}

object Draw extends App {
  def apply(shape: Shape): Unit = {
    val area = shape.Area
    val perimeter = shape.Perimeter
    val numberOfSides = shape.Sides
    val sentence = s"Area: $area cm, Perimeter: $perimeter cm, Number of Sides: $numberOfSides,"
    shape match {
      case Circle(radius) => println(s"$sentence, radius: $radius cm")
      case Rectangle(width,height) => println(s"$sentence, width: $width cm, height: $height cm")
      case Square(length) => println(s"$sentence, length: $length cm")
      case _ => println("This is not Shape")
    }
  }

  Draw(Circle(5))
  Draw(Rectangle(5,4))
  Draw(Square(10))
}