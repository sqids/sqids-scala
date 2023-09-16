/*
 * Copyright 2023 Sqids
 *
 * SPDX-License-Identifier: MIT
 */

package sqids

import scala.annotation.tailrec
import sqids.options.Alphabet
import sqids.options.Blocklist

final case class Sqid(value: String) {
  def prefix = value.head
  def toNumbers(_alphabet: Alphabet): List[Long] = {
    @tailrec
    def go(
      id: String,
      alphabet: Alphabet,
      acc: Vector[Long]
    ): List[Long] =
      if (id.isEmpty) acc.toList
      else
        alphabet.splitAtSeparator(id) match {
          case Left(_) => Nil
          case Right(("", _)) => acc.toList
          case Right((first, rest)) =>
            go(rest, alphabet.shuffle, acc :+ alphabet.removeSeparator.toNumber(first))
        }

    val offset = _alphabet.offsetFromPrefix(prefix)

    go(value.tail, _alphabet.rearrange(offset).reverse, Vector.empty)
  }
}

object Sqid {
  private final case class SqidEncodeState(
    value: String,
    alphabet: Alphabet,
    numbers: List[Long],
    increment: Int,
    originalAlphabet: Alphabet
  ) {
    override def toString =
      value

    def withNextnr(nr: Long) =
      append(alphabet.removeSeparator.toId(nr))

    def addSeparator =
      append(alphabet.separator.toString)

    def addPartitionOrSeparator(partition: String, shouldAddPartition: Boolean) =
      if (shouldAddPartition) append(partition)
      else addSeparator

    def append(s: String) =
      copy(value = value + s)

    def length = value.length

    def fillToMinLength(minLength: Int): SqidEncodeState =
      if (value.length < minLength) {
        val shuffled = alphabet.shuffle
        copy(
          value = value + shuffled.value.take(math.min(minLength - length, alphabet.length)),
          alphabet = shuffled
        ).fillToMinLength(minLength)
      } else this

    def shuffle =
      copy(alphabet = alphabet.shuffle)

    def handleBlocked(blocklist: Blocklist, minLength: Int): Either[SqidsError, SqidEncodeState] =
      if (blocklist.isBlocked(value) && increment <= alphabet.length)
        Sqid
          .getEncodeState(numbers, originalAlphabet, increment + 1)
          .handleMinLength(minLength)
          .handleBlocked(blocklist, minLength)
      else if (blocklist.isBlocked(value))
        Left(SqidsError.RegenerationMaxAttempts)
      else Right(this)

    def handleMinLength(minLength: Int): SqidEncodeState =
      if (minLength > length)
        copy(value + alphabet.separator).fillToMinLength(minLength)
      else this

    def toSqid = Sqid(value)
  }

  private def getEncodeState(
    numbers: List[Long],
    a: Alphabet,
    increment: Int
  ): SqidEncodeState = {
    @tailrec
    def go(
      numbers: List[Long],
      sqid: SqidEncodeState,
    ): SqidEncodeState =
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

    val alphabet = a.rearrange(numbers, increment)

    go(
      numbers = numbers,
      sqid = SqidEncodeState(
        value = alphabet.prefix.toString,
        alphabet = alphabet.reverse,
        numbers = numbers,
        increment = increment,
        originalAlphabet = a
      )
    )
  }

  def fromNumbers(
    numbers: List[Long],
    a: Alphabet,
    minLength: Int,
    blocklist: Blocklist
  ): Either[SqidsError, Sqid] =
    getEncodeState(numbers, a, 0)
      .handleMinLength(minLength)
      .handleBlocked(blocklist, minLength)
      .map(_.toSqid)
}
