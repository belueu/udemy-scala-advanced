package lectures.part1as

object AdvancedPatternMatching extends App {

  val numbers = List(1)

  val description = numbers match {
    case head :: Nil => println(s"the only element is $head")
    case _ =>
  }

  /*
    - constants
    - wildcards
    - case classes
    - tuples
    - some special magic like above
  * */

  class Person(val name: String, val age: Int)

  object Person { // singleton object
    def unapply(person: Person): Option[(String, Int)] = Some(person.name, person.age)

    def unapply(age: Int): Option[String] = Some(if (age < 21) "minor" else "adult")
  }

  val bob = new Person("Bob", 25)

  val greeting = bob match {
    case Person(n, a) => if (a <= 25) s"Hi my name is $n, and I am $a years old"
  }

  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is: $status"
  }

  println(legalStatus)

  /*
    Exercise.
  * */

  val n: Int = 45
  val mathProperty = n match {
    case x if x < 10 => "single digit"
    case x if x % 2 == 0 => "an even number"
    case _ => "no property"
  }

  // Exercise implementation
  object even {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit {
    def unapply(arg: Int): Boolean= arg > -10 && arg < 10
  }

  object odd {
    def unapply(arg: Int): Boolean = arg % 2 != 0
  }

  val number: Int = 6

  val matchProperty = number match {
    case singleDigit() => "single digit"
    case even() => "an even number"
    case odd() => "an odd number"
    case _ => "no match property"
  }

  println(matchProperty)


}
