package sqids.options

import scala.util.control.NoStackTrace
import scala.annotation.tailrec

final case class InvalidAlphabet(override val getMessage: String) extends RuntimeException with NoStackTrace

sealed abstract case class Alphabet(value: String) {
  def length = value.length
  def indexOf(c: Char) = value.indexOf(c.toInt)
  def prefix = value.head
  def partition = value(1)
  def removePrefixAndPartition: Alphabet = new Alphabet(value.drop(2)) {}
  def removeSeparator: Alphabet = new Alphabet(value.take(value.length - 1)) {}
  def separator: Char = value.last

  def toId(num: Int): String = {
    @tailrec
    def go(num: Int, acc: List[Char]): String =
      if (num <= 0) acc.mkString
      else
        go(num / length, value(num % length) :: acc)

    go(num / length, List(value(num % length)))
  }

  def toNumber(id: String): Int =
    id.foldLeft(0)((acc, c) => acc * length + indexOf(c))

  def shuffle: Alphabet =
    new Alphabet(value.indices.take(length - 1).foldLeft(value) { (str, i) =>
      val j = length - 1 - i
      val r = (i * j + str(i) + str(j)) % length
      val iChar = str(i)
      str.updated(i, str(r)).updated(r, iChar)
    }) {}

  def getOffset(numbers: List[Int]): Int =
    numbers.indices.foldLeft(numbers.length) { (offset, i) =>
      offset + i + value(numbers(i) % length)
    } % length

  def rearrange(offset: Int): Alphabet =
    new Alphabet(value.drop(offset) + value.take(offset)) {}

  def rearrange(numbers: List[Int]): Alphabet =
    rearrange(getOffset(numbers))
}

object Alphabet {
  def apply(value: String): Either[InvalidAlphabet, Alphabet] =
    value match {
      case v if v.distinct.length != v.length =>
        Left(InvalidAlphabet("Alphabet must contain unique characters"))
      case v if v.length < 5 =>
        Left(InvalidAlphabet("Alphabet must contain more than 5 characters"))
      case v =>
        Right(new Alphabet(v) {})
    }

  def default: Alphabet =
    new Alphabet((('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).mkString) {}
}
