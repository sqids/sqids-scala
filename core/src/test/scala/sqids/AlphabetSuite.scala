/*
 * Copyright 2023 Sqids
 *
 * SPDX-License-Identifier: MIT
 */

package sqids

import munit.ScalaCheckSuite

import sqids.options.Alphabet

class AlphabetSuite extends ScalaCheckSuite {
  test("simple") {
    val numbers: List[Long] = List(1, 2, 3)
    val id = "489158"
    Alphabet("0123456789abcdef")
      .flatMap(Sqids.forAlphabet)
      .foreach { sqids =>
        assertEquals(sqids.encode(numbers).map(_.value), Right(id))
        assertEquals(sqids.decode(id), numbers)
      }
  }

  test("short alphabet") {
    val numbers: List[Long] = List(1, 2, 3)
    Alphabet("abc")
      .flatMap(Sqids.forAlphabet)
      .foreach(sqids => assertEquals(sqids.decode(sqids.encodeUnsafeString(numbers: _*)), numbers))
  }

  test("long alphabet") {
    val numbers: List[Long] = List(1, 2, 3)
    Alphabet(
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-_+|{}[];:'\"/?.>,<`~"
    )
      .flatMap(Sqids.forAlphabet)
      .foreach(sqids => assertEquals(sqids.decode(sqids.encodeUnsafeString(numbers: _*)), numbers))
  }

  test("multibyte characters") {
    assertEquals(Alphabet("ë1092"), Left(SqidsError.AlphabetMultibyteChars))
  }

  test("repeating alphabet characters") {
    assertEquals(Alphabet("aabcdefg"), Left(SqidsError.AlphabetNotUnique))
  }

  test("too short of an alphabet") {
    assertEquals(Alphabet("ab"), Left(SqidsError.AlphabetTooSmall))
  }
}
