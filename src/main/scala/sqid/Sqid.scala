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
    if (value.length < minLength) {
      val shuffled = alphabet.shuffle
      copy(
        value = value + shuffled.value.take(math.min(minLength - length, alphabet.length)),
        alphabet = shuffled
      ).fillToMinLength(minLength)
    } else this

  def shuffle = copy(alphabet = alphabet.shuffle)

  def handleBlocked(blocklist: Blocklist, minLength: Int): Either[SqidsError, Sqid] =
    if (blocklist.isBlocked(value) && increment <= alphabet.length)
      Sqid
        .fromNumbers(numbers, originalAlphabet, increment + 1)
        .handleMinLength(minLength)
        .handleBlocked(blocklist, minLength)
    else if (blocklist.isBlocked(value))
      Left(SqidsError.RegenerationMaxAttempts)
    else Right(this)

  def handleMinLength(minLength: Int): Sqid =
    if (minLength > length)
      copy(value + alphabet.separator).fillToMinLength(minLength)
    else this
}

object Sqid {
  def fromNumbers(
    numbers: List[Long],
    a: Alphabet,
    increment: Int = 0
  ): Sqid = {
    val alphabet = a.rearrange(numbers, increment)

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
        increment = increment,
        originalAlphabet = a
      )
    )
  }
}
