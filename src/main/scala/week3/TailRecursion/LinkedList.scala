package week3.TailRecursion

sealed trait IntList {
  def length(): Int = {
    @scala.annotation.tailrec
    def lengthOfList(node: IntList, cnt: Int): Int = node match {
      case Node( _ , tail ) => lengthOfList(tail,cnt+1)
      case End => cnt
      case _ => 0
    }
    lengthOfList(this,0)
  }

  def product(): Int = {
    @scala.annotation.tailrec
    def productOfList(node: IntList, product: Int): Int = node match {
      case Node(head, tail) => productOfList(tail,product * head)
      case End => product
      case _ => 0
    }
    productOfList(this, 1)
  }

  def double(): IntList = {
    def doubleListValue(node: IntList): IntList = node match {
      case Node( head, tail) => Node( head * 2 , doubleListValue(tail))
      case End => End
    }
    doubleListValue(this)
  }

  def map(f: Int => Int) : IntList = {
    def DoFunction(node: IntList, f: Int => Int) : IntList = node match {
      case Node ( head, tail) => Node ( f(head), DoFunction(tail,f))
      case End => End
    }
    DoFunction(this,f)
  }
}

case object End extends IntList

case class Node(head: Int, tail: IntList) extends IntList

object LinkedList extends App {

  val intList = Node(1, Node(2, Node(3, Node(4, End))))

  assert(intList.length == 4)
  assert(intList.tail.length == 3)
  assert(End.length == 0)

  assert(intList.product == 1 * 2 * 3 * 4)
  assert(intList.tail.product == 2 * 3 * 4)
  assert(End.product == 1)

  assert(intList.double == Node(1 * 2, Node(2 * 2, Node(3 * 2, Node(4 * 2, End)))))
  assert(intList.tail.double == Node(4, Node(6, Node(8, End))))
  assert(End.double == End)

  assert(intList.map(x => x * 3) == Node(1 * 3, Node(2 * 3, Node(3 * 3, Node(4 * 3, End)))))
  assert(intList.map(x => 5 - x) == Node(5 - 1, Node(5 - 2, Node(5 - 3, Node(5 - 4, End)))))

}