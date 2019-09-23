package week3.TailRecursion

sealed trait GenericList[A] {
  def length(): Int = {
    def lengthOfGenericList(node: GenericList[A], cnt: Int): Int = node match {
      case GenericNode( i, end) => lengthOfGenericList(end, cnt+1)
      case GenericEnd() => cnt
      case _ => 0
    }
    lengthOfGenericList(this,0)
  }
  def map[B](f: A => B): GenericList[B] = {
    def changeGenericList(node: GenericList[A], f: A => B ) : GenericList[B] = node match {
      case GenericNode(i,end) => GenericNode(f(i), changeGenericList(end, f))
      case GenericEnd() => GenericEnd()
    }
    changeGenericList(this,f)
  }
}

case class GenericEnd[A]() extends GenericList[A]

case class GenericNode[A](i: A, end: GenericList[A]) extends GenericList[A]

object main extends App {

  val genericList: GenericList[Int] = GenericNode(1, GenericNode(2, GenericNode(3, GenericEnd())))
  println(genericList.length())
  println(genericList.map(x => x.toString() + "8"))
  assert(genericList.map(x => x + 8) == GenericNode(1 + 8, GenericNode(2 + 8, GenericNode(3 + 8, GenericEnd()))))
  assert(genericList.map(x => x.toString) == GenericNode("1", GenericNode("2", GenericNode("3", GenericEnd()))))
}