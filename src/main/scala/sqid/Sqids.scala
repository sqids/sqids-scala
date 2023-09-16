/*
 * Copyright 2023 Sqids
 *
 * SPDX-License-Identifier: MIT
 */

package sqids

import sqids.options.Alphabet
import sqids.options.Blocklist
import sqids.options.InvalidSqidsOptions
import sqids.options.SqidsOptions

trait Sqids {
  def encodeUnsafeString(numbers: Long*): String
  def encodeUnsafe(numbers: Long*): Sqid
  def encode(numbers: Long*): Either[SqidsError, Sqid]
  def encode(numbers: List[Long]): Either[SqidsError, Sqid]
  def decode(id: String): List[Long]
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

      override def encodeUnsafe(numbers: Long*): Sqid = encode(numbers: _*) match {
        case Left(value) => throw value
        case Right(value) => value
      }

      override def encodeUnsafeString(numbers: Long*): String = encode(numbers: _*) match {
        case Left(error) => throw error
        case Right(value) => value.value
      }

      override def encode(numbers: Long*): Either[SqidsError, Sqid] =
        encode(numbers.toList)

      override def encode(numbers: List[Long]): Either[SqidsError, Sqid] =
        encode_(numbers)

      private def minValue: Long = 0

      private def maxValue: Long = Long.MaxValue

      override def decode(input: String): List[Long] =
        input match {
          case "" => List.empty
          case id if !_alphabet.validId(id) => List.empty
          case id => Sqid(id).toNumbers(_alphabet)
        }

      private def encode_(
        numbers: List[Long]
      ): Either[SqidsError, Sqid] =
        numbers match {
          case numbers if numbers.exists(i => i > maxValue || i < minValue) =>
            val outOfRangeNumbers = numbers.filter(n => n > maxValue || n < minValue)
            Left(
              SqidsError.OutOfRange(
                s"some nr is out of range: ${outOfRangeNumbers.mkString(", ")}, max: $maxValue min: $minValue"
              )
            )
          case numbers =>
            Sqid
              .fromNumbers(
                numbers,
                _alphabet,
                options.minLength,
                options.blocklist
              )

        }
    }
  }
}
