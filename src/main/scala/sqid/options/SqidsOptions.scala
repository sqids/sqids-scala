/*
 * Copyright 2023 Sqids
 *
 * SPDX-License-Identifier: MIT
 */

package sqids.options

import scala.util.control.NoStackTrace

final case class InvalidSqidsOptions(override val getMessage: String)
  extends RuntimeException
  with NoStackTrace
sealed abstract case class SqidsOptions(
  alphabet: Alphabet,
  minLength: Int,
  blocklist: Blocklist
) {
  def withBlocklist(blocklist: Blocklist): SqidsOptions = new SqidsOptions(
    alphabet,
    minLength,
    blocklist.filter(alphabet)
  ) {}

  def withAlphabet(alphabet: Alphabet): Either[InvalidSqidsOptions, SqidsOptions] = SqidsOptions.apply(
    alphabet,
    minLength,
    blocklist
  )

  def withMinLength(minLength: Int): Either[InvalidSqidsOptions, SqidsOptions] =
    SqidsOptions.apply(
      alphabet,
      minLength,
      blocklist
    )
}

object SqidsOptions {
  val MinLengthLimit = 255
  def apply(
    alphabet: Alphabet,
    minLength: Int,
    blocklist: Blocklist
  ): Either[InvalidSqidsOptions, SqidsOptions] =
    if (minLength < 0)
      Left(InvalidSqidsOptions("minLength need to be > 0"))
    else if (minLength > MinLengthLimit)
      Left(InvalidSqidsOptions(s"minLength cant be larger than $MinLengthLimit"))
    else
      Right(
        new SqidsOptions(
          alphabet,
          minLength,
          blocklist.filter(alphabet)
        ) {}
      )

  def default: SqidsOptions = new SqidsOptions(
    alphabet = Alphabet.default,
    minLength = 0,
    blocklist = Blocklist.default
  ) {}
}
