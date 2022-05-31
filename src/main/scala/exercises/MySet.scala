package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {

  /*
    Exercise - implement a functional set
  * */
  def apply(elem: A): Boolean = contains(elem)

  def contains(elem: A): Boolean

  def +(elem: A): MySet[A]

  def ++(anotherSet: MySet[A]): MySet[A] // union

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(predicate: A => Boolean): MySet[A]

  def foreach(f: A => Unit): Unit

  /*
    Exercise
    - removing an element
    - intersection with another set
    - difference with another set
  * */

  def -(elem: A): MySet[A] // remove element

  def --(anotherSet: MySet[A]): MySet[A] // difference

  def &(anotherSet: MySet[A]): MySet[A] // intersection

  // Exercise - implement a unary_! = NEGATION of a set
  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false

  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  // part 2 - Enhancements
  override def -(elem: A): MySet[A] = this

  override def --(anotherSet: MySet[A]): MySet[A] = this

  override def &(anotherSet: MySet[A]): MySet[A] = this

  override def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)

}

//class AllInclusiveSet[A] extends MySet[A] {
//  override def contains(elem: A): Boolean = true
//
//  override def +(elem: A): MySet[A] = this
//
//  override def ++(anotherSet: MySet[A]): MySet[A] = this
//
//  // naturals = AllInclusiveSet[Int] = all the natural numbers
//  // naturals.map(x => x % 3) => [0, 1, 2]
//  override def map[B](f: A => B): MySet[B] = ???
//
//  override def flatMap[B](f: A => MySet[B]): MySet[B] = ???
//
//  override def filter(predicate: A => Boolean): MySet[A] = ??? // property-based set
//
//  override def foreach(f: A => Unit): Unit = ???
//
//  override def -(elem: A): MySet[A] = ???
//
//  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
//
//  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)
//
//  override def unary_! : MySet[A] = new EmptySet[A]
//}

// all elements of type A which satisfy a property
// {x in A | property(x) }
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  override def contains(elem: A): Boolean = property(elem)

  // { x in A | property(x) } + element = { x in A | property(x) || x == element }
  override def +(elem: A): MySet[A] = {
    new PropertyBasedSet[A](x => property(x) || x == elem)
  }

  // { x in A | property(x) } ++ Set => { x in A | property(x) || Set contains x }
  override def ++(anotherSet: MySet[A]): MySet[A] = {
    new PropertyBasedSet[A](x => property(x) || anotherSet(x))
  }

  /**
   * all integers => (_ % 3) => [0 1 2] - is it truly finite?
   * */
  override def map[B](f: A => B): MySet[B] = politelyFail

  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

  override def foreach(f: A => Unit): Unit = politelyFail

  override def filter(predicate: A => Boolean): MySet[A] = {
    new PropertyBasedSet[A](x => property(x) && predicate(x))
  }

  override def -(elem: A): MySet[A] = filter(x => x != elem)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole!")
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean = {
    elem == head || tail.contains(elem)
  }

  override def +(elem: A): MySet[A] = {
    if (this.contains(elem)) this
    else new NonEmptySet[A](elem, this)
  }

  override def ++(anotherSet: MySet[A]): MySet[A] = {
    tail ++ anotherSet + head
  }

  override def map[B](f: A => B): MySet[B] = {
    tail.map(f) + f(head)
  }

  override def flatMap[B](f: A => MySet[B]): MySet[B] = {
    tail.flatMap(f) ++ f(head)
  }

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail.filter(predicate)
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  // part 2 - Enhancements
  override def -(elem: A): MySet[A] = {
    if (head == elem) tail
    else tail - elem + head
  }

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet) // intersection = filter

  // new operator
  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))
}

object MySet {
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] = {
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)
    }

    buildSet(values, new EmptySet[A])
  }
}

object MySetPlayground extends App {
  val s = MySet(1, 2, 3, 4)
  s.foreach(println)
  println("============")
  (s + 5).foreach(println)
  println("============")
  ((s + 5) ++ MySet(-1, -2) + 3).foreach(println)
  println("============")
  ((s + 5) ++ MySet(-1, -2) + 3).map(x => x * 10).foreach(println)
  println("============")
  ((s + 5) ++ MySet(-1, -2) + 3).map(x => x * 10).flatMap(x => MySet(x, x * 10)).foreach(println)
  println("============")
  ((s + 5) ++ MySet(-1, -2) + 3).map(x => x * 10).flatMap(x => MySet(x, x * 10)).filter(x => x % 3 == 0).foreach(println)


  // testing Enhancements
  val negative = !s // s.unary_! = all the naturals not equal to 1, 2, 3, 4
  println(negative(2))
  println(negative(5))

  val negativeEven = negative.filter(_ % 2 == 0)
  println(negativeEven(5))

  val negativeEven5 = negativeEven + 5 // all even numbers > 4 + 5

}

