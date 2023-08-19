package sqids

import munit.ScalaCheckSuite
import org.scalacheck.Prop._
import org.scalacheck.Gen

final class SqidsSuite_encoding extends ScalaCheckSuite {
  val sqids = Sqids.default
  test("simple") {
    val numbers = List(1, 2, 3);
    val id = "8QRLaD";
    assertEquals(sqids.encodeUnsafeString(numbers: _*), id)
  }

  test("different inputs") {
    val numbers =
      List(0, 0, 0, 1, 2, 3, 100, 1000, 100000, 1000000, sqids.maxValue)

    assertEquals(sqids.decode(sqids.encodeUnsafeString(numbers: _*)), numbers)
  }

  test("incremental numbers") {
    val ids = Map(
      "bV" -> List(0),
      "U9" -> List(1),
      "g8" -> List(2),
      "Ez" -> List(3),
      "V8" -> List(4),
      "ul" -> List(5),
      "O3" -> List(6),
      "AF" -> List(7),
      "ph" -> List(8),
      "n8" -> List(9)
    )
    ids.foreach { case (id, numbers) =>
      assertEquals(sqids.encodeUnsafeString(numbers: _*), id)
      assertEquals(sqids.decode(id), numbers)
    }
  }

  test("incremental numbers, same index 0") {
    val ids = List(
      "SrIu" -> List(0, 0),
      "nZqE" -> List(0, 1),
      "tJyf" -> List(0, 2),
      "e86S" -> List(0, 3),
      "rtC7" -> List(0, 4),
      "sQ8R" -> List(0, 5),
      "uz2n" -> List(0, 6),
      "7Td9" -> List(0, 7),
      "3nWE" -> List(0, 8),
      "mIxM" -> List(0, 9)
    )
    ids.foreach { case (id, numbers) =>
      assertEquals(sqids.encodeUnsafeString(numbers: _*), id)
      assertEquals(sqids.decode(id), numbers)
    }
  }
  test("incremental numbers, same index 1") {
    val ids = List(
      "SrIu" -> List(0, 0),
      "nbqh" -> List(1, 0),
      "t4yj" -> List(2, 0),
      "eQ6L" -> List(3, 0),
      "r4Cc" -> List(4, 0),
      "sL82" -> List(5, 0),
      "uo2f" -> List(6, 0),
      "7Zdq" -> List(7, 0),
      "36Wf" -> List(8, 0),
      "m4xT" -> List(9, 0)
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
      76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99)

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
  test("decoding an invalid ID with a repeating reserved character".ignore) {
    assertEquals(sqids.decode("fff"), List())
  }
  test("encode out-of-range numbers") {
    assertEquals(
      sqids.encode(List(sqids.minValue - 1)),
      Left(SqidsError.OutOfRange("some nr is out of range: List(-1), max: 2147483647 min: 0"))
    )

    assertEquals(
      sqids.encode(List(sqids.maxValue + 1)),
      Left(SqidsError.OutOfRange("some nr is out of range: List(-2147483648), max: 2147483647 min: 0"))
    )
  }

  property("arbitrary numbers") {
    forAll(Gen.choose(0, Int.MaxValue)) { (n: Int) =>
      assertEquals(sqids.decode(sqids.encodeUnsafeString(n)), List(n))
    }
  }
  property("arbitrary number lists") {
    forAll(Gen.listOf(Gen.choose(0, Int.MaxValue))) { (n: List[Int]) =>
      assertEquals(sqids.decode(sqids.encodeUnsafeString(n: _*)), n)
    }
  }
}
