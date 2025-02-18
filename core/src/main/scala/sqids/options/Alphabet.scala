/*
 * Copyright 2023 Sqids
 *
 * SPDX-License-Identifier: MIT
 */

package sqids.options

import scala.annotation.tailrec
import sqids.SqidsError
import sqids.Utils.StringOps

sealed abstract case class Alphabet(value: Vector[Char]) {
  def length = value.length
  def indexOf(c: Char) = value.indexOf(c.toInt)
  def prefix = value.head
  def separator: Char = value.head
  def removeSeparator: Alphabet = new Alphabet(value.tail) {}
  def splitAtSeparator(id: String): Either[String, (String, String)] =
    (id.takeWhile(_ != separator), id.dropWhile(_ != separator).tailOrEmpty) match {
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
  def fillToMinLength(id: String, minLength: Int): String =
    id + value.take(math.min(minLength - id.length, length)).mkString

  def toNumber(id: String): Long =
    id.foldLeft(0L)((acc, c) => acc * length + indexOf(c).toLong)

  def shuffle: Alphabet =
    new Alphabet(
      value.indices
        .take((length - 1).toInt)
        .foldLeft(value) { (vec, i) =>
          val j: Int = length - 1 - i
          val r: Int = (i * j + vec(i) + vec(j.toInt)) % length
          val iChar = vec(i)
          vec
            .updated(i, vec(r))
            .updated(r, iChar)
        }
    ) {}

  def offsetFromPrefix(prefix: Char) = value.indexOf(prefix.toInt)

  def getOffset(numbers: List[Long], increment: Int): Int =
    (numbers.indices.foldLeft(numbers.length) { (offset, i) =>
      offset + i + value((numbers(i) % length.toLong).toInt)
    } % length) + increment

  def rearrange(offset: Int): Alphabet =
    new Alphabet(value.drop(offset) ++ value.take(offset)) {}

  def rearrange(numbers: List[Long], increment: Int): Alphabet =
    rearrange(getOffset(numbers, increment))

  def reverse: Alphabet = new Alphabet(value.reverse) {}
}

object Alphabet {
  def apply(value: String): Either[SqidsError, Alphabet] =
    value match {
      case v if v.distinct.length != v.length =>
        Left(SqidsError.AlphabetNotUnique)
      case v if v.length < 3 =>
        Left(SqidsError.AlphabetTooSmall)
      case v if v.getBytes.length != v.length =>
        Left(SqidsError.AlphabetMultibyteChars)
      case v =>
        Right(new Alphabet(v.toVector) {})
    }

  def default: Alphabet =
    new Alphabet((('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toVector) {}
}
