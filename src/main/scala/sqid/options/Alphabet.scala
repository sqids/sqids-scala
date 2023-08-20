package sqids.options

import scala.annotation.tailrec
import sqids.SqidsError

sealed abstract case class Alphabet(value: String) {
  def length = value.length
  def indexOf(c: Char) = value.indexOf(c.toInt)
  def prefix = value.head
  def partition = value(1)
  def removePrefixAndPartition: Alphabet = new Alphabet(value.drop(2)) {}
  def removeSeparator: Alphabet = new Alphabet(value.take(value.length - 1)) {}
  def separator: Char = value.last
  def splitAtSeparator(id: String): Either[String, (String, String)] =
    (id.takeWhile(_ != separator), id.dropWhile(_ != separator).tail) match {
      case (first, _) if first.exists(!removeSeparator.value.contains(_)) =>
        Left("First part have invalid characters")
      case res => Right(res)
    }

  def validId(id: String): Boolean =
    id.forall(c => value.contains(c))

  def toId(num: Long): String = {
    @tailrec
    def go(num: Long, acc: List[Char]): String =
      if (num <= 0) acc.mkString
      else
        go(num / length, value((num % length).toInt) :: acc)

    go(num / length, List(value((num % length).toInt)))
  }

  def toNumber(id: String): Long =
    id.foldLeft(0L)((acc, c) => acc * length + indexOf(c).toLong)

  def shuffle: Alphabet =
    new Alphabet(value.indices.take((length - 1).toInt).foldLeft(value) { (str, i) =>
      val j: Int = length - 1 - i
      val r: Int = (i * j + str(i) + str(j.toInt)) % length
      val iChar = str(i)
      str.updated(i, str(r)).updated(r, iChar)
    }) {}

  def offsetFromPrefix(prefix: Char) = value.indexOf(prefix.toInt)

  def getOffset(numbers: List[Long]): Int =
    numbers.indices.foldLeft(numbers.length) { (offset, i) =>
      offset + i + value(numbers(i).toInt % length)
    } % length

  def rearrange(offset: Int): Alphabet =
    new Alphabet(value.drop(offset) + value.take(offset)) {}

  def rearrange(numbers: List[Long]): Alphabet =
    rearrange(getOffset(numbers))
}

object Alphabet {
  def apply(value: String): Either[SqidsError, Alphabet] =
    value match {
      case v if v.distinct.length != v.length =>
        Left(SqidsError.AlphabetNotUnique)
      case v if v.length < 5 =>
        Left(SqidsError.AlphabetTooSmall)
      case v =>
        Right(new Alphabet(v) {})
    }

  def default: Alphabet =
    new Alphabet((('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).mkString) {}
}
