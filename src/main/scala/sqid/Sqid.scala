/*
 * Copyright 2023 Sqids
 *
 * SPDX-License-Identifier: MIT
 */

package sqids

import scala.annotation.tailrec
import sqids.options.Alphabet
import sqids.options.Blocklist

final case class Sqid(
  value: String,
  alphabet: Alphabet,
  numbers: List[Long],
  increment: Int,
  originalAlphabet: Alphabet
) {
  override def toString = value
  def withNextnr(nr: Long) = append(alphabet.removeSeparator.toId(nr))
  def addSeparator = append(alphabet.separator.toString)
  def addPartitionOrSeparator(partition: String, shouldAddPartition: Boolean) =
    if (shouldAddPartition) append(partition)
    else addSeparator
  def append(s: String) = copy(value = value + s)
  def length = value.length
  def fillToMinLength(minLength: Int): Sqid =
    copy(value =
      value.head.toString +
        alphabet.value.take(minLength - length) +
        value.drop(1).take(length)
    )
  def shuffle = copy(alphabet = alphabet.shuffle)

  def handleBlocked(blocklist: Blocklist, maxValue: Long): Either[SqidsError, Sqid] = ???
  //   if (blocklist.isBlocked(value)) {
  //     val newNumbers: Either[SqidsError, List[Long]] =
  //       if (partitioned)
  //         if (numbers.head + 1L > maxValue)
  //           Left(SqidsError.OutOfRange("Ran out of range checking against the blocklist"))
  //         else
  //           Right(numbers.head + 1L :: numbers.tail)
  //       else
  //         Right(0L :: numbers)

  //     newNumbers.flatMap(numbers =>
  //       Sqid
  //         .fromNumbers(numbers, originalAlphabet, true)
  //         .handleBlocked(blocklist, maxValue)
  //     )
  //   } else Right(this)

  def handleMinLength(minLength: Int): Sqid = ???
  //   if (length < minLength)
  //     if (!partitioned)
  //       Sqid
  //         .fromNumbers(0 :: numbers, originalAlphabet, true)
  //         .handleMinLength(minLength)
  //     else
  //       fillToMinLength(minLength)
  //   else this
}

object Sqid {
  def fromNumbers(
    numbers: List[Long],
    a: Alphabet
  ): Sqid = {
    val alphabet = a.rearrange(numbers)

    @tailrec
    def go(
      numbers: List[Long],
      sqid: Sqid,
    ): Sqid =
      numbers match {
        case Nil => sqid.copy(value = "")
        case List(nr) => sqid.withNextnr(nr)
        case nr :: next =>
          go(
            numbers = next,
            sqid = sqid
              .withNextnr(nr)
              .addSeparator
              .shuffle
          )
      }

    go(
      numbers = numbers,
      sqid = Sqid(
        value = alphabet.prefix.toString,
        alphabet = alphabet.reverse,
        numbers = numbers,
        increment = 0,
        originalAlphabet = a
      )
    )
  }
}
