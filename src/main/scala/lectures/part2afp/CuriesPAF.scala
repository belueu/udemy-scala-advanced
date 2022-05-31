package lectures.part2afp

object CuriesPAF extends App {

  // curried functions
  val supperAdded: Int => Int => Int =
    x => y => x + y

  val add3 = supperAdded(3) // Int => Int = y => 3 + y
  println(add3(5))
  println(supperAdded(2)(5)) // curried function

  def curriedAdder(x: Int)(y: Int): Int = x + y // curried method

  val add4: Int => Int = curriedAdder(4)

  // lifting = ETA-EXPANSION

  // functions != methods (JVM limitation)
  def inc(x: Int): Int = x + 1

  List(1, 2, 3).map(inc) // ETA_EXPANSION

  // Partial function applications
  val add5 = curriedAdder(5) _ // do ETA-EXPANSION and convert to a function of Int => Int

  // EXERCISES
  val simpleAddFunction = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int): Int = x + y

  def curriedAddMethod(x: Int)(y: Int): Int = x + y

  // add7: Int => Int = y => 7 + y
  // as ,any different implementations of add7 using the above
  // be creative!

  val add7 = (x: Int) => simpleAddFunction(7, x)
  val add7_2 = simpleAddFunction.curried(7)

  val add7_3 = curriedAddMethod(7) _ // PAF
  val add7_4 = curriedAddMethod(7)(_) // PAF equivalent ^^^

  val add7_5 = simpleAddMethod(7, _: Int) // alternative syntax for turning methods into function values
  val add7_6 = simpleAddFunction(7, _: Int)

  // underscores are powerful
  def concatenation(a: String, b: String, c: String): String = a + b + c

  val insertName = concatenation("Hello, I'm ", _: String, ", how are you?")
  println(insertName("Cristian"))

  val fillInTheBlanks = concatenation("Hello, ", _: String, _: String)
  println(fillInTheBlanks("Cristian", ", Scala is awesome"))

  /*
    EXERCISES
    1. Process a list of numbers and return their string representation with different formats
       use %4.2f, %8.6f and %14.12f with a curried formatter function

    2. Difference between
       - functions vs methods
       - parameters: by-name vs 0-lambda
  * */

  def byName(n: => Int): Int = n + 1

  def byFunction(f: () => Int): Int = f() + 1

  def method: Int = 42

  def parenMethod(): Int = 42

  /*
    calling byName and byFunction
    - int
    - method
    - parenMethod
    - lambda
    - PAF
  * */

  // exercise #1
  def curriedFormatter(s: String)(number: Double): String = s.format(number)

  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  val simpleFormat = curriedFormatter("%4.2f") _ // lift
  val seriousFormat = curriedFormatter("%8.6f") _
  val preciseFormat = curriedFormatter("%14.12f") _

  println(numbers.map(simpleFormat))
  println(numbers.map(seriousFormat))
  println(numbers.map(preciseFormat))

  // exercise #2
  byName(23) // ok
  byName(method) // ok
  byName(parenMethod()) // ok
  byName(parenMethod) // ok but beware ==> byName(parenMethod())
  //  byName(() => 42) // nok
  byName((() => 42) ()) // ok if you call the lambda with "()"
  //  byName(parenMethod _) // nok
  //  byName(parenMethod() _) // nok

  //  byFunction(43) // nok
  //  byFunction(method) // nok
  byFunction(parenMethod) // ok - compiler does ETA-EXPANSION
  byFunction(() => 45) // ok
  byFunction(parenMethod _) // also works but '_' unnecessary
}
