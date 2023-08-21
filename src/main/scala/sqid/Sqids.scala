/*
 * Copyright 2023 Sqids
 *
 * SPDX-License-Identifier: MIT
 */

package sqids

import scala.annotation.tailrec
import sqids.options.Alphabet
import sqids.options.Blocklist
import sqids.options.InvalidSqidsOptions
import sqids.options.SqidsOptions

trait Sqids {
  def encodeUnsafeString(numbers: Int*): String
  def encodeUnsafe(numbers: Int*): Sqid
  def encode(numbers: Int*): Either[SqidsError, Sqid]
  def encode(numbers: List[Int]): Either[SqidsError, Sqid]
  def decode(id: String): List[Int]
  def minValue: Int
  def maxValue: Int
}

object Sqids {
  def forAlphabet(a: Alphabet): Either[InvalidSqidsOptions, Sqids] =
    SqidsOptions.default.withAlphabet(a).map(Sqids.apply)

  def withMinLength(minLength: Int): Either[InvalidSqidsOptions, Sqids] =
    SqidsOptions.default.withMinLength(minLength).map(Sqids.apply)

  def withBlocklist(blocklist: Blocklist): Sqids =
    apply(
      SqidsOptions.default.withBlocklist(blocklist = blocklist)
    )

  def default: Sqids =
    apply(SqidsOptions.default)

  def apply(options: SqidsOptions): Sqids = {
    val _alphabet = options.alphabet.shuffle
    new Sqids {

      override def encodeUnsafe(numbers: Int*): Sqid = encode(numbers: _*) match {
        case Left(value) => throw value
        case Right(value) => value
      }

      override def encodeUnsafeString(numbers: Int*): String = encode(numbers: _*) match {
        case Left(error) => throw error
        case Right(value) => value.value
      }

      override def encode(numbers: Int*): Either[SqidsError, Sqid] =
        encode(numbers.toList)

      override def encode(numbers: List[Int]): Either[SqidsError, Sqid] =
        encode_(numbers)

      override def minValue: Int = 0

      override def maxValue: Int = Int.MaxValue

      override def decode(input: String): List[Int] =
        input match {
          case "" => List.empty
          case id if !_alphabet.validId(id) => List.empty
          case id => getNumbers(id.head, id.tail)
        }

      private def getNumbers(prefix: Char, id: String): List[Int] = {
        @tailrec
        def go(
          id: String,
          alphabet: Alphabet,
          acc: Vector[Int]
        ): List[Int] =
          if (id.isEmpty) acc.toList
          else
            alphabet.splitAtSeparator(id) match {
              case Left(_) => Nil
              case Right((first, rest)) =>
                go(rest, alphabet.shuffle, acc :+ alphabet.removeSeparator.toNumber(first))
            }

        val (alphabet, partitionIndex) = {
          val offset = _alphabet.offsetFromPrefix(prefix)
          val rearranged = _alphabet.rearrange(offset)
          val partition = rearranged.partition
          (rearranged.removePrefixAndPartition, id.indexOf(partition.toInt))
        }

        if (partitionIndex > 0 && partitionIndex < id.length - 1)
          go(id.drop(partitionIndex + 1), alphabet.shuffle, Vector.empty)
        else
          go(id, alphabet, Vector.empty)
      }

      private def encode_(
        numbers: List[Int]
      ): Either[SqidsError, Sqid] =
        numbers match {
          case numbers if numbers.exists(i => i > maxValue || i < minValue) =>
            Left(
              SqidsError.OutOfRange(
                s"some nr is out of range: ${numbers.filter(n => n > maxValue || n < minValue)}, max: $maxValue min: $minValue"
              )
            )
          case numbers =>
            Sqid
              .fromNumbers(numbers, _alphabet, false)
              .handleMinLength(options.minLength)
              .handleBlocked(options.blocklist, maxValue)
        }
    }
  }
}
