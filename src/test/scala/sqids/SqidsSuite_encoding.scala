/*
 * Copyright 2023 Sqids
 *
 * SPDX-License-Identifier: MIT
 */

package sqids

import munit.ScalaCheckSuite
import org.scalacheck.Prop._
import org.scalacheck.Gen

final class SqidsSuite_encoding extends ScalaCheckSuite {
  val sqids = Sqids.default
  test("simple") {
    val numbers: List[Long] = List(1, 2, 3)
    val id = "86Rf07";
    assertEquals(sqids.encodeUnsafeString(numbers: _*), id)
  }

  test("different inputs") {
    val numbers =
      List(0, 0, 0, 1, 2, 3, 100, 1000, 100000, 1000000, Long.MaxValue)

    assertEquals(sqids.decode(sqids.encodeUnsafeString(numbers: _*)), numbers)
  }

  test("incremental numbers") {
    val ids = Map(
      "bM" -> List(0L),
      "Uk" -> List(1L),
      "gb" -> List(2L),
      "Ef" -> List(3L),
      "Vq" -> List(4L),
      "uw" -> List(5L),
      "OI" -> List(6L),
      "AX" -> List(7L),
      "p6" -> List(8L),
      "nJ" -> List(9L)
    )
    ids.foreach { case (id, numbers) =>
      assertEquals(sqids.encodeUnsafeString(numbers: _*), id)
      assertEquals(sqids.decode(id), numbers)
    }
  }

  test("incremental numbers, same index 0") {
    val ids = List(
      "SvIz" -> List(0L, 0L),
      "n3qa" -> List(0L, 1L),
      "tryF" -> List(0L, 2L),
      "eg6q" -> List(0L, 3L),
      "rSCF" -> List(0L, 4L),
      "sR8x" -> List(0L, 5L),
      "uY2M" -> List(0L, 6L),
      "74dI" -> List(0L, 7L),
      "30WX" -> List(0L, 8L),
      "moxr" -> List(0L, 9L)
    )
    ids.foreach { case (id, numbers) =>
      assertEquals(sqids.encodeUnsafeString(numbers: _*), id)
      assertEquals(sqids.decode(id), numbers)
    }
  }
  test("incremental numbers, same index 1") {
    val ids = List(
      "SvIz" -> List(0L, 0L),
      "nWqP" -> List(1L, 0L),
      "tSyw" -> List(2L, 0L),
      "eX68" -> List(3L, 0L),
      "rxCY" -> List(4L, 0L),
      "sV8a" -> List(5L, 0L),
      "uf2K" -> List(6L, 0L),
      "7Cdk" -> List(7L, 0L),
      "3aWP" -> List(8L, 0L),
      "m2xn" -> List(9L, 0L)
    )
    ids.foreach { case (id, numbers) =>
      assertEquals(sqids.encodeUnsafeString(numbers: _*), id)
      assertEquals(sqids.decode(id), numbers)
    }
  }
  test("multi input") {
    val numbers = List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
      24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
      50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75,
      76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99).map(
      _.toLong
    )

    val output = sqids.decode(sqids.encodeUnsafeString(numbers: _*))
    assertEquals(output, numbers)
  }

  test("encoding no numbers") {
    assertEquals(sqids.encodeUnsafeString(), "")
  }
  test("decoding empty string") {
    assertEquals(sqids.decode(""), List())
  }
  test("decoding an ID with an invalid character") {
    assertEquals(sqids.decode("*"), List())
  }

  test("encode out-of-range numbers") {
    assertEquals(
      sqids.encode(List(-1L)),
      Left(SqidsError.OutOfRange("some nr is out of range: -1, max: 9223372036854775807 min: 0"))
    )

    assertEquals(
      sqids.encode(List(Long.MaxValue + 1)),
      Left(
        SqidsError.OutOfRange(
          "some nr is out of range: -9223372036854775808, max: 9223372036854775807 min: 0"
        )
      )
    )
  }

  property("arbitrary numbers") {
    forAll(Gen.choose(0L, Long.MaxValue)) { (n: Long) =>
      assertEquals(sqids.decode(sqids.encodeUnsafeString(n)), List(n))
    }
  }
  property("arbitrary number lists") {
    forAll(Gen.listOf(Gen.choose(0L, Long.MaxValue))) { (n: List[Long]) =>
      assertEquals(sqids.decode(sqids.encodeUnsafeString(n: _*)), n)
    }
  }
}
