package lectures.part1as

import scala.util.Try

object DarkSugars extends App {

  // syntax sugar #1: methods with single parameter
  def singleArgMethod(arg: Int): String = s"$arg little ducks..."

  val description = singleArgMethod {
    // write some code
    42
  }

  val aTryInstance = Try { // java try {...}
    throw new RuntimeException
  }

  List(1, 2, 3).map { x =>
    x + 1
  }

  // syntax sugar #2: single abstract method pattern
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  val aFunkyInstance: Action = (x: Int) => x + 1

  // example: Runnable
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Hello, Scala!") // valid Thread creation
  })

  // equivalent ^^^
  val aSweeterThread = new Thread(() => println("Sweet, Scala!"))

  abstract class AnAbstractType {
    def implemented: Int = 23

    def f(a: Int): Unit
  }

  // equivalent ^^^
  val anAbstractInstance: AnAbstractType = (a: Int) => println("sweet")

  // syntax sugar #3: the :: and #:: methods are special

  val prependedList = 2 :: List(3, 4)
  // equivalent ^^^ val prependedList = 2 +: List(3, 4)
  // 2.::(List(3, 4))
  // List(3, 4).::(2)
  // ?!

  //
  // scala spec: last char decides associativity of method
  1 :: 2 :: 3 :: List(4, 5)
  List(4, 5).::(1).::(2).::(3) // equivalent ^^^
  val appendedList = List(5, 6) :+ 7

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this // actual implementation
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int] // viable syntax

  // syntax sugar #4: multi-word method naming

  class TeenGirl(name: String) {
    def `and then said`(gossip: String): Unit = println(s"$name said $gossip")
  }

  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "Did you hear about Jenna?" // acceptable expression

  // syntax sugar #5: infix types
  class Composite[A, B]

  val composite: Composite[Int, String] = ???
  val secondComposite: Int Composite String = ???

  class -->[A, B]

  val towards: Int --> String = ??? // infix types

  // syntax sugar #6: update() is very special, much like apply()
  val anArray = Array(1, 2, 3)
  anArray(2) = 7 // rewritten to anArray.update(2, 7)
  // update() used in mutable collections
  // remember apply() and update()

  // syntax sugar #7: setters for mutable containers
  class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation

    def member: Int = internalMember // getter
    def member_=(value: Int): Unit = { // setter
      internalMember = value
    }

    val aMutableContainer = new Mutable
    aMutableContainer.member = 42 // rewritten as aMutableContainer.member_=(42)
  }

}
