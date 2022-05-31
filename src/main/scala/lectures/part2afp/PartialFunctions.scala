package lectures.part2afp

import scala.io.Source.stdin

object PartialFunctions extends App {

  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] === Int => Int

  val aFussyFunction = (x: Int) => {
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException
  }

  class FunctionNotApplicableException extends RuntimeException

  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }
  // {1, 2, 5} => Int - a partial function from Int to Int

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // partial function value, and it's equivalent to function above

  println(aPartialFunction(2))
  //  println(aPartialFunction(57000))

  // Partial Function utilities
  println(aPartialFunction.isDefinedAt(57))

  // Partial functions can be lifted to total functions
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(98))

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 57
  }

  println(pfChain(2))
  println(pfChain(45))

  // PF extend normal functions
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // HOF's accept partial functions as well
  val aMappedList = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }
  println(aMappedList)

  /*
    Note: PF can only have ONE parameter type
  * */

  /**
   * Exercises
   *
   * 1 - construct a PF instance yourself (anonymous class)
   * 2 - dumb chatbot as a PF
  * */


  // PF construction
  val constructedPartialFunction = new PartialFunction[Int, Int] {
    override def isDefinedAt(x: Int): Boolean = {
      x == 1 || x == 2 || x ==5
    }

    override def apply(v1: Int): Int = v1 match {
      case 1 => 42
      case 2 => 56
      case 5 => 999
    }
  }

  val chatbot: PartialFunction[String, Any] = {
    case "hello" => "Hi, my name is HAL9000"
    case "goodbye" => "Once you start talking to me there is no return human"
    case "call mom" => "Unable to find your phone without your credit card"
    case "exit" => scala.io.Source.stdin.close()
    case _ => "Can you repeat please, I don't understand"
  }

  scala.io.Source.stdin.getLines().map(chatbot).foreach(println)
}
