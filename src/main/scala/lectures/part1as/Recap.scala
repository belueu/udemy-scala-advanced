package lectures.part1as

import scala.annotation.tailrec

object Recap extends App {
  val aCondition: Boolean = false
  val aConditionedVal = if (aCondition) 42 else 55

  // instructions vs expressions

  // compiler infers types for us
  val aCodeBlock = {
    if (aCondition) 54
    56
  }

  // Unit is equivalent to Void
  val theUnit = println("Hello Scala")

  // functions
  def aFunction(x: Int): Int = {
    x + 1
  }

  // recursion: stack and tail  (@tailrec)
  @tailrec
  def factorial(n: Int, accumulator: Int): Int = {
    if (n <= 0) accumulator
    else factorial(n - 1, n * accumulator)
  }

  // object-oriented programming
  class Animal

  class Dog extends Animal

  val aDog: Animal = new Dog // oop polymorphism by subtyping

  trait Carnivore {
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("Crunch!")
  }

  // method notations
  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog // close to natural language

  // anonymous classes
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("roar")
  }

  aCarnivore.eat(aCroc)

  // generics
  abstract class MyList[+A] // variance and variance problems treated in THIS course

  // singleton objects and companions
  object MyList

  // case classes
  case class Person(name: String, age: Int)

  // exceptions and try/catch/finally

  //  val throwsException = throw new RuntimeException("Runtime exception") // This returns Nothing
  val aPotentialFailure = try {
    throw new RuntimeException()
  } catch {
    case e: Exception => "I caught an exception"
  } finally {
    println("Some logs")
  }

  // packaging and imports

  // functional programming

  val incrementer = new Function[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  println(incrementer(1))

  val anonymousIncrementer = (x: Int) => x + 1
  List(1, 2, 3).map(anonymousIncrementer) // higher-order function
  // map, flatMap, filter

  // for-comprehensions
  val pairs = for {
    num <- List(1, 2, 3) // if guards
    char <- List('a', 'b', 'c')
  } yield num + "-" + char

  // Scala collections: Seq, Array, List, Vector, Map, Tuple
  val aMap = Map(
    "Daniel" -> 789,
    "Jess" -> 555
  )

  // "collections": Option, Try
  val anOption = Some(2)

  // Pattern matching
  val x = 2
  x match {
    case 1 => "is first"
    case 2 => "is second"
    case 3 => "is third"
    case _ => x + " th"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _) => s"Hi my name is $n"
    case Person(_, a) => s"Hi, I am $a years old"
  }

}

