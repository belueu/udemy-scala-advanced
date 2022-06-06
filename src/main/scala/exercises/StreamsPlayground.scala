package exercises

import scala.annotation.tailrec

/*
  Exercise: implement a lazily evaluated, singly linked STREAM of elements.

  naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers (potentially infinite!)
  naturals.take(100).foreach(println) // lazily evaluated stream of the first 100 naturals (finite stream)
  naturals.foreach(println) // will crash - infinite!
  naturals.map(_ * 2) // stream of all even numbers (potentially infinite!)
* */
abstract class MyStream[+A] {
  def isEmpty: Boolean

  def head: A

  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] // prepend operator

  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concatenate two streams

  def foreach(f: A => Unit): Unit

  def map[B](f: A => B): MyStream[B]

  def flatMap[B](f: A => MyStream[B]): MyStream[B]

  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // takes the first n elements out of this stream

  def takeAsList(n: Int): List[A] = take(n).toList()

  /*
    [1, 2, 3].toList([]) =
    [2, 3].toList([1]) =
    [3].toList([2, 1]) =
    [].toList([3, 2, 1]) =
    = [1, 2, 3]
  * */
  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] = {
    if (isEmpty) acc.reverse
    else tail.toList(head :: acc)
  }
}

object EmptyStream extends MyStream[Nothing] {
  override def isEmpty: Boolean = true

  override def head: Nothing = throw new NoSuchElementException()

  override def tail: MyStream[Nothing] = throw new NoSuchElementException()

  override def #::[B >: Nothing](element: B): MyStream[B] = new Cons[B](element, this)

  override def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  override def foreach(f: Nothing => Unit): Unit = ()

  override def map[B](f: Nothing => B): MyStream[B] = this

  override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

  override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  override def take(n: Int): MyStream[Nothing] = this
}

class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
  override def isEmpty: Boolean = false

  override val head: A = hd

  override lazy val tail: MyStream[A] = tl // call by need

  /*
  val someStream = new Cons(1, EmptyStream)
  val prepended = 1 #:: s = new Cons(1, s)
* */
  override def #::[B >: A](element: B): MyStream[B] = new Cons[B](element, this)

  override def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons(head, tail ++ anotherStream)

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  /*
    s = new Cons(1, ?)
    mapped = s.map(_ + 1) = new Cons(2, s.tail.map(_ + 1))
      ... mapped.tail
  * */
  override def map[B](f: A => B): MyStream[B] = new Cons[B](f(head), tail.map(f)) // preserves lazy evaluation

  override def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

  override def filter(predicate: A => Boolean): MyStream[A] = {
    if (predicate(head)) new Cons(head, tail.filter(predicate))
    else tail.filter(predicate) // preserves lazy evaluation
  }

  override def take(n: Int): MyStream[A] = {
    if (n <= 0) EmptyStream
    else if (n == 1) new Cons[A](head, EmptyStream)
    else new Cons[A](head, tail.take(n - 1))
  }
}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] = {
    new Cons[A](start, MyStream.from(generator(start))(generator))
  }
}

object StreamsPlayground extends App {

  val naturals = MyStream.from(1)(_ + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val startFromZero = 0 #:: naturals // naturals.#::(0)
  println(startFromZero.head)

  startFromZero.take(10000).foreach(println)

  // map, flatMap
  println(startFromZero.map(_ * 2).take(100).toList())
  println(startFromZero.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(10).toList())
  println(startFromZero.take(20).filter( _ < 10).toList())

  // Exercises on streams
  // 1 - stream of Fibonacci numbers
  // 2 - stream of prime numbers with Eratosthenes' sieve
  /*
    [2, 3, 4, ...]
    filter out all numbers % 2 == 0
    [2, 3, 5, 7, 9, 11, ...]
    filter out all numbers % 3 == 0
    [2, 3, 5, 7, 11, 13, 17, ...]
    filter out all numbers % 5 == 0
    ...
  * */

  /*
    [first, [ ...
    [first, fibonacci(second, first + second ...
  * */
  def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] = {
    new Cons[BigInt](first, fibonacci(second, first + second))
  }

  println(fibonacci(1, 1).take(100).toList())

  /*
    [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, ..., n]
    [2, 3, 5, 7, 9, 11, 13, ...]
    [2 eratosthenes applied to (numbers filtered by n % 2 != 0),....]
    [2, 3 eratosthenes applied to [5, 7, 9, 11, ...] filtered by n % 3 != 0, ...]
    [2, 3, 5 eratosthenes applied to [7, 11, 13, ...] filtered by n % 5 != 0, ...]

  * */
  // eratosthenes sieve
  def eratosthenes(numbers: MyStream[Int]): MyStream[Int] = {
    if (numbers.isEmpty) numbers
    else new Cons[Int](numbers.head, eratosthenes(numbers.tail.filter(n => n % numbers.head != 0)))
  }

  println(eratosthenes(MyStream.from(2)(_ + 1)).take(100).toList())
}
